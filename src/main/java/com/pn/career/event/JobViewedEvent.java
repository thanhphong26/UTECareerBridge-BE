package com.pn.career.event;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@RequiredArgsConstructor
public class JobViewedEvent {
    private final Integer userId;
    private final Integer jobId;
}
