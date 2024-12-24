package com.pn.career.dtos;
public record UpdatePasswordDTO(String oldPassword, String newPassword, String confirmPassword) {
}
