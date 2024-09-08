package com.pn.career.services;

import com.pn.career.models.Industry;
import com.pn.career.repositories.IndustryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@AllArgsConstructor
public class IndustryService implements IIndustryService{
    private final IndustryRepository industryRepository;
    @Override
    public List<Industry> getAllIndustries() {
        return industryRepository.findAll();
    }

    @Override
    public List<Industry> getAllActiveIndustries(boolean isAdmin) {
        if(isAdmin){
            return industryRepository.findAll();
        }
        return industryRepository.findAllByIsActiveTrue();
    }


}
