package kz.che.xm.crypto;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import static org.springframework.boot.SpringApplication.run;

@EnableCaching
@SpringBootApplication
public class XmCryptoInvestmentApp {
    static void main(String[] args) {
        run(XmCryptoInvestmentApp.class, args);
    }
}
