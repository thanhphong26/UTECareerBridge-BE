package com.pn.career.event;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Data
@RequiredArgsConstructor
@Getter
@Setter
public class JobUnsavedEvent {
    private final Integer userId;
    private final Integer jobId;
}
