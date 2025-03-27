package com.pn.career.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class SocialAccountDTO {
    @JsonProperty("google_account_id")
    protected String googleAccountId;
    public boolean isGoogleAccountIdValid() {
        return googleAccountId != null && !googleAccountId.isEmpty();
    }
    public boolean isSocialLogin() {
        return googleAccountId != null && !googleAccountId.isEmpty();
    }
}
