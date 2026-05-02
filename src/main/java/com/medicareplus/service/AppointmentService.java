package com.medicareplus.service;

import com.medicareplus.DAO.AppointmentDAO;
import com.medicareplus.DAO.MedicalRecordDAO;
import com.medicareplus.model.Appointment;
import com.medicareplus.model.MedicalRecord;
import java.sql.SQLException;
import java.util.List;

public class AppointmentService {
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final MedicalRecordDAO medicalRecordDAO = new MedicalRecordDAO();

    public boolean bookAppointment(Appointment appointment) throws SQLException, ClassNotFoundException {
        if (!appointmentDAO.isSlotAvailable(appointment.getDoctorId(), appointment.getAppointmentDate(), appointment.getAppointmentTime()))
            return false;
        return appointmentDAO.add(appointment);
    }

    public List<Appointment> getAppointmentsByPatientId(int patientId) throws SQLException, ClassNotFoundException {
        return appointmentDAO.findByPatientId(patientId);
    }

    public List<Appointment> getAppointmentsByDoctorId(int doctorId) throws SQLException, ClassNotFoundException {
        return appointmentDAO.findByDoctorId(doctorId);
    }

    public List<Appointment> getAllAppointments() throws SQLException, ClassNotFoundException {
        return appointmentDAO.findAll();
    }

    public boolean updateAppointmentStatus(int appointmentId, String status) throws SQLException, ClassNotFoundException {
        return appointmentDAO.updateStatus(appointmentId, status);
    }

    public Appointment getAppointmentById(int appointmentId) throws SQLException, ClassNotFoundException {
        return appointmentDAO.findById(appointmentId);
    }

    public boolean addMedicalRecord(MedicalRecord record) throws SQLException, ClassNotFoundException {
        return medicalRecordDAO.add(record);
    }

    public List<MedicalRecord> getMedicalRecordsByPatientId(int patientId) throws SQLException, ClassNotFoundException {
        return medicalRecordDAO.findByPatientId(patientId);
    }

    public List<MedicalRecord> getMedicalRecordsByDoctorId(int doctorId) throws SQLException, ClassNotFoundException {
        return medicalRecordDAO.findByDoctorId(doctorId);
    }

    public int getAppointmentCount() throws SQLException, ClassNotFoundException {
        return appointmentDAO.count();
    }

    public int getPendingAppointmentCount() throws SQLException, ClassNotFoundException {
        return appointmentDAO.countPending();
    }

    public boolean isSlotAvailable(int doctorId, String date, String time) throws SQLException, ClassNotFoundException {
        return appointmentDAO.isSlotAvailable(doctorId, date, time);
    }
}