package com.pn.career.services;

import com.pn.career.components.LocalizationUtils;
import com.pn.career.dtos.EmployerUpdateDTO;
import com.pn.career.exceptions.DataNotFoundException;
import com.pn.career.models.Employer;
import com.pn.career.repositories.EmployerRepository;
import com.pn.career.repositories.IndustryRepository;
import com.pn.career.responses.EmployerResponse;
import com.pn.career.utils.MessageKeys;
import com.pn.career.utils.SlugConverter;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@AllArgsConstructor
public class EmployerService implements IEmployerService {
    private final EmployerRepository employerRepository;
    private final IndustryRepository industryRepository;
    private final IBenefitDetailService benefitDetailService;
    private final AsyncCloudinaryService asyncCloudinaryService;
    private final LocalizationUtils localizationUtils;
    private final Logger logger= LoggerFactory.getLogger(EmployerService.class);
    @Override
    @Transactional
    public Employer updateEmployer(Integer employerId, EmployerUpdateDTO employerUpdateDTO) {
        try {
            Employer employer=employerRepository.findById(employerId).orElseThrow(() -> new DataNotFoundException(
                    localizationUtils.getLocalizedMessage(MessageKeys.EMPLOYER_DOES_NOT_EXISTS)));
            employer.setCompanyName(employer.getCompanyName().toUpperCase());
            employer.setCompanyAddress(employerUpdateDTO.getCompanyAddress());
            employer.setCompanyDescription(employerUpdateDTO.getCompanyDescription());
            employer.setCompanyEmail(employerUpdateDTO.getCompanyEmail());
            employer.setCompanyWebsite(employerUpdateDTO.getCompanyWebsite());
            employer.setCompanySize(employerUpdateDTO.getCompanySize());
            employer.setIndustry(industryRepository.findById(employerUpdateDTO.getIndustryId()).orElseThrow(() -> new DataNotFoundException(
                    localizationUtils.getLocalizedMessage(MessageKeys.INDUSTRY_DOES_NOT_EXISTS))));
            String companyNameSlug = SlugConverter.toSlug(employerUpdateDTO.getCompanyName());

            CompletableFuture<String> logoFuture = CompletableFuture.completedFuture(null);
            CompletableFuture<String> backgroundFuture = CompletableFuture.completedFuture(null);
            CompletableFuture<String> videoFuture = CompletableFuture.completedFuture(null);
            if (!employerUpdateDTO.getCompanyLogo().isEmpty()) {
                if(employer.getCompanyLogo()!=null){
                    asyncCloudinaryService.deleteFile(employer.getCompanyLogo());
                    logger.info("Deleted logo");
                }
                logoFuture = asyncCloudinaryService.uploadFileAsync(employerUpdateDTO.getCompanyLogo(), companyNameSlug + "_logo");
            }
            if (!employerUpdateDTO.getBackgroundImage().isEmpty()) {
                if(employer.getBackgroundImage()!=null){
                    asyncCloudinaryService.deleteFile(employer.getBackgroundImage());
                    logger.info("Deleted background");
                }
                backgroundFuture = asyncCloudinaryService.uploadFileAsync(employerUpdateDTO.getBackgroundImage(), companyNameSlug + "_background");
            }
            if (!employerUpdateDTO.getVideoIntroduction().isEmpty()) {
                if (employer.getVideoIntroduction()!=null){
                    asyncCloudinaryService.deleteFile(employer.getVideoIntroduction());
                    logger.info("Deleted video");
                }
                videoFuture = asyncCloudinaryService.uploadFileAsync(employerUpdateDTO.getVideoIntroduction(), companyNameSlug + "_video");
            }
            CompletableFuture.allOf(logoFuture, backgroundFuture, videoFuture).join();
            logoFuture.thenAccept(url -> {
                if (url != null) employer.setCompanyLogo(url);
            });

            backgroundFuture.thenAccept(url -> {
                if (url != null) employer.setBackgroundImage(url);
            });

            videoFuture.thenAccept(url -> {
                if (url != null) employer.setVideoIntroduction(url);
            });
            benefitDetailService.createBenefitDetail(employer, employerUpdateDTO.getBenefitDetails());
            return employerRepository.save(employer);
        } catch (DataNotFoundException e) {
            throw new RuntimeException("Đã xảy ra lỗi khi cập nhật thông tin. Vui lòng thử lại sau", e);
        }
    }

    @Override
    public Employer getEmployerById(Integer employerId) throws DataNotFoundException {
        return employerRepository.findById(employerId).orElseThrow(()-> new DataNotFoundException(
                localizationUtils.getLocalizedMessage(MessageKeys.EMPLOYER_DOES_NOT_EXISTS)));
    }

    @Override
    public Page<EmployerResponse> getAllEmployers(String keyword, Integer industryId, PageRequest pageRequest) {
        Page<Employer> employers;
        employers=employerRepository.searchEmployers(keyword,industryId,pageRequest);
        return employers.map(EmployerResponse::fromUser);
    }
}
