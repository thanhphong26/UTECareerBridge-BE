package com.pn.career.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChatDTO {
    @NotNull
    private int receiver_id;

    @NotNull
    private String message;
}
