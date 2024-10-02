package com.pn.career.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Table(name = "employers")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Setter
@Getter
@PrimaryKeyJoinColumn(name = "employer_id")
public class Employer extends User{
    @Column(name = "company_name")
    private String companyName;
    @Column(name = "company_address")
    private String companyAddress;
    @Column(name = "company_logo")
    private String companyLogo;
    @Column(name = "company_email")
    private String companyEmail;
    @Column(name = "company_website")
    private String companyWebsite;
    @Column(name = "company_description")
    private String companyDescription;
    @Column(name = "background_image")
    private String backgroundImage;
    @Column(name = "video_introduction")
    private String videoIntroduction;
    @Column(name = "company_size")
    private String companySize;
    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status")
    private EmployerStatus approvalStatus;
    @Column(name = "rejected_reason")
    private String rejectedReason;
    @Column(name = "business_certificate")
    private String businessCertificate;
    @ManyToOne
    @JoinColumn(name = "industry_id")
    private Industry industry;
    @OneToMany(mappedBy = "employer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmployerPackage> employerPackages = new ArrayList<>();
    @OneToMany(mappedBy = "employer", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // hoặc @JsonIgnore nếu không cần
    private List<BenefitDetail> benefitDetails = new ArrayList<>();
}
