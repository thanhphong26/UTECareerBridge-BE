package com.pn.career.services;

import com.pn.career.dtos.IndustryDTO;
import com.pn.career.dtos.IndustryUpdateDTO;
import com.pn.career.exceptions.DuplicateNameException;
import com.pn.career.models.Industry;
import com.pn.career.repositories.IndustryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@AllArgsConstructor
public class IndustryService implements IIndustryService{
    private final IndustryRepository industryRepository;
    @Override
    public List<Industry> getAllActiveIndustries(boolean isAdmin) {
        if(isAdmin){
            return industryRepository.findAll();
        }
        return industryRepository.findAllByIsActiveTrue();
    }
    @Override
    @Transactional
    public Industry createIndustry(IndustryDTO industryDTO) {
        if(industryRepository.existsByIndustryNameIgnoreCase(industryDTO.getIndustryName())){
            throw new DuplicateNameException("Loại hình công ty có tên "+industryDTO.getIndustryName()+" đã tồn tại");
        }
        Industry newIndustry=Industry.builder()
                .industryName(industryDTO.getIndustryName())
                .isActive(true)
                .build();
        return industryRepository.save(newIndustry);
    }
    @Override
    public Industry getIndustryById(Integer industryId) {
        return industryRepository.findById(industryId).orElseThrow(()->new RuntimeException("Không tìm thấy loại hình công ty có id "+industryId));
    }
    @Override
    @Transactional
    public Industry updateIndustry(Integer industryId, IndustryUpdateDTO industryUpdateDTO) {
        Industry existingIndustry=getIndustryById(industryId);
        if (!existingIndustry.getIndustryName().equals(industryUpdateDTO.getIndustryName())) {
            // Chỉ kiểm tra trùng lặp nếu tên thực sự thay đổi
            if (industryRepository.existsByIndustryNameIgnoreCase(industryUpdateDTO.getIndustryName())) {
                throw new DuplicateNameException("Loại hình công ty có tên " + industryUpdateDTO.getIndustryName() + " đã tồn tại");
            }
            existingIndustry.setIndustryName(industryUpdateDTO.getIndustryName());
        }
        existingIndustry.setActive(industryUpdateDTO.isActive());
        return industryRepository.save(existingIndustry);
    }

    @Override
    @Transactional
    public void deleteIndustry(Integer industryId) {
        Industry industry = industryRepository.findById(industryId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy loại hình công ty có id " + industryId));
        industryRepository.delete(industry);
    }


}
