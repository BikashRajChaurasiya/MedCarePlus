package com.medicareplus.model;

public class Doctor {
	private int doctorId;
	private int userId;
	private String name;
	private String specialization;
	private String qualification;
	private int experienceYears;
	private double consultationFee;
	private String availabilityStatus;
	private String contactPhone;

	public Doctor() {
	}

	public Doctor(int userId, String specialization, String qualification, int experienceYears, double consultationFee,
			String availabilityStatus, String contactPhone) {
		this.userId = userId;
		this.specialization = specialization;
		this.qualification = qualification;
		this.experienceYears = experienceYears;
		this.consultationFee = consultationFee;
		this.availabilityStatus = availabilityStatus;
		this.contactPhone = contactPhone;
	}

	public int getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(int doctorId) {
		this.doctorId = doctorId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSpecialization() {
		return specialization;
	}

	public void setSpecialization(String specialization) {
		this.specialization = specialization;
	}

	public String getQualification() {
		return qualification;
	}

	public void setQualification(String qualification) {
		this.qualification = qualification;
	}

	public int getExperienceYears() {
		return experienceYears;
	}

	public void setExperienceYears(int experienceYears) {
		this.experienceYears = experienceYears;
	}

	public double getConsultationFee() {
		return consultationFee;
	}

	public void setConsultationFee(double consultationFee) {
		this.consultationFee = consultationFee;
	}

	public String getAvailabilityStatus() {
		return availabilityStatus;
	}

	public void setAvailabilityStatus(String availabilityStatus) {
		this.availabilityStatus = availabilityStatus;
	}

	public String getAvailability() {
		return availabilityStatus;
	}

	public void setAvailability(String availability) {
		this.availabilityStatus = availability;
	}

	public String getContactPhone() {
		return contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

	public String getContact() {
		return contactPhone;
	}

	public void setContact(String contact) {
		this.contactPhone = contact;
	}
}
