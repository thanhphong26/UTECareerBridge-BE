package com.pn.career.exceptions;

public class DuplicateBenefitNameException extends RuntimeException{
    public DuplicateBenefitNameException(String message) {
        super(message);
    }
}
