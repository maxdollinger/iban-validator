package com.iban.iban;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/iban")
public class IbanController {

    private final IbanService ibanService;

    public IbanController(IbanService ibanService) {
        this.ibanService = ibanService;
    }

    @GetMapping("/validation")
    public ResponseEntity<IbanValidationResponse> validate(@RequestParam String iban) {
        IbanValidationResponse response = ibanService.validate(iban);
        return response.patternValid()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }
}
