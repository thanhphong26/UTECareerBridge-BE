package com.pn.career.event;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Data
@Setter
@Getter
@RequiredArgsConstructor
public class JobAppliedEvent {
    private final Integer userId;
    private final Integer jobId;
}
