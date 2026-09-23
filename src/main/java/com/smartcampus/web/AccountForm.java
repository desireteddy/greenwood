package com.smartcampus.web;

public class AccountForm {
    private String username;
    private String role;
    private Long studentId;
    private String temporaryPassword;
    private String confirmTemporaryPassword;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getConfirmTemporaryPassword() {
        return confirmTemporaryPassword;
    }

    public void setConfirmTemporaryPassword(String confirmTemporaryPassword) {
        this.confirmTemporaryPassword = confirmTemporaryPassword;
    }

    public String getTemporaryPassword() {
        return temporaryPassword;
    }

    public void setTemporaryPassword(String temporaryPassword) {
        this.temporaryPassword = temporaryPassword;
    }
}
