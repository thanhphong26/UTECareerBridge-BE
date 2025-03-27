package com.pn.career.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)  // Thêm annotation này

public class MeetingSettingsDTO {
    private Boolean host_video;
    private Boolean participant_video;
    private Boolean join_before_host;
    private Boolean allow_participants_to_share;
}
