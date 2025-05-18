package com.pn.career.responses;

import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@Builder
public class MonthlyUserGrowthResponse {
    private List<UserGrowthResponse> userGrowthResponses;
}
