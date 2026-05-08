package com.medicareplus.util;

import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PasswordUtil {
	private static final int ITERATIONS = 120_000;
	private static final int KEY_LENGTH = 256;
	private static final int SALT_BYTES = 16;
	private static final SecureRandom RANDOM = new SecureRandom();

	private PasswordUtil() {
	}

	public static String hashPassword(String password) {
		byte[] salt = new byte[SALT_BYTES];
		RANDOM.nextBytes(salt);
		byte[] hash = pbkdf2(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
		return "PBKDF2$" + ITERATIONS + "$" + Base64.getEncoder().encodeToString(salt) + "$"
				+ Base64.getEncoder().encodeToString(hash);
	}

	public static boolean verifyPassword(String password, String storedHash) {
		if (storedHash == null || password == null) {
			return false;
		}

		if (!storedHash.startsWith("PBKDF2$")) {
			return storedHash.equals(password) || storedHash.equals(EncryptionUtil.sha256(password));
		}

		String[] parts = storedHash.split("\\$");
		if (parts.length != 4) {
			return false;
		}

		int iterations = Integer.parseInt(parts[1]);
		byte[] salt = Base64.getDecoder().decode(parts[2]);
		byte[] expected = Base64.getDecoder().decode(parts[3]);
		byte[] actual = pbkdf2(password.toCharArray(), salt, iterations, expected.length * 8);
		return constantTimeEquals(expected, actual);
	}

	public static boolean isModernHash(String storedHash) {
		return storedHash != null && storedHash.startsWith("PBKDF2$");
	}

	private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLength) {
		try {
			KeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
			return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
		} catch (Exception e) {
			throw new IllegalStateException("Unable to hash password", e);
		}
	}

	private static boolean constantTimeEquals(byte[] a, byte[] b) {
		if (a.length != b.length) {
			return false;
		}
		int result = 0;
		for (int i = 0; i < a.length; i++) {
			result |= a[i] ^ b[i];
		}
		return result == 0;
	}
}
