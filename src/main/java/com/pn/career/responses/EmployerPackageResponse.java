package com.pn.career.responses;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.pn.career.models.EmployerPackage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployerPackageResponse {
    private Integer employerId;
    private String companyName;
    private PackageResponse packageResponse;
    private Integer amount;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime expiredAt;
    public static EmployerPackageResponse fromEmployerPackage(EmployerPackage employerPackage){
        return EmployerPackageResponse.builder()
                .employerId(employerPackage.getEmployer().getUserId())
                .companyName(employerPackage.getEmployer().getCompanyName())
                .packageResponse(PackageResponse.from(employerPackage.getJobPackage()))
                .amount(employerPackage.getAmount())
                .expiredAt(employerPackage.getExpiredAt())
                .build();
    }
}
