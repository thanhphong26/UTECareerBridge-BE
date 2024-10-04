package com.pn.career.services;

import com.pn.career.dtos.PackageDTO;
import com.pn.career.exceptions.DataNotFoundException;
import com.pn.career.models.Feature;
import com.pn.career.models.Package;
import com.pn.career.repositories.FeatureRepository;
import com.pn.career.repositories.PackageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@RequiredArgsConstructor
public class PackageService implements IPackageService{
    private final PackageRepository packageRepository;
    private final FeatureRepository featureRepository;

    @Override
    public Package getPackageById(Integer packageId) {
        return packageRepository.findById(packageId).orElseThrow(()->new DataNotFoundException("Không tìm thấy thông tin gói dịch vụ tương ứng"));
    }

    @Override
    @Transactional
    public Package createPackage(PackageDTO packageDTO) {
        Feature feature=featureRepository.findById(packageDTO.getFeatureId()).orElseThrow(()->new DataNotFoundException("Không tìm thấy thông tin tính năng tương ứng"));
        Package packageService=Package.builder()
                .packageName(packageDTO.getPackageName())
                .price(packageDTO.getPrice())
                .description(packageDTO.getDescription())
                .duration(packageDTO.getDuration())
                .amount(packageDTO.getAmount())
                .isActive(true)
                .feature(feature)
                .build();
        return packageRepository.save(packageService);
    }
    @Override
    @Transactional
    public Package updatePackage(Integer packageId, PackageDTO packageDTO) {
        Package packageService=packageRepository.findById(packageId).orElseThrow(()->new DataNotFoundException("Không tìm thấy thông tin gói dịch vụ tương ứng"));
        Feature feature=featureRepository.findById(packageDTO.getFeatureId()).orElseThrow(()->new DataNotFoundException("Không tìm thấy thông tin tính năng tương ứng"));
        packageService.setPackageName(packageDTO.getPackageName());
        packageService.setPrice(packageDTO.getPrice());
        packageService.setDescription(packageDTO.getDescription());
        packageService.setDuration(packageDTO.getDuration());
        packageService.setAmount(packageDTO.getAmount());
        packageService.setFeature(feature);
        packageService.setActive(packageDTO.isActive());
        return packageRepository.save(packageService);
    }
    @Override
    @Transactional
    public void deletePackage(Integer packageId) {
        Package packageService=packageRepository.findById(packageId).orElseThrow(()->new DataNotFoundException("Không tìm thấy thông tin gói dịch vụ tương ứng"));
        packageService.setActive(false);
        packageRepository.save(packageService);
    }
    @Override
    public List<Package> getAllPackages(boolean isAdmin) {
        if(isAdmin){
            return packageRepository.findAll();
        }
        return packageRepository.findAllByIsActiveTrue();
    }
}
