package com.iban.bank;

import com.iban.common.ApiV1Controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;

@ApiV1Controller
@RequestMapping("/bank")
public class BankController {

    private final BankService bankService;

    public BankController(BankService bankService) {
        this.bankService = bankService;
    }

    @PostMapping("/")
    public ResponseEntity<Bank> upsert(@RequestBody Bank bank) {
        Bank result = bankService.upsertBank(
                bank.getCountryCode(),
                bank.getBankCode(),
                bank.getName(),
                bank.getAccountAlgo()
        );
        return ResponseEntity.ok(result);
    }
}
