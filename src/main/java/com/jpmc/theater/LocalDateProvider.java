package com.jpmc.theater;
/**
 * In a multithreaded environment the current implementation
 * of LocalDateProvider would yield more than one instance
 * which will violate the Singleton pattern.
 *
 * Instead, we will reimplement using enum. Since Java ensures
 * that enum is instantiated only once in java program.
 *
 *
 * **/

import java.time.LocalDate;

public enum LocalDateProvider {
    INSTANCE;

    public LocalDate currentDate() {
            return LocalDate.now();
    }
}
