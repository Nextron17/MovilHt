package com.example.hortitechv1.models;

public class ResetPasswordRequest {
    private String email;
    private String verificationCode;
    private String newPassword;
    public ResetPasswordRequest(String email, String code, String newPassword) {
        this.email = email;
        this.verificationCode = code;
        this.newPassword = newPassword;
    }
}
