package kz.che.xm.crypto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import kz.che.xm.crypto.dto.enums.CurrencyTypeDto;
import kz.che.xm.crypto.dto.exception.ErrorResponse;
import kz.che.xm.crypto.dto.reqres.CryptoSearchRequest;
import kz.che.xm.crypto.dto.view.AllSupportedCryptosView;
import kz.che.xm.crypto.dto.view.CryptoStatView;
import kz.che.xm.crypto.dto.view.SupportedCurrencyView;
import kz.che.xm.crypto.mapping.CryptoStatMapper;
import kz.che.xm.crypto.mapping.CurrencyTypeMapper;
import kz.che.xm.crypto.model.AllCryptosStatModel;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class CryptoController {
    private final CryptoUsecase usecase;
    private final CryptoStatMapper statMapper;
    private final CurrencyTypeMapper currencyMapper;

    @GetMapping("/currencies")
    @Operation(summary = "Get all currencies",
            description = "Return all supported currencies in normalize order.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "All supported currencies.",
                    content = @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SupportedCurrencyView.class)))
    })
    public ResponseEntity<AllSupportedCryptosView> getAllCurrencies() {
        AllCryptosStatModel stats = usecase.getCurrenciesCacheb(new CryptoSearchRequest());
        AllSupportedCryptosView view = statMapper.toView(stats);
        return ResponseEntity.ok(view);

    }

    @GetMapping("/stat")
    @Operation(summary = "Get statistic by currency.",
            description = "Return oldest, newest, min and max rates for currency.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Statistic calculated.",
                    content = @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CryptoStatView.class))),
            @ApiResponse(responseCode = "429",
                    description = "Unsupported currency.",
                    content = @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CryptoStatView> getCryptoStat(@RequestParam(name = "currency") CurrencyTypeDto currency) {
        return ResponseEntity.ok(
                statMapper.toView(usecase.getStat(CryptoSearchRequest.builder()
                        .currency(currency)
                        .build()))
        );
    }

    @GetMapping("/top")
    @Operation(summary = "Get top currency.",
            description = "Return top by normalize rate currency. For the date.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Top currency found",
                    content = @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SupportedCurrencyView.class))),
            @ApiResponse(responseCode = "429",
                    description = "Unsupported currency",
                    content = @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<SupportedCurrencyView> getTopCrypto(@RequestParam("date")
                                                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                              ZonedDateTime date) {
        return ResponseEntity.ok(SupportedCurrencyView.builder()
                .currency(currencyMapper.toDto(usecase.getTop(
                        CryptoSearchRequest.builder()
                                .dateFrom(date)
                                .dateTo(date.withDayOfMonth(2))
                                .build())))
                .build()
        );
    }
}
