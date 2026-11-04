package com.vamshi.HospitalManagementSystem.appointment.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vamshi.HospitalManagementSystem.appointment.entities.AppointmentEntity;
import com.vamshi.HospitalManagementSystem.common.enums.AppointmentStatus;

@Repository
public interface AppointmentRepository extends JpaRepository<AppointmentEntity, UUID> {

    List<AppointmentEntity> findByPatientId(UUID patientId);

    List<AppointmentEntity> findByDoctorId(UUID doctorId);

    List<AppointmentEntity> findByDoctorIdAndStatus(UUID doctorId, AppointmentStatus status);

    List<AppointmentEntity> findByPatientIdAndDoctorId(UUID patientId, UUID doctorId);

    long countByScheduledAtBetween(LocalDateTime start, LocalDateTime end);
}
