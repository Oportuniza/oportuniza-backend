package org.oportuniza.oportunizabackend.offers.exceptions;

import org.oportuniza.oportunizabackend.utils.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class OffersExceptionHandler {
    @ExceptionHandler({OfferNotFoundException.class, JobNotFoundException.class, ServiceNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleApplicationNotFoundException(RuntimeException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(exception.getMessage(), new Date()));
    }

}
