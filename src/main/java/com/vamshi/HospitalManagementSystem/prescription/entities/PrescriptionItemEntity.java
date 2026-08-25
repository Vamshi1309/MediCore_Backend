package com.vamshi.HospitalManagementSystem.prescription.entities;

import java.util.UUID;

import com.vamshi.HospitalManagementSystem.common.enums.MedicineFrequency;
import com.vamshi.HospitalManagementSystem.inventory.entities.MedicineEntity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "prescription_items")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PrescriptionItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "prescription_id", nullable = false)
    private PrescriptionEntity prescription;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "medicine_id", nullable = false)
    private MedicineEntity medicine;

    @Column(nullable = false)
    private String dosage;

    @Column(nullable = false)
    private Integer duration;

    @Column(columnDefinition = "TEXT")
    private String instructions;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MedicineFrequency frequency;

    @PrePersist
    public void prePersist() {
        if (frequency == null) {
            frequency = MedicineFrequency.MORNING_AFTERNOON;
        }
    }
}