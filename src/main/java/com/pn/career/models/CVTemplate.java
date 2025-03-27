package com.pn.career.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
@Entity
@Table(name = "cv_templates")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CVTemplate extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String description;
    private String templateHtml;
    private String templateCss;
}
