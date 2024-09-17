package com.pn.career.responses;
import com.pn.career.models.BenefitDetail;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BenefitDetailResponse {
   private Integer employerId;
   private List<BenefitResponse> benefitResponseList;
   public static BenefitDetailResponse fromBenefitDetailResponse(List<BenefitDetail> benefitDetails) {
       if (benefitDetails.isEmpty()) {
           return null;
       }
       Integer employerId = benefitDetails.get(0).getEmployer().getUserId();
       List<BenefitResponse> benefitResponses = benefitDetails.stream()
               .map(benefitDetail -> BenefitResponse.builder()
                       .benefitId(benefitDetail.getBenefit().getBenefitId())
                       .benefitName(benefitDetail.getBenefit().getBenefitName())
                       .benefitIcon(benefitDetail.getBenefit().getBenefitIcon())
                       .benefitDescription(benefitDetail.getDescription())
                       .build())
               .collect(Collectors.toList());

       return BenefitDetailResponse.builder()
               .employerId(employerId)
               .benefitResponseList(benefitResponses)
               .build();
   }
}
