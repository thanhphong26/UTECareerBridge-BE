package com.pn.career.repositories;

import com.pn.career.dtos.EmployerJobCountDTO;
import com.pn.career.models.Employer;
import com.pn.career.models.EmployerStatus;
import com.pn.career.responses.EmployerResponse;
import com.pn.career.responses.StudentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;
import java.util.Optional;

public interface EmployerRepository extends JpaRepository<Employer, Integer> {
    Integer countEmployerByApprovalStatus(EmployerStatus status);
    Optional<Employer> findByCompanyName(String companyName);
    @Query("SELECT e FROM Employer e WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR e.companyName LIKE %:keyword% ) " +
            "AND (:industryId IS NULL OR :industryId = 0 OR e.industry.industryId = :industryId)" + "AND (:status IS NULL OR e.approvalStatus = :status)")
    Page<Employer> searchEmployers(@Param("keyword") String keyword, @Param("industryId") Integer industryId, Pageable pageable, EmployerStatus status);
    @Query("SELECT e FROM Employer e WHERE e.industry.industryId = :industryId AND e.approvalStatus = 'APPROVED' ORDER BY FUNCTION('RAND')")
    Page<Employer> findRandomEmployersByIndustry(@Param("industryId") Integer industryId, Pageable pageable);
    //get top 10 employer has the most job
    @Query("select e as employer, count(j) as jobCount from Employer e left join Job j on j.employer=e group by e order by jobCount desc")
    Page<Object[]> findTopEmployerByJobCount(Pageable pageable);
    //get all employer by job categopry name and employer status

    @Query("SELECT e FROM Employer e JOIN Job j ON e.userId = j.employer.userId " +
            "JOIN j.jobCategory c WHERE c.jobCategoryName = :categoryName " +
            "AND e.approvalStatus = :status")
    List<Employer> findAllByJobCategoryAndStatus(@Param("categoryName") String categoryName,
                                                 @Param("status") EmployerStatus status);

    @Query(value = "SELECT " +
            "(SELECT COUNT(*) FROM applications a JOIN jobs j ON a.job_id = j.job_id WHERE j.employer_id = :employer_id) AS total_applications, " +
            "(SELECT COUNT(*) FROM interviews i WHERE i.employer_id = :employer_id) AS total_interviews",
            nativeQuery = true)
    Object getEmployerStatisticsRaw(@Param("employer_id") Integer employerId);
}
