package com.pn.career.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pn.career.models.Job;
import jakarta.persistence.JoinColumn;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ApplicationDTO {


    @JsonProperty("job_id")
    private int job_id;


}
