package com.medicareplus.util;

public final class SecurityConfig {
	public static final int MAX_FAILED_LOGIN_ATTEMPTS = Integer.getInteger("medicare.auth.maxAttempts", 3);
	public static final int ACCOUNT_LOCK_MINUTES = Integer.getInteger("medicare.auth.lockMinutes", 15);
	public static final int SESSION_TIMEOUT_SECONDS = Integer.getInteger("medicare.auth.sessionTimeoutSeconds",
			30 * 60);
	public static final int RESET_TOKEN_MINUTES = Integer.getInteger("medicare.auth.resetTokenMinutes", 15);

	private SecurityConfig() {
	}
}
