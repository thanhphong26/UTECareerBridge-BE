package com.pn.career.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Table(name = "employers")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@PrimaryKeyJoinColumn(name = "employer_id")
public class Employer extends User{
    @Column(name = "company_name")
    private String companyName;
    @Column(name = "company_address")
    private String companyAddress;
    @Column(name = "company_logo")
    private String companyLogo;
    @Column(name = "company_website")
    private String companyWebsite;
    @Column(name = "company_description")
    private String companyDescription;
    @Column(name = "background_image")
    private String backgroundImage;
    @Column(name = "video_introduction")
    private String videoIntroduction;
    @Column(name = "company_size")
    private int companySize;
    @Column(name = "is_approved")
    private boolean isApproved;
    @Column(name = "business_certificate")
    private String businessCertificate;
    @OneToMany(mappedBy = "employer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmployerPackage> employerPackages = new ArrayList<>();

}
