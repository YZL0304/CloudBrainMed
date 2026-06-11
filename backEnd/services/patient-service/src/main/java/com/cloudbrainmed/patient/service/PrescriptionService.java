package com.cloudbrainmed.patient.service;

import com.cloudbrainmed.patient.entity.Prescription;

import java.util.List;

public interface PrescriptionService {
    List<Prescription> getByRegisterId(String registerId);
    List<Prescription> getByPatientId(String patientId);
}
