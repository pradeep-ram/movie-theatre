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

public class ShowingTest {
    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void showingMustHaveANonNullMovie() {
        var showing = new Showing(
                null,
                9,
                LocalDateTime.of(LocalDateProvider.INSTANCE.currentDate(), LocalTime.of(23, 0))
        );

        Set<ConstraintViolation<Showing>> constraintViolations = validator.validate(showing, Default.class);
        assertEquals(1, constraintViolations.size());
        assertEquals("movie cannot be null", constraintViolations.iterator().next().getMessage());
    }

    @Test
    void showingMustBeAssociatedWithASequence() {
        var showing = new Showing(
                new Movie("The Batman", Duration.ofMinutes(95), 10.0, 1),
                0,
                LocalDateTime.of(LocalDateProvider.INSTANCE.currentDate(), LocalTime.of(23, 0))
        );

        Set<ConstraintViolation<Showing>> constraintViolations = validator.validate(showing, Default.class);
        assertEquals(1, constraintViolations.size());
        assertEquals("show sequence of the day has to be greater than or equal to one", constraintViolations.iterator().next().getMessage());
    }

    @Test
    void showingMustHaveStartTime() {
        var showing = new Showing(
                new Movie("The Batman", Duration.ofMinutes(95), 10.0, 1),
                1,
                null);

        Set<ConstraintViolation<Showing>> constraintViolations = validator.validate(showing, Default.class);
        assertEquals(1, constraintViolations.size());
        assertEquals("show must have a start time", constraintViolations.iterator().next().getMessage());
    }

    @Test
    void testShowingIsValid() {
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
}
