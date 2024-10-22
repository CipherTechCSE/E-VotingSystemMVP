package org.ciphertech.api_gateway.dto.authority;

public class AuthorityResponse<T> {
    private String message;
    private T data;

    public AuthorityResponse(String message, T data) {
        this.message = message;
        this.data = data;
    }

    // Getters and setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
