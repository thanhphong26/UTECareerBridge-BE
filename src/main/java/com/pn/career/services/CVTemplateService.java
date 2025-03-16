package com.pn.career.services;

import com.pn.career.models.CVTemplate;
import com.pn.career.repositories.CVTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CVTemplateService implements ICVTemplateService{
    private final CVTemplateRepository cvTemplateRepository;
    @Override
    public CVTemplate findCVTemplateById(Integer id) {
        return cvTemplateRepository.findById(id).orElse(null);
    }
    @Override
    public CVTemplate saveCVTemplate(CVTemplate cvTemplate) {
        return cvTemplateRepository.save(cvTemplate);
    }
    @Override
    public void deleteCVTemplate(Integer cvTemplateId) {
        CVTemplate cvTemplate = cvTemplateRepository.findById(cvTemplateId).orElse(null);
        cvTemplateRepository.delete(cvTemplate);
    }
    @Override
    public List<CVTemplate> findAllCVTemplates() {
        return cvTemplateRepository.findAll();
    }

}
