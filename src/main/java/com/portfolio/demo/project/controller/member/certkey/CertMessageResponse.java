package com.portfolio.demo.project.controller.member.certkey;

import lombok.Data;

@Data
public  class CertMessageResponse {
    private String phone;
    private String key;
    private Boolean passed;

    public CertMessageResponse(String phone, String key, Boolean passed) {
        this.phone = phone;
        this.key = key;
        this.passed = passed;
    }
}