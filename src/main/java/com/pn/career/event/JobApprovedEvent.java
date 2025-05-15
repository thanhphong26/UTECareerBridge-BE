package com.pn.career.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JobApprovedEvent {
    private final Integer jobId;
    private final Integer employerId;
}
