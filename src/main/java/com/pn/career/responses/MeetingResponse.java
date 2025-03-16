package com.pn.career.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pn.career.dtos.MeetingSettingsDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)  // Thêm annotation này
public class MeetingResponse {
    private String message;

    @JsonProperty("join_url")
    private String joinUrl;

    @JsonProperty("password")
    private String password;

    @JsonProperty("id")
    private String id;

    @JsonProperty("host_email")
    private String hostEmail;

    @JsonProperty("topic")
    private String topic;

    @JsonProperty("duration")
    private Integer duration;

    @JsonProperty("start_time")
    private String startTime;

    @JsonProperty("timezone")
    private String timezone;
    // Thêm thông tin từ Google Calendar
    private MeetingSettingsDTO settings;  // Thay đổi từ String sang MeetingSettings
    private String calendarEventId;

    // For error responses
    private String errorMessage;

    public MeetingResponse(String s, Object o, Object o1) {
    }
}
