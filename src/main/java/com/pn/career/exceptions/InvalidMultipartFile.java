package com.pn.career.exceptions;

public class InvalidMultipartFile extends RuntimeException {
    public InvalidMultipartFile(String message) {
        super(message);
    }
}
