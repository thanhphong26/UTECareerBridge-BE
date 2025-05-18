package com.pn.career.repositories;

import com.pn.career.responses.AdminJobResponse;
import com.pn.career.responses.AdminTopEmployerResponse;
import com.pn.career.responses.TopSkillResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface JobRepositoryCustom {
    List<TopSkillResponse> getTopApplicantSkillsByEmployerId(Integer employerId, Integer limit, LocalDateTime startDate, LocalDateTime endDate);
    List<TopSkillResponse> getTopSkillsByEmployerId(Integer employerId, int limit, LocalDateTime startDate, LocalDateTime endDate);
    AdminJobResponse getStatisticsJobByAdmin(LocalDateTime startDate, LocalDateTime endDate);
    List<Map<String, Object>> getTopRequestedSkills(int limit, LocalDateTime startDate, LocalDateTime endDate);
}
