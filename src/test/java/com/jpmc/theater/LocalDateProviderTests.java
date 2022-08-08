package com.jpmc.theater;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LocalDateProviderTests {
    @Test
    void makeSureCurrentTime() {
        System.out.println("current time is - " + LocalDateProvider.INSTANCE.currentDate());
    }

    @Test
    void whenInstantiatedTwice_thenOnlyOneInstanceOfSingletonExists() {
        LocalDateProvider instance1 = LocalDateProvider.INSTANCE;
        LocalDateProvider instance2 = LocalDateProvider.INSTANCE;
        assertTrue(instance1.equals(instance2));
    }
}
