package com.vamshi.HospitalManagementSystem.pharmacy.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vamshi.HospitalManagementSystem.common.utils.AuthUtil;
import com.vamshi.HospitalManagementSystem.exceptions.ResourceAlreadyExistsException;
import com.vamshi.HospitalManagementSystem.exceptions.ResourceNotFoundException;
import com.vamshi.HospitalManagementSystem.inventory.entities.MedicineEntity;
import com.vamshi.HospitalManagementSystem.inventory.repositories.InventoryRepository;
import com.vamshi.HospitalManagementSystem.inventory.services.InventoryService;
import com.vamshi.HospitalManagementSystem.pharmacy.dtos.DispenseItemRequest;
import com.vamshi.HospitalManagementSystem.pharmacy.dtos.DispenseItemResponse;
import com.vamshi.HospitalManagementSystem.pharmacy.dtos.DispenseMedicineRequest;
import com.vamshi.HospitalManagementSystem.pharmacy.dtos.DispenseResponse;
import com.vamshi.HospitalManagementSystem.pharmacy.entities.DispenseEntity;
import com.vamshi.HospitalManagementSystem.pharmacy.repositories.DispenseRepository;
import com.vamshi.HospitalManagementSystem.prescription.entities.PrescriptionEntity;
import com.vamshi.HospitalManagementSystem.prescription.repositories.PrescriptionRepository;
import com.vamshi.HospitalManagementSystem.user.entities.UserEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PharmacyServiceImpl implements PharmacyService {

        private final DispenseRepository dispenseRepository;
        private final InventoryService inventoryService;
        private final InventoryRepository inventoryRepository;
        private final PrescriptionRepository prescriptionRepository;
        private final AuthUtil authUtil;

        @Override
        @Transactional
        public DispenseResponse dispenseMedicine(DispenseMedicineRequest request) {
                UserEntity pharmacist = authUtil.getLoggedInUser();

                PrescriptionEntity prescription = prescriptionRepository
                                .findById(request.getPrescriptionId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Prescription not found"));

                if (dispenseRepository.existsByPrescriptionId(
                                prescription.getId())) {
                        throw new ResourceAlreadyExistsException(
                                        "Prescription already dispensed");
                }

                double totalAmount = 0.0;

                List<DispenseEntity> dispenseList = new ArrayList<>();

                for (DispenseItemRequest item : request.getItems()) {
                        MedicineEntity medicine = inventoryRepository
                                        .findById(item.getMedicineId())
                                        .orElseThrow(() -> new ResourceNotFoundException(
                                                        "Medicine not found: "
                                                                        + item.getMedicineId()));

                        inventoryService.consumeMedicine(medicine.getId(), item.getQuantityDispensed());

                        double itemTotal = medicine.getPrice()
                                        * item.getQuantityDispensed();

                        totalAmount += itemTotal;

                        dispenseList.add(DispenseEntity.builder()
                                        .prescription(prescription)
                                        .patient(prescription.getPatient())
                                        .dispensedBy(pharmacist)
                                        .medicine(medicine)
                                        .quantityDispensed(item.getQuantityDispensed())
                                        .totalAmount(itemTotal)
                                        .notes(request.getNotes())
                                        .build());

                }

                List<DispenseEntity> savedList = dispenseRepository.saveAll(dispenseList);

                List<DispenseItemResponse> itemResponses = savedList
                                .stream()
                                .map((DispenseEntity saved) -> DispenseItemResponse.builder()
                                                .dispenseId(saved.getId()) // ← ID exists now
                                                .medicineId(saved.getMedicine().getId())
                                                .medicineName(saved.getMedicine()
                                                                .getMedicineName())
                                                .quantityDispensed(
                                                                saved.getQuantityDispensed())
                                                .pricePerUnit(saved.getMedicine().getPrice())
                                                .totalPrice(saved.getTotalAmount())
                                                .build())
                                .toList();
                // Step 6 — return full response with bill
                return DispenseResponse.builder()
                                .prescriptionId(prescription.getId())
                                .patientId(prescription.getPatient().getId())
                                .patientName(prescription.getPatient().getName())
                                .dispensedById(pharmacist.getId())
                                .dispensedByName(pharmacist.getName())
                                .items(itemResponses)
                                .totalAmount(totalAmount)
                                .notes(request.getNotes())
                                .dispensedAt(LocalDateTime.now())
                                .build();

        }

        // ── Get By Patient ───────────────────────────────────
        @Override
        public List<DispenseResponse> getDispenseHistoryByPatient(
                        UUID patientId) {

                List<DispenseEntity> history = dispenseRepository
                                .findByPatientIdOrderByDispensedAtDesc(patientId);

                return history.stream()
                                .map(this::mapToResponse)
                                .toList();
        }

        // ── Get By Pharmacist ────────────────────────────────
        @Override
        public List<DispenseResponse> getDispenseHistoryByPharmacist(
                        UUID pharmacistId) {

                List<DispenseEntity> history = dispenseRepository
                                .findByDispensedByIdOrderByDispensedAtDesc(pharmacistId);

                if (history.isEmpty()) {
                        throw new ResourceNotFoundException(
                                        "No dispense history found for this pharmacist");
                }

                return history.stream()
                                .map(this::mapToResponse)
                                .toList();
        }

        // ── Get By Prescription ──────────────────────────────
        @Override
        public List<DispenseResponse> getDispenseHistoryByPrescription(
                        UUID prescriptionId) {

                List<DispenseEntity> history = dispenseRepository
                                .findByPrescriptionIdOrderByDispensedAtDesc(prescriptionId);

                if (history.isEmpty()) {
                        throw new ResourceNotFoundException(
                                        "No dispense history found for this prescription");
                }

                return history.stream()
                                .map(this::mapToResponse)
                                .toList();
        }

        // ── Helper ───────────────────────────────────────────
        private DispenseResponse mapToResponse(DispenseEntity dispense) {
                return DispenseResponse.builder()
                                .prescriptionId(dispense.getPrescription().getId())
                                .patientId(dispense.getPatient().getId())
                                .patientName(dispense.getPatient().getName())
                                .dispensedById(dispense.getDispensedBy().getId())
                                .dispensedByName(dispense.getDispensedBy().getName())
                                .totalAmount(dispense.getTotalAmount())
                                .notes(dispense.getNotes())
                                .dispensedAt(dispense.getDispensedAt())
                                .items(List.of(
                                                DispenseItemResponse.builder()
                                                                .dispenseId(dispense.getId())
                                                                .medicineId(dispense.getMedicine().getId())
                                                                .medicineName(dispense.getMedicine()
                                                                                .getMedicineName())
                                                                .quantityDispensed(
                                                                                dispense.getQuantityDispensed())
                                                                .pricePerUnit(dispense.getMedicine()
                                                                                .getPrice())
                                                                .totalPrice(dispense.getTotalAmount())
                                                                .build()))
                                .build();
        }

}
