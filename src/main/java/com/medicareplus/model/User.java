package com.medicareplus.model;

import java.sql.Timestamp;

public class User {
	private int userId;
	private String fullName;
	private String email;
	private String password;
	private String role;
	private boolean active = true;
	private int failedAttempts;
	private Timestamp accountLockedUntil;
	private Timestamp createdAt;

	public User() {
	}

	public User(int userId, String fullName, String email, String password, String role, Timestamp createdAt) {
		this.userId = userId;
		this.fullName = fullName;
		this.email = email;
		this.password = password;
		this.role = role;
		this.createdAt = createdAt;
	}

	public User(String fullName, String email, String password, String role) {
		this.fullName = fullName;
		this.email = email;
		this.password = password;
		this.role = role;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getId() {
		return userId;
	}

	public void setId(int id) {
		this.userId = id;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getName() {
		return fullName;
	}

	public void setName(String name) {
		this.fullName = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public int getFailedAttempts() {
		return failedAttempts;
	}

	public void setFailedAttempts(int failedAttempts) {
		this.failedAttempts = failedAttempts;
	}

	public Timestamp getAccountLockedUntil() {
		return accountLockedUntil;
	}

	public void setAccountLockedUntil(Timestamp accountLockedUntil) {
		this.accountLockedUntil = accountLockedUntil;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}
}
