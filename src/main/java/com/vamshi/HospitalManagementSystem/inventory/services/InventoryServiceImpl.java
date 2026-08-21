package com.vamshi.HospitalManagementSystem.inventory.services;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.vamshi.HospitalManagementSystem.exceptions.BadRequestException;
import com.vamshi.HospitalManagementSystem.exceptions.ResourceAlreadyExistsException;
import com.vamshi.HospitalManagementSystem.exceptions.ResourceNotFoundException;
import com.vamshi.HospitalManagementSystem.common.utils.AuthUtil;
import com.vamshi.HospitalManagementSystem.inventory.dtos.AddMedicineRequest;
import com.vamshi.HospitalManagementSystem.inventory.dtos.MedicineResponse;
import com.vamshi.HospitalManagementSystem.inventory.dtos.UpdateMedicineRequest;
import com.vamshi.HospitalManagementSystem.inventory.entities.MedicineEntity;
import com.vamshi.HospitalManagementSystem.inventory.repositories.InventoryRepository;
import com.vamshi.HospitalManagementSystem.user.entities.UserEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final AuthUtil authUtil;

    @Override
    public MedicineResponse addMedicine(AddMedicineRequest request) {

        if (inventoryRepository.existsByMedicineName(request.getMedicineName())) {
            throw new ResourceAlreadyExistsException("Medicine already exists: " + request.getMedicineName());
        }

        UserEntity pharmacist = authUtil.getLoggedInUser();

        MedicineEntity medicine = MedicineEntity.builder()
                .medicineName(request.getMedicineName())
                .quantity(request.getQuantity())
                .unit(request.getUnit())
                .expiryDate(request.getExpiryDate())
                .price(request.getPrice())
                .addedBy(pharmacist)
                .build();

        MedicineEntity savedMedicine = inventoryRepository.save(medicine);

        return mapToResponse(savedMedicine);

    }

    @Override
    public MedicineResponse updateMedicine(UUID id, UpdateMedicineRequest request) {
        MedicineEntity medicine = inventoryRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Medicine not found with id: " + id));

        // only update fields that are sent
        if (request.getQuantity() != null)
            medicine.setQuantity(request.getQuantity());

        if (request.getUnit() != null)
            medicine.setUnit(request.getUnit());

        if (request.getExpiryDate() != null)
            medicine.setExpiryDate(request.getExpiryDate());

        if (request.getPrice() != null)
            medicine.setPrice(request.getPrice());

        return mapToResponse(inventoryRepository.save(medicine));
    }

    @Override
    public MedicineResponse getMedicineById(UUID id) {
        return mapToResponse(
                inventoryRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Medicine not found with id: " + id)));
    }

    @Override
    public List<MedicineResponse> getAllMedicines() {
        return inventoryRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<MedicineResponse> getLowStockMedicines(Integer threshold) {
        return inventoryRepository
                .findByQuantityLessThan(threshold)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<MedicineResponse> getExpiredMedicines() {
        return inventoryRepository
                .findByExpiryDateBefore(LocalDate.now())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void deleteMedicine(UUID id) {
        MedicineEntity medicine = inventoryRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Medicine not found with id: " + id));

        inventoryRepository.delete(medicine);
    }

    private MedicineResponse mapToResponse(MedicineEntity medicine) {
        return MedicineResponse.builder()
                .id(medicine.getId())
                .medicineName(medicine.getMedicineName())
                .quantity(medicine.getQuantity())
                .unit(medicine.getUnit())
                .expiryDate(medicine.getExpiryDate())
                .price(medicine.getPrice())
                .addedById(medicine.getAddedBy().getId())
                .addedByName(medicine.getAddedBy().getName())
                .createdAt(medicine.getCreatedAt())
                .updatedAt(medicine.getUpdatedAt())
                .build();
    }

    @Override
    public MedicineResponse consumeMedicine(UUID id, Integer quantity) {
        MedicineEntity medicine = inventoryRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Medicine not found"));

        if (medicine.getQuantity() < quantity) {
            throw new BadRequestException(
                    "Insufficient stock");
        }

        medicine.setQuantity(
                medicine.getQuantity() - quantity);

        inventoryRepository.save(medicine);

        return mapToResponse(medicine);
    }

}
