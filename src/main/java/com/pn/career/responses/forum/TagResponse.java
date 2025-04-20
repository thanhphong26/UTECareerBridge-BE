package com.pn.career.responses.forum;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TagResponse {
    private Integer id;
    private String name;
    private String description;
}
