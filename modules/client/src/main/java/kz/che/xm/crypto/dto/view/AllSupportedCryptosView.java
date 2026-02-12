package kz.che.xm.crypto.dto.view;

import kz.che.xm.crypto.dto.enums.CurrencyTypeDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AllSupportedCryptosView {
    private List<CurrencyTypeDto> currencies;
}
