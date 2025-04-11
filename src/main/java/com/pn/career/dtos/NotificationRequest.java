package com.pn.career.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class NotificationRequest {
    private String title;
    private String message;
}
