package com.pn.career.repositories;

import com.pn.career.models.EmployerCredential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployerCredentialRepository extends JpaRepository<EmployerCredential, Integer> {
    Optional<EmployerCredential> findByEmployerId(Integer employerId);
    EmployerCredential findByZoomAccessToken(String zoomAccessToken);
    EmployerCredential findByZoomRefreshToken(String zoomRefreshToken);
    EmployerCredential findByGoogleAccessToken(String googleAccessToken);
    EmployerCredential findByGoogleRefreshToken(String googleRefreshToken);
}
