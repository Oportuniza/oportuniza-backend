package org.oportuniza.oportunizabackend.offers.exceptions;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class OffersExceptionHandler {
    @ExceptionHandler(OfferNotFoundException.class)
    public void handleApplicationNotFoundException(OfferNotFoundException exception) {


    }

}
