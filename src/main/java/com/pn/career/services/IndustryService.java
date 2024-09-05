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
}
