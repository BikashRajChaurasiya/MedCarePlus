package com.medicareplus.service;

import com.medicareplus.DAO.DoctorDAO;
import com.medicareplus.model.Doctor;
import java.sql.SQLException;
import java.util.List;

public class DoctorService {
    private final DoctorDAO doctorDAO = new DoctorDAO();

    public boolean addDoctor(Doctor doctor) throws SQLException, ClassNotFoundException {
        return doctorDAO.add(doctor);
    }

    public List<Doctor> getAllDoctors() throws SQLException, ClassNotFoundException {
        return doctorDAO.findAll();
    }

    public List<Doctor> getAvailableDoctors() throws SQLException, ClassNotFoundException {
        return doctorDAO.findAvailable();
    }

    public Doctor getDoctorById(int doctorId) throws SQLException, ClassNotFoundException {
        return doctorDAO.findById(doctorId);
    }

    public Doctor getDoctorByUserId(int userId) throws SQLException, ClassNotFoundException {
        return doctorDAO.findByUserId(userId);
    }

    public List<Doctor> getDoctorsBySpecialization(String specialization) throws SQLException, ClassNotFoundException {
        return doctorDAO.searchBySpecialization(specialization);
    }

    public boolean updateDoctor(Doctor doctor) throws SQLException, ClassNotFoundException {
        return doctorDAO.update(doctor);
    }

    public boolean deleteDoctor(int doctorId) throws SQLException, ClassNotFoundException {
        return doctorDAO.delete(doctorId);
    }

    public int getDoctorCount() throws SQLException, ClassNotFoundException {
        return doctorDAO.count();
    }
}