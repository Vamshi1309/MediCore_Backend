package com.vamshi.HospitalManagementSystem.common.enums;

public enum MedicineFrequency {

    MORNING(1),
    AFTERNOON(1),
    NIGHT(1),

    MORNING_NIGHT(2),
    MORNING_AFTERNOON(2),
    AFTERNOON_NIGHT(2),

    MORNING_AFTERNOON_NIGHT(3);

    private final int dosesPerDay;

    MedicineFrequency(int dosesPerDay) {
        this.dosesPerDay = dosesPerDay;
    }

    public int getDosesPerDay() {
        return dosesPerDay;
    }
}
