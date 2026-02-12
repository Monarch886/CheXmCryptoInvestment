package kz.che.xm.crypto.mapping;

import kz.che.xm.crypto.dto.enums.CurrencyTypeDto;
import kz.che.xm.crypto.dto.exception.CryptoInvestmentError;
import kz.che.xm.crypto.dto.exception.CryptoInvestmentException;
import kz.che.xm.crypto.type.CurrencyType;
import org.mapstruct.Mapper;

import static kz.che.xm.crypto.dto.enums.CurrencyTypeDto.UNKNOWN;
import static org.mapstruct.NullValueCheckStrategy.ALWAYS;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;
import static org.mapstruct.ReportingPolicy.ERROR;

@Mapper(componentModel = "spring",
        nullValueCheckStrategy = ALWAYS, unmappedTargetPolicy = ERROR, nullValuePropertyMappingStrategy = IGNORE)
public interface CurrencyTypeMapper {

    default CurrencyType toModel(CurrencyTypeDto dto) {
        if (dto == UNKNOWN) {
            throw new CryptoInvestmentException(CryptoInvestmentError.UNKNOWN_CURRENCY, "Unsupported currency.");
        } else {
            return CurrencyType.valueOf(dto.name());
        }
    }

    CurrencyTypeDto toDto(CurrencyType dto);

    default CurrencyTypeDto toDto(String string) {
        return CurrencyTypeDto.fromString(string);
    }
}
