package kz.che.xm.crypto.dto.exception;

import lombok.Getter;

@Getter
public class CryptoInvestmentException extends RuntimeException {
    private final CryptoInvestmentError error;
    private final String description;

    public CryptoInvestmentException(CryptoInvestmentError error, String description) {
        super(error.getMessage(), null);
        this.error = error;
        this.description = description;
    }
}
