package com.medicareplus.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public final class EncryptionUtil {
	private EncryptionUtil() {
	}

	public static String sha256(String value) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
			StringBuilder sb = new StringBuilder();
			for (byte b : hash)
				sb.append(String.format("%02x", b));
			return sb.toString();
		} catch (Exception e) {
			throw new IllegalStateException("Unable to hash value", e);
		}
	}
}
