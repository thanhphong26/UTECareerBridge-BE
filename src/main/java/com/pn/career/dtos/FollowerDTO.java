package com.pn.career.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FollowerDTO {
    @JsonProperty("following_id")
    @NotNull
    private int followingId;
}
