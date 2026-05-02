package com.medicareplus.controller;

import jakarta.servlet.annotation.WebServlet;

@WebServlet("/appointment/*")
public class AppointmentServlet extends PatientController {
    private static final long serialVersionUID = 1L;
}
