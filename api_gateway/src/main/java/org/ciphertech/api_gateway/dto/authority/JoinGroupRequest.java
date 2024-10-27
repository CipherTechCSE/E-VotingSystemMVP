package org.ciphertech.api_gateway.dto.authority;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JoinGroupRequest {
    private String T;
    private String S;

    // Getter and setter for T
    public String getT() {
        return T;
    }

    public void setT(String T) {
        this.T = T;
    }

    // Getter and setter for S

    public String getS() {
        return S;
    }

    public void setS(String S) {
        this.S = S;
    }
}
