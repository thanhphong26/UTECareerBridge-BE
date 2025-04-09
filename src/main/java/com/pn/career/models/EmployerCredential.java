package com.pn.career.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Table(name = "employer_credentials")
@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class EmployerCredential extends BaseEntity {
    @Id
    private Integer employerId;
    @Column(name = "zoom_access_token", nullable = false, columnDefinition = "TEXT")
    private String zoomAccessToken;
    @Column(name = "zoom_refresh_token", nullable = false, columnDefinition = "TEXT")
    private String zoomRefreshToken;
    @Column(name = "google_access_token", nullable = false, columnDefinition = "TEXT")
    private String googleAccessToken;
    @Column(name = "google_refresh_token", nullable = false, columnDefinition = "TEXT")
    private String googleRefreshToken;
    @OneToOne
    @JoinColumn(name = "employer_id", referencedColumnName = "employer_id", insertable = false, updatable = false)
    private Employer employer;
    @Column(name = "google_auth_valid")
    private boolean googleAuthValid = true;
}
