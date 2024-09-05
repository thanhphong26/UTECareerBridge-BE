package com.pn.career.services;

import com.pn.career.components.LocalizationUtils;
import com.pn.career.dtos.EmployerUpdateDTO;
import com.pn.career.exceptions.DataNotFoundException;
import com.pn.career.models.Employer;
import com.pn.career.repositories.EmployerRepository;
import com.pn.career.repositories.IndustryRepository;
import com.pn.career.utils.MessageKeys;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@AllArgsConstructor
public class EmployerService implements IEmployerService {
    private final EmployerRepository employerRepository;
    private final IndustryRepository industryRepository;
    private final CloudinaryService cloudinaryService;
    private final LocalizationUtils localizationUtils;
    @Override
    @Transactional
    public Employer updateEmployer(Integer employerId, EmployerUpdateDTO employerUpdateDTO) {
        try {
            Employer employer=employerRepository.findById(employerId).orElseThrow(() -> new DataNotFoundException(
                    localizationUtils.getLocalizedMessage(MessageKeys.EMPLOYER_DOES_NOT_EXISTS)));
            employer.setCompanyName(employer.getCompanyName());
            employer.setCompanyDescription(employerUpdateDTO.getCompanyDescription());
            employer.setCompanyWebsite(employerUpdateDTO.getCompanyWebsite());
            employer.setCompanySize(employerUpdateDTO.getCompanySize());
            employer.setIndustry(industryRepository.findById(employerUpdateDTO.getIndustryId()).orElseThrow(() -> new DataNotFoundException(
                    localizationUtils.getLocalizedMessage(MessageKeys.INDUSTRY_DOES_NOT_EXISTS))));
            String companyNameSlug = employerUpdateDTO.getCompanyName().replaceAll("[^a-zA-Z0-9]", "_");

            if (!employerUpdateDTO.getCompanyLogo().isEmpty()) {
                String logoUrl = cloudinaryService.uploadFile(employerUpdateDTO.getCompanyLogo(),companyNameSlug + "_logo");
                employer.setCompanyLogo(logoUrl);
            }

            if (!employerUpdateDTO.getBackgroundImage().isEmpty()) {
                String backgroundUrl = cloudinaryService.uploadFile(employerUpdateDTO.getBackgroundImage(),companyNameSlug + "_background");
                employer.setBackgroundImage(backgroundUrl);
            }

            if (!employerUpdateDTO.getVideoIntroduction().isEmpty()) {
                String videoUrl = cloudinaryService.uploadFile(employerUpdateDTO.getVideoIntroduction(),companyNameSlug + "_video");
                employer.setVideoIntroduction(videoUrl);
            }
            return employerRepository.save(employer);
        } catch (DataNotFoundException | IOException e) {
            throw new RuntimeException("Failed to update company profile", e);
        }
    }

    @Override
    public Employer getEmployerById(Integer employerId) throws DataNotFoundException {
        return employerRepository.findById(employerId).orElseThrow(()-> new DataNotFoundException(
                localizationUtils.getLocalizedMessage(MessageKeys.EMPLOYER_DOES_NOT_EXISTS)));
    }
}
