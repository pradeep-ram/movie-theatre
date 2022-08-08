package com.jpmc.theater;


import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class Reservation {

    @NotNull(message = "customer info is required to make a reservation")
    private final Customer customer;
    @NotNull(message = "to make a reservation show cannot be null")
    private final Showing showing;
    @Min(value = 1, message = "atleast one audience is required to make a reservation")
    private final int audienceCount;

    public Reservation(Customer customer, Showing showing, int audienceCount) {
        this.customer = customer;
        this.showing = showing;
        this.audienceCount = audienceCount;
    }

    public double totalFee() {
        return effectivePricePerTicketAfterDiscount(showing.getSequenceOfTheDay(), showing.getMovieFee()) * audienceCount;
    }

    private double effectivePricePerTicketAfterDiscount(int showSequence, double ticketPrice) {
        double specialDiscount = 0, finalDiscount = 0;
        if (Movie.MOVIE_CODE_SPECIAL == showing.getMovie().getSpecialCode()) {
            specialDiscount = ticketPrice * 0.2;  // 20% discount for special movie
        }

        double sequenceDiscount = 0;
        if (showSequence == 1) {
            sequenceDiscount = 3; // $3 discount for 1st show
        } else if (showSequence == 2) {
            sequenceDiscount = 2; // $2 discount for 2nd show
        }
        finalDiscount = Math.max(specialDiscount, sequenceDiscount);

        double showBetween9and4Discount = 0;
        if(showing.getStartTime().getHour() >= 11 && showing.getStartTime().getHour() <= 16) {
            showBetween9and4Discount = ticketPrice * 0.25; //new req 25% discount for shows between 9am and 4pm
            finalDiscount = Math.max(finalDiscount, showBetween9and4Discount);
        }

        //$1 off for shows on the 7th of the month
        if(showing.getStartTime().getDayOfMonth() == 7) {
            finalDiscount = Math.max(finalDiscount, 1);
        }

        return ticketPrice - finalDiscount;
    }
}