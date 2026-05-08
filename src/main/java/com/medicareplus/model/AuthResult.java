package com.medicareplus.model;

public class AuthResult {
	public enum Status {
		SUCCESS, INVALID_CREDENTIALS, LOCKED, INACTIVE, ROLE_MISMATCH
	}

	private final Status status;
	private final User user;
	private final String message;

	private AuthResult(Status status, User user, String message) {
		this.status = status;
		this.user = user;
		this.message = message;
	}

	public static AuthResult success(User user) {
		return new AuthResult(Status.SUCCESS, user, "Login successful.");
	}

	public static AuthResult failure(Status status, String message) {
		return new AuthResult(status, null, message);
	}

	public Status getStatus() {
		return status;
	}

	public User getUser() {
		return user;
	}

	public String getMessage() {
		return message;
	}

	public boolean isSuccess() {
		return status == Status.SUCCESS;
	}
}
