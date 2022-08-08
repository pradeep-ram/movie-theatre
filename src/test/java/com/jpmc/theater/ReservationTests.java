package com.jpmc.theater;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.groups.Default;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReservationTests {

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenCustomerIsNull_thenReservationCannotBeMade() {
        Customer customer = null;
        var showing = new Showing(
                new Movie("The Batman", Duration.ofMinutes(95), 10.0, 1),
                9,
                LocalDateTime.of(LocalDateProvider.INSTANCE.currentDate(), LocalTime.of(23, 0))
        );
        var reservation = new Reservation(customer, showing, 3);

        Set<ConstraintViolation<Reservation>> constraintViolations = validator.validate(reservation, Default.class);
        assertEquals(1, constraintViolations.size());
        assertEquals("customer info is required to make a reservation", constraintViolations.iterator().next().getMessage());
    }

    @Test
    void whenShowIsNull_thenReservationCannotBeMade() {
        var customer = new Customer("Bruno Max", "unused-id");
        Showing showing = null;
        var reservation = new Reservation(customer, showing, 3);

        Set<ConstraintViolation<Reservation>> constraintViolations = validator.validate(reservation, Default.class);
        assertEquals(1, constraintViolations.size());
        assertEquals("to make a reservation show cannot be null", constraintViolations.iterator().next().getMessage());
    }

    @Test
    void testReservationIsValid() {
        var customer = new Customer("John Mack", "unused-id");
        var showing = new Showing(
                new Movie("The Batman", Duration.ofMinutes(95), 10.0, 1),
                9,
                LocalDateTime.of(LocalDateProvider.INSTANCE.currentDate(), LocalTime.of(23, 0))
        );
        var reservation = new Reservation(customer, showing, 3);
        Set< ConstraintViolation<Reservation>> constraintViolations = validator.validate(reservation, Default.class);
        assertEquals(0, constraintViolations.size());
    }

    @Test
    void whenReservationIsForSpecialMovie_andShowTimeNotBetweenEleven_andFourAfternoon_thenApplyTwentyPercentDiscount() {
        var customer = new Customer("John Mack", "unused-id");
        var showing = new Showing(
                new Movie("The Batman", Duration.ofMinutes(95), 10.0, 1),
                9,
                LocalDateTime.of(LocalDateProvider.INSTANCE.currentDate(), LocalTime.of(23, 0))
        );
        assertEquals(24, new Reservation(customer, showing, 3).totalFee(), "apply special movie discount and total cost of three tickets should be $24");
    }

    @Test
    void whenReservationIsForSpecialMovie_andShowTimeIsBetweenEleven_andFourAfternoon_thenApplyTwentyFivePercentDiscount() {
        var customer = new Customer("Mickey Mike", "unused-id");
        var showing = new Showing(
                new Movie("Spider-Man: No Way Home", Duration.ofMinutes(90), 10.0, 1),
                2,
                LocalDateTime.of(LocalDateProvider.INSTANCE.currentDate(), LocalTime.of(11, 0))
        );
        assertEquals(22.5, new Reservation(customer, showing, 3).totalFee(), "showtime is 11am-4pm , applying 25% discount, total price must be $22.5");
    }

    @Test
    void whenReservationIsForFirstShowOfTheDay_andShowTimeNotBetweenEleven_andFourAfternoon_thenDiscountShouldBeThreeDollars() {
        var customer = new Customer("JPM Chase", "unused-id");
        var showing = new Showing(
                new Movie("Turning Red", Duration.ofMinutes(85), 10.0, 0),
                1,
                LocalDateTime.of(LocalDateProvider.INSTANCE.currentDate(), LocalTime.of(9, 0))
        );
        assertEquals(21, new Reservation(customer, showing, 3).totalFee(), "First show discount must be applied and total ticket price must be $21");
    }

    @Test
    void whenReservationIsForFirstShowOfTheDay_andShowTimeIsBetweenEleven_andFourAfternoon_thenApplyMaxDiscount() {
        var customer = new Customer("Johnny Chase", "unused-id");
        var showing = new Showing(
                new Movie("Turning Red", Duration.ofMinutes(85), 10.0, 0),
                1,
                LocalDateTime.of(LocalDateProvider.INSTANCE.currentDate(), LocalTime.of(11, 0))
        );
        assertEquals(21, new Reservation(customer, showing, 3).totalFee(), "first show discount is more than 11-4p,  applying $3 per ticket discount, total price must be $21");
    }

    @Test
    void whenReservationIsForSecondShowOfTheDay_andShowTimeNotBetweenEleven_andFourAfternoon_thenDiscountShouldBeTwoDollars() {
        var customer = new Customer("Morgan Chase", "unused-id");
        var showing = new Showing(
                new Movie("Spider-Man: No Way Home", Duration.ofMinutes(90), 10, 0),
                2,
                LocalDateTime.of(LocalDateProvider.INSTANCE.currentDate(), LocalTime.of(10, 59))
        );
        assertEquals(24, new Reservation(customer, showing, 3).totalFee(), "second show discount,  applying $2 per ticket discount, total price must be $24");
    }

    @Test
    void whenReservationIsForSecondShowOfTheDay_andShowTimeIsBetweenEleven_andFourAfternoon_thenApplyMaxDiscount() {
        var customer = new Customer("Ben Frank", "unused-id");
        var showing = new Showing(
                new Movie("Spider-Man: No Way Home", Duration.ofMinutes(90), 10.0, 0),
                2,
                LocalDateTime.of(LocalDateProvider.INSTANCE.currentDate(), LocalTime.of(13, 0))
        );
        assertEquals(22.5, new Reservation(customer, showing, 3).totalFee(), "11-4p is more than second show discount,  applying 25% discount, total price must be $22.5");
    }

    @Test
    void whenReservationIsForShowTimeBetweenEleven_andFourAfternoon_thenApplyTwentyFivePercentDiscount() {
        var customer = new Customer("Evelyn Johnson", "unused-id");
        var showing = new Showing(
                new Movie("Turning Red", Duration.ofMinutes(85), 10.0, 0),
                4,
                LocalDateTime.of(LocalDateProvider.INSTANCE.currentDate(), LocalTime.of(14, 30))
        );
        assertEquals(22.5, new Reservation(customer, showing, 3).totalFee(), "11-4p discount only,  applying 25% off, total price must be $22.5");
    }

    @Test
    void whenReservationIsForShowTimeAfterFourPM_andIsNotASpecialMovie_andNotFirstNorSecondShow_andNotOnTheSeventhDayOfMonth_thenTicketPriceIsActualPrice() {
        var customer = new Customer("Eva Shash", "unused-id");
        var showing = new Showing(
                new Movie("Turning Red", Duration.ofMinutes(85), 10.0, 0),
                7,
                LocalDateTime.of(LocalDateProvider.INSTANCE.currentDate(), LocalTime.of(19, 30))
        );
        assertEquals(30, new Reservation(customer, showing, 3).totalFee(), "does not fall into any discount category should be actual price of $30");
    }

    @Test
    void whenReservationIsForSeventhDayOfTheMonth_andShowTimeNotBetweenEleven_andFourAfternoon_thenApplyOneDollarDiscount() {
        var customer = new Customer("Eva Shash", "unused-id");
        var showing = new Showing(
                new Movie("Turning Red", Duration.ofMinutes(85), 10.0, 0),
                7,
                LocalDateTime.of(LocalDateProvider.INSTANCE.currentDate().withDayOfMonth(7).plusMonths(2), LocalTime.of(19, 30))
        );
        assertEquals(27, new Reservation(customer, showing, 3).totalFee(), "must apply 7th day of the month discount and total price should be $27");
    }

    @Test
    void whenReservationIsForSeventhDayOfTheMonth_andShowTimeIsBetweenEleven_andFourAfternoon_thenDiscountShouldBeTwentyFivePercent() {
        var customer = new Customer("Eva Shash", "unused-id");
        var showing = new Showing(
                new Movie("The Batman", Duration.ofMinutes(95), 10.0, 0),
                3,
                LocalDateTime.of(LocalDateProvider.INSTANCE.currentDate().withDayOfMonth(7).plusMonths(2), LocalTime.of(12, 50))
        );
        assertEquals(22.5, new Reservation(customer, showing, 3).totalFee(), "showtime between 11-4pm, apply 25% discount. total price must be $22.5");
    }
}
