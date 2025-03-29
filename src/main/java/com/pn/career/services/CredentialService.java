package com.pn.career.services;

import com.pn.career.models.EmployerCredential;
import com.pn.career.repositories.EmployerCredentialRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CredentialService {
    private final EmployerCredentialRepository employerCredentialRepository;
    @Transactional
    public Optional<EmployerCredential> findByEmployerId(Integer employerId) {
        return employerCredentialRepository.findByEmployerId(employerId);
    }
    @Transactional
    public EmployerCredential saveZoomCredentials(Integer employerId,
                                                  String accessToken,
                                                  String refreshToken) {
        Optional<EmployerCredential> existingCredential = employerCredentialRepository.findByEmployerId(employerId);

        EmployerCredential credential;
        if (existingCredential.isPresent()) {
            credential = existingCredential.get();
            credential.setZoomAccessToken(accessToken);
            credential.setZoomRefreshToken(refreshToken);
        } else {
            credential = EmployerCredential.builder()
                    .employerId(employerId)
                    .zoomAccessToken(accessToken)
                    .zoomRefreshToken(refreshToken)
                    .build();
        }

        return employerCredentialRepository.save(credential);
    }

    @Transactional
    public EmployerCredential saveGoogleCredentials(Integer employerId,
                                                    String accessToken,
                                                    String refreshToken) {
        Optional<EmployerCredential> existingCredential = employerCredentialRepository.findByEmployerId(employerId);

        EmployerCredential credential;
        if (existingCredential.isPresent()) {
            credential = existingCredential.get();
            credential.setGoogleAccessToken(accessToken);
            credential.setGoogleRefreshToken(refreshToken);
        } else {
            credential = EmployerCredential.builder()
                    .employerId(employerId)
                    .googleAccessToken(accessToken)
                    .googleRefreshToken(refreshToken)
                    .build();
        }

        return employerCredentialRepository.save(credential);
    }

    @Transactional
    public void updateZoomToken(Integer employerId, String newAccessToken) {
        Optional<EmployerCredential> credential = findByEmployerId(employerId);
        credential.ifPresent(c -> {
            c.setZoomAccessToken(newAccessToken);
            employerCredentialRepository.save(c);
        });
    }

    @Transactional
    public void updateGoogleToken(Integer employerId, String newAccessToken) {
        Optional<EmployerCredential> credential = findByEmployerId(employerId);
        credential.ifPresent(c -> {
            c.setGoogleAccessToken(newAccessToken);
            employerCredentialRepository.save(c);
        });
    }
}
