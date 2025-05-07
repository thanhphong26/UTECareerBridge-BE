package com.pn.career.dtos.Forum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ForumDTO {
    private String name;
    private String description;
    private String image;
}
