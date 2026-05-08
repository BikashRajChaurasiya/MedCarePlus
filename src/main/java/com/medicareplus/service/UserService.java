package com.medicareplus.service;

import com.medicareplus.DAO.PatientDAO;
import com.medicareplus.DAO.UserDAO;
import com.medicareplus.model.AuthResult;
import com.medicareplus.model.PasswordResetToken;
import com.medicareplus.model.Patient;
import com.medicareplus.model.User;
import com.medicareplus.util.EncryptionUtil;
import com.medicareplus.util.PasswordUtil;
import com.medicareplus.util.SecurityConfig;
import com.medicareplus.util.ValidationUtil;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Base64;
import java.util.List;

public class UserService {
	private static final String LOCK_MESSAGE = "Your account has been temporarily locked due to multiple failed login attempts. Please try again later or use Forgot Password.";
	private static final SecureRandom RANDOM = new SecureRandom();

	private final UserDAO userDAO = new UserDAO();
	private final PatientDAO patientDAO = new PatientDAO();

	public boolean registerUser(User user) throws SQLException, ClassNotFoundException {
		validateUser(user);
		if (userDAO.emailExists(user.getEmail()))
			return false;
		user.setPassword(PasswordUtil.hashPassword(user.getPassword()));
		return userDAO.register(user);
	}

	public boolean registerPatient(User user, Patient patient) throws SQLException, ClassNotFoundException {
		user.setRole("patient");
		boolean created = registerUser(user);
		if (!created)
			return false;
		patient.setUserId(user.getUserId());
		return patientDAO.add(patient);
	}

	public AuthResult authenticate(String email, String password, String selectedRole)
			throws SQLException, ClassNotFoundException {
		return authenticate(email, password, selectedRole, null);
	}

	public AuthResult authenticate(String email, String password, String selectedRole, String ipAddress)
			throws SQLException, ClassNotFoundException {

		if (!ValidationUtil.isValidEmail(email) || !ValidationUtil.hasText(password)) {
			userDAO.recordLoginAttempt(null, email, false, "INVALID_INPUT", ipAddress);
			return AuthResult.failure(AuthResult.Status.INVALID_CREDENTIALS, "Invalid email or password.");
		}

		User user = userDAO.findByEmail(email);
		if (user == null) {
			userDAO.recordLoginAttempt(null, email, false, "UNKNOWN_ACCOUNT", ipAddress);
			return AuthResult.failure(AuthResult.Status.INVALID_CREDENTIALS, "Invalid email or password.");
		}

		if (!user.isActive()) {
			userDAO.recordLoginAttempt(user.getUserId(), email, false, "INACTIVE", ipAddress);
			return AuthResult.failure(AuthResult.Status.INACTIVE,
					"This account is inactive. Please contact the administrator.");
		}

		if (isLocked(user)) {
			userDAO.recordLoginAttempt(user.getUserId(), email, false, "LOCKED", ipAddress);
			return AuthResult.failure(AuthResult.Status.LOCKED, LOCK_MESSAGE);
		}

		if (!PasswordUtil.verifyPassword(password, user.getPassword())) {
			userDAO.recordFailedAttempt(user.getUserId(), SecurityConfig.MAX_FAILED_LOGIN_ATTEMPTS,
					SecurityConfig.ACCOUNT_LOCK_MINUTES);
			User updated = userDAO.findById(user.getUserId());
			if (updated != null && isLocked(updated)) {
				userDAO.recordLoginAttempt(user.getUserId(), email, false, "LOCKED_AFTER_FAILURES", ipAddress);
				return AuthResult.failure(AuthResult.Status.LOCKED, LOCK_MESSAGE);
			}
			userDAO.recordLoginAttempt(user.getUserId(), email, false, "BAD_PASSWORD", ipAddress);
			return AuthResult.failure(AuthResult.Status.INVALID_CREDENTIALS, "Invalid email or password.");
		}

		if (selectedRole != null && !selectedRole.isBlank() && !selectedRole.equalsIgnoreCase(user.getRole())) {
			userDAO.recordLoginAttempt(user.getUserId(), email, false, "ROLE_MISMATCH", ipAddress);
			return AuthResult.failure(AuthResult.Status.ROLE_MISMATCH, "Selected role does not match this account.");
		}

		if (!PasswordUtil.isModernHash(user.getPassword())) {
			userDAO.updatePassword(user.getUserId(), PasswordUtil.hashPassword(password));
			user = userDAO.findById(user.getUserId());
		} else {
			userDAO.resetFailedAttempts(user.getUserId());
		}

		userDAO.recordLoginAttempt(user.getUserId(), email, true, null, ipAddress);
		return AuthResult.success(user);
	}

	public User loginUser(String email, String password) throws SQLException, ClassNotFoundException {
		AuthResult result = authenticate(email, password, null);
		return result.isSuccess() ? result.getUser() : null;
	}

	public String createPasswordResetToken(String email) throws SQLException, ClassNotFoundException {
		User user = userDAO.findByEmail(email);
		if (user == null || !user.isActive()) {
			return null;
		}

		String token = generateSecureToken();
		userDAO.invalidateResetTokens(user.getUserId());
		userDAO.createResetToken(user.getUserId(), hashToken(token), SecurityConfig.RESET_TOKEN_MINUTES);
		return token;
	}

	public boolean resetPassword(String token, String newPassword) throws SQLException, ClassNotFoundException {
		if (!ValidationUtil.isValidPassword(newPassword) || !ValidationUtil.hasText(token)) {
			return false;
		}

		PasswordResetToken resetToken = userDAO.findValidResetToken(hashToken(token));
		if (resetToken == null) {
			return false;
		}

		User user = userDAO.findById(resetToken.getUserId());
		if (user == null || PasswordUtil.verifyPassword(newPassword, user.getPassword())) {
			return false;
		}

		boolean updated = userDAO.updatePassword(user.getUserId(), PasswordUtil.hashPassword(newPassword));
		if (updated) {
			userDAO.markResetTokenUsed(resetToken.getTokenId());
		}
		return updated;
	}

	public boolean updatePassword(String email, String newPassword) throws SQLException, ClassNotFoundException {
		if (!ValidationUtil.isValidPassword(newPassword)) {
			return false;
		}
		return userDAO.updatePassword(email, PasswordUtil.hashPassword(newPassword));
	}

	public User getUserByEmail(String email) throws SQLException, ClassNotFoundException {
		return userDAO.findByEmail(email);
	}

	public User getUserById(int userId) throws SQLException, ClassNotFoundException {
		return userDAO.findById(userId);
	}

	public List<User> getAllUsers() throws SQLException, ClassNotFoundException {
		return userDAO.findAll();
	}

	public boolean deleteUser(int userId) throws SQLException, ClassNotFoundException {
		return userDAO.delete(userId);
	}

	public boolean emailExists(String email) throws SQLException, ClassNotFoundException {
		return userDAO.emailExists(email);
	}

	public int getUserCount() throws SQLException, ClassNotFoundException {
		return userDAO.count();
	}

	public boolean isValidEmail(String email) {
		return ValidationUtil.isValidEmail(email);
	}

	public boolean isValidPassword(String password) {
		return ValidationUtil.isValidPassword(password);
	}

	private boolean isLocked(User user) {
		Timestamp lockedUntil = user.getAccountLockedUntil();
		return lockedUntil != null && lockedUntil.toInstant().isAfter(Instant.now());
	}

	private String generateSecureToken() {
		byte[] bytes = new byte[32];
		RANDOM.nextBytes(bytes);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
	}

	private String hashToken(String token) {
		return EncryptionUtil.sha256(token);
	}

	private void validateUser(User user) {
		if (!ValidationUtil.hasText(user.getFullName()))
			throw new IllegalArgumentException("Full name is required");
		if (!ValidationUtil.isValidEmail(user.getEmail()))
			throw new IllegalArgumentException("Valid email is required");
		if (!ValidationUtil.isValidPassword(user.getPassword()))
			throw new IllegalArgumentException("Password must be at least 6 characters");
	}
}
