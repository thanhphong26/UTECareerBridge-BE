package com.pn.career.dtos;

import lombok.Data;

@Data
public class TokenRegistrationDTO {
    private String token;
    private Integer userId;
    private DeviceInfo deviceInfo;
    @Data
    public static class DeviceInfo {
        private String userAgent;
        private String platform;
    }
}
