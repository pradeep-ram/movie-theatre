package com.jpmc.theater;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.groups.Default;
import java.time.Duration;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MovieTests {

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testTicketPriceIsValid() {
        Movie johnWick = new Movie("John Wick", Duration.ofMinutes(85), 10, 1);
        Set< ConstraintViolation<Movie>> constraintViolations = validator.validate(johnWick, Default.class);
        assertEquals(0, constraintViolations.size());
    }

    @Test
    void catchInvalidTicketPrice() {
        Movie matrix = new Movie("Matrix", Duration.ofMinutes(95), 0.5, 1);
        Set< ConstraintViolation<Movie>> constraintViolations = validator.validate(matrix, Default.class);
        assertEquals(1, constraintViolations.size());
        assertEquals("ticket price cannot be less than $1.0", constraintViolations.iterator().next().getMessage());
    }
}
