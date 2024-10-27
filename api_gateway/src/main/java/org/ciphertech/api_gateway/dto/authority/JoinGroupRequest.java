package org.ciphertech.api_gateway.dto.authority;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JoinGroupRequest {
    private String t;
    private String s;

    // Getter and setter for T
    public String getT() {
        return t;
    }

    public void setT(String t) {
        this.t = t;
    }

    // Getter and setter for S

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }
}
