package com.pn.career.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Table(name = "employers")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@PrimaryKeyJoinColumn(name = "employer_id")
public class Employer extends User{
    @Column(name = "company_name")
    private String companyName;
    @Column(name = "company_logo")
    private String companyLogo;
    @Column(name = "company_address")
    private String companyAddress;
    @Column(name = "company_phone_number")
    private String companyPhoneNumber;
    @Column(name = "company_website")
    private String companyWebsite;
    @Column(name = "company_description")
    private String companyDescription;
    @Column(name = "company_image")
    private String companyImage;
    @Column(name = "video_intro")
    private String videoIntro;
    @Column(name = "company_size")
    private int companySize;
    @Column(name = "is_approved")
    private boolean isApproved;
    @Column(name = "business_certificate")
    private String businessCertificate;
    @OneToMany(mappedBy = "employer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmployerPackage> employerPackages = new ArrayList<>();

}
