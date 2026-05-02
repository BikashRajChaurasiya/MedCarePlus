package com.medicareplus.service;

import com.medicareplus.DAO.PatientDAO;
import com.medicareplus.model.Patient;
import java.sql.SQLException;

public class PatientService {
    private final PatientDAO patientDAO = new PatientDAO();
    public boolean addPatient(Patient patient) throws SQLException, ClassNotFoundException { return patientDAO.add(patient); }
    public Patient getPatientByUserId(int userId) throws SQLException, ClassNotFoundException { return patientDAO.findByUserId(userId); }
    public Patient getPatientById(int patientId) throws SQLException, ClassNotFoundException { return patientDAO.findById(patientId); }
}
