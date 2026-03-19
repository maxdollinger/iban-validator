
package com.iban.iban;

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
