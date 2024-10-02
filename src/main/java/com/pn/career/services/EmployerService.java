package com.pn.career.services;
import com.pn.career.components.LocalizationUtils;
import com.pn.career.dtos.EmployerUpdateDTO;
import com.pn.career.dtos.UpdatePasswordDTO;
import com.pn.career.dtos.UpdateProfileDTO;
import com.pn.career.exceptions.DataNotFoundException;
import com.pn.career.models.Employer;
import com.pn.career.models.EmployerStatus;
import com.pn.career.models.Token;
import com.pn.career.repositories.EmployerRepository;
import com.pn.career.repositories.IndustryRepository;
import com.pn.career.repositories.TokenRepository;
import com.pn.career.responses.EmployerResponse;
import com.pn.career.utils.MessageKeys;
import com.pn.career.utils.SlugConverter;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@AllArgsConstructor
public class EmployerService implements IEmployerService {
    private final EmployerRepository employerRepository;
    private final IndustryRepository industryRepository;
    private final IBenefitDetailService benefitDetailService;
    private final AsyncCloudinaryService asyncCloudinaryService;
    private final CloudinaryService cloudinaryService;
    private final LocalizationUtils localizationUtils;
    private final Logger logger= LoggerFactory.getLogger(EmployerService.class);
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    @Transactional
    public Employer updateEmployer(Integer employerId, EmployerUpdateDTO employerUpdateDTO) {
        try {
            Employer employer=employerRepository.findById(employerId).orElseThrow(() -> new DataNotFoundException(
                    localizationUtils.getLocalizedMessage(MessageKeys.EMPLOYER_DOES_NOT_EXISTS)));
            logger.info("Id of employer: "+employerId);
            employer.setCompanyName(employer.getCompanyName().toUpperCase());
            employer.setCompanyAddress(employerUpdateDTO.getCompanyAddress());
            employer.setCompanyDescription(employerUpdateDTO.getCompanyDescription());
            employer.setCompanyEmail(employerUpdateDTO.getCompanyEmail());
            employer.setCompanyWebsite(employerUpdateDTO.getCompanyWebsite());
            employer.setCompanySize(employerUpdateDTO.getCompanySize());
            employer.setVideoIntroduction(employerUpdateDTO.getVideoIntroduction());
            employer.setIndustry(industryRepository.findById(employerUpdateDTO.getIndustryId()).orElseThrow(() -> new DataNotFoundException(
                    localizationUtils.getLocalizedMessage(MessageKeys.INDUSTRY_DOES_NOT_EXISTS))));
            String companyNameSlug = SlugConverter.toSlug(employerUpdateDTO.getCompanyName());
            CompletableFuture<String> logoFuture = CompletableFuture.completedFuture(null);
            CompletableFuture<String> backgroundFuture = CompletableFuture.completedFuture(null);
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
            CompletableFuture.allOf(logoFuture, backgroundFuture).join();
            logoFuture.thenAccept(url -> {
                if (url != null) employer.setCompanyLogo(url);
            });
            backgroundFuture.thenAccept(url -> {
                if (url != null) employer.setBackgroundImage(url);
            });
            benefitDetailService.updateBenefitDetail(employer, employerUpdateDTO.getBenefitDetails());
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
    @Transactional
    public Employer updateProfile(Integer employerId, UpdateProfileDTO updateProfileDTO) {
        Employer employer=employerRepository.findById(employerId).orElseThrow(() -> new DataNotFoundException("Không tìm thấy nhà tuyển dụng tương ứng"));
        employer.setFirstName(updateProfileDTO.getFistName());
        employer.setLastName(updateProfileDTO.getLastName());
        employer.setGender(updateProfileDTO.isGender());
        employer.setDob(updateProfileDTO.getDob());
        employer.setPhoneNumber(updateProfileDTO.getPhoneNumber());
        return employerRepository.save(employer);
    }

    @Override
    @Transactional
    public Employer updatePassword(Integer employerId, UpdatePasswordDTO updatePasswordDTO) {
        Employer employer=employerRepository.findById(employerId).orElseThrow(() -> new DataNotFoundException("Không tìm thấy nhà tuyển dụng tương ứng"));
        if(!passwordEncoder.matches(updatePasswordDTO.oldPassword(),employer.getPassword())){
            throw new RuntimeException("Mật khẩu cũ không chính xác");
        }
        if(updatePasswordDTO.oldPassword().equals(updatePasswordDTO.newPassword())){
            throw new RuntimeException("Mật khẩu mới không được trùng với mật khẩu cũ");
        }
        if(!updatePasswordDTO.newPassword().equals(updatePasswordDTO.confirmPassword())){
            throw new RuntimeException("Mật khẩu xác nhận không khớp");
        }
        employer.setPassword(passwordEncoder.encode(updatePasswordDTO.newPassword()));
        revokedTokens(employerId);
        return employerRepository.save(employer);
    }

    @Override
    @Transactional
    public void revokedTokens(Integer employerId) {
        Employer employer=employerRepository.findById(employerId).orElseThrow(() -> new DataNotFoundException("Không tìm thấy nhà tuyển dụng tương ứng"));
        List<Token> tokens=tokenRepository.findByUser(employer);
        tokens.forEach(token -> {
            token.setRevoked(true);
            token.setExpired(true);
        });
    }

    @Override
    public Page<EmployerResponse> getAllEmployers(String keyword, Integer industryId, PageRequest pageRequest) {
        Page<Employer> employers;
        employers=employerRepository.searchEmployers(keyword,industryId,pageRequest);
        return employers.map(EmployerResponse::fromUser);
    }
    @Override
    @Transactional
    public Employer addBusinessCertificate(Integer employerId, MultipartFile businessCertificate) {
       try {
           Employer employer=employerRepository.findById(employerId).orElseThrow(() -> new DataNotFoundException("Không tìm thấy nhà tuyển dụng tương ứng"));
           logger.info("Uploading business certificate");
           if(!businessCertificate.isEmpty()){
               String companySlug=SlugConverter.toSlug(employer.getCompanyName());
               if(employer.getBusinessCertificate()!=null){
                   asyncCloudinaryService.deleteFile(employer.getBusinessCertificate());
                   logger.info("Deleted business certificate");
               }
               String certificateUrl=cloudinaryService.uploadFile(businessCertificate,companySlug+"_business_certificate");
                if(certificateUrl!=null){
                    employer.setBusinessCertificate(certificateUrl);
                    employer.setApprovalStatus(EmployerStatus.PENDING);
                    return employerRepository.save(employer);
                }else {
                    throw new RuntimeException("Đã xảy ra lỗi khi tải lên giấy phép kinh doanh. Vui lòng thử lại sau");
                }
           }else {
               throw new RuntimeException("Vui lòng chọn một tệp tin. Giấy phép kinh doanh không được để trống");
           }
       }catch(IOException e){
              throw new RuntimeException("Đã xảy ra lỗi khi tải lên giấy phép kinh doanh. Vui lòng thử lại sau",e);
       }
    }
    @Override
    @Transactional
    public void approveEmployer(Integer employerId) {
        Employer employer=employerRepository.findById(employerId).orElseThrow(() -> new DataNotFoundException("Không tìm thấy nhà tuyển dụng tương ứng"));
        if(employer.getBusinessCertificate()==null || employer.getBusinessCertificate().isEmpty()){
            throw new RuntimeException("Nhà tuyển dụng chưa cung cấp giấy phép kinh doanh");
        }
        employer.setApprovalStatus(EmployerStatus.APPROVED);
        employerRepository.save(employer);
    }
    @Override
    @Transactional
    public void rejectEmployer(Integer employerId, String reason) {
        Employer employer=employerRepository.findById(employerId).orElseThrow(() -> new DataNotFoundException("Không tìm thấy nhà tuyển dụng tương ứng"));
        employer.setApprovalStatus(EmployerStatus.REJECTED);
        employer.setRejectedReason(reason);
        employer.setActive(false);
        employer.setReasonBlocked(reason);
        tokenRepository.deleteByUser(employer);
        employerRepository.save(employer);
    }
}
