package com.pn.career.services;

import com.pn.career.responses.UserResponse;

import java.io.ByteArrayInputStream;
import java.util.List;

public interface IExportService {
    ByteArrayInputStream exportUserToPdf(List<UserResponse> userResponses);
    ByteArrayInputStream exportUserExcel(List<UserResponse> userResponses);

}
