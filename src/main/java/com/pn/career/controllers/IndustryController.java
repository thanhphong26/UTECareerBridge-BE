package com.pn.career.controllers;

import com.pn.career.components.LocalizationUtils;
import com.pn.career.models.Industry;
import com.pn.career.responses.IndustryResponse;
import com.pn.career.responses.ResponseObject;
import com.pn.career.services.IIndustryService;
import com.pn.career.services.IndustryService;
import com.pn.career.utils.MessageKeys;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/industries")
@AllArgsConstructor
public class IndustryController {
    private final IIndustryService industryService;
    private final LocalizationUtils localizationUtils;
    @GetMapping("/get-all-industries")
    public ResponseEntity<ResponseObject> getAllIndustries() {
        List<Industry> industries = industryService.getAllIndustries();
        List<IndustryResponse> industryResponses = industries.stream()
                .map(IndustryResponse::fromIndustry)
                .toList();
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.GET_ALL_INDUSTRIES_SUCCESSFULLY))
                .status(HttpStatus.OK)
                .data(industryResponses)
                .build());
    }

}
