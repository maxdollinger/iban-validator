package com.iban.iban.model;

public class DefaultIban extends Iban {

    public DefaultIban(String iban) {
        super(iban);
    }

    public String getBankCode() {
        return "";
    }

    public String getBankAccount() {
        return "";
    }
}
