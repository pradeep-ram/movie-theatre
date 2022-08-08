package com.jpmc.theater;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TheaterTests {
    @Test
    void totalFeeForCustomer() {
        Theater theater = new Theater(LocalDateProvider.INSTANCE);
        Customer john = new Customer("John Doe", "id-12345");
        Reservation reservation = theater.reserve(john, 2, 4);
        assertEquals(37.5, reservation.totalFee(),"since show start time is between 11am-4pm , apply max discount which is 25% and total fee must be $37.5");
    }

    @Test
    void printMovieSchedule() {
        Theater theater = new Theater(LocalDateProvider.INSTANCE);
        theater.printSchedule();
    }
}
