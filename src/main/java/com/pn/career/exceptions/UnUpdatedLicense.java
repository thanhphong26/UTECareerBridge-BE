package com.pn.career.exceptions;

public class UnUpdatedLicense extends RuntimeException {
    public UnUpdatedLicense(String message) {
        super(message);
    }
}
