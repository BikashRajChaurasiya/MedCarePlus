package com.medicareplus.service;

import com.medicareplus.DAO.PatientDAO;
import com.medicareplus.model.Patient;
import java.sql.SQLException;
import java.util.List;

public class PatientService {
	private final PatientDAO patientDAO = new PatientDAO();

	public boolean addPatient(Patient patient) throws SQLException, ClassNotFoundException {
		return patientDAO.add(patient);
	}

	public Patient getPatientByUserId(int userId) throws SQLException, ClassNotFoundException {
		return patientDAO.findByUserId(userId);
	}

	public Patient getPatientById(int patientId) throws SQLException, ClassNotFoundException {
		return patientDAO.findById(patientId);
	}

	public List<Patient> getAllPatients() throws SQLException, ClassNotFoundException {
		return patientDAO.findAll();
	}

	public List<Patient> searchPatients(String keyword) throws SQLException, ClassNotFoundException {
		return patientDAO.search(keyword);
	}

}
