package com.pn.career.repositories;

import com.pn.career.responses.TopSkillResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface JobRepositoryCustom {
    List<TopSkillResponse> getTopApplicantSkillsByEmployerId(Integer employerId, Integer limit, LocalDateTime startDate, LocalDateTime endDate);
    List<TopSkillResponse> getTopSkillsByEmployerId(Integer employerId, int limit, LocalDateTime startDate, LocalDateTime endDate);
}
