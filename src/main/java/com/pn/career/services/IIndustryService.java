package com.pn.career.services;

import com.pn.career.dtos.IndustryDTO;
import com.pn.career.dtos.IndustryUpdateDTO;
import com.pn.career.models.Industry;

import java.util.List;

public interface IIndustryService {
    List<Industry> getAllActiveIndustries(boolean isAdmin);
    Industry createIndustry(IndustryDTO industryDTO);
    Industry getIndustryById(Integer industryId);
    Industry updateIndustry(Integer industryId, IndustryUpdateDTO industryUpdateDTO);
    void deleteIndustry(Integer industryId);
}
