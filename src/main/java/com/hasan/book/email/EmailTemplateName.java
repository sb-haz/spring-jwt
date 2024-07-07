package com.hasan.book.email;

import lombok.Getter;

@Getter
// Enum to store the email template names
public enum EmailTemplateName {

    ACTIVATE_ACCOUNT("activate_account");
    private final String name;

    EmailTemplateName(String name) {
        this.name = name;
    }

}
