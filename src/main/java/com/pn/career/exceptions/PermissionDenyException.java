package com.pn.career.exceptions;
public class PermissionDenyException extends RuntimeException{
    public PermissionDenyException(String message) {
        super(message);
    }
}