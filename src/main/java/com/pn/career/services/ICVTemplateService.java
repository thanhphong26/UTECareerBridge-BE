package com.pn.career.services;

import com.pn.career.models.CVTemplate;

import java.util.List;

public interface ICVTemplateService {
    CVTemplate findCVTemplateById(Integer id);
    CVTemplate saveCVTemplate(CVTemplate cvTemplate);
    void deleteCVTemplate(Integer cvTemplateId);
    List<CVTemplate> findAllCVTemplates();
}
