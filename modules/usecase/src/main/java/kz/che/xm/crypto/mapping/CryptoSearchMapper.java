package kz.che.xm.crypto.mapping;

import kz.che.xm.crypto.dto.reqres.CryptoSearchRequest;
import kz.che.xm.crypto.search.CryptoSearch;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.NullValueCheckStrategy.ALWAYS;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;
import static org.mapstruct.ReportingPolicy.ERROR;

@Mapper(componentModel = "spring",
        nullValueCheckStrategy = ALWAYS, unmappedTargetPolicy = ERROR, nullValuePropertyMappingStrategy = IGNORE,
        uses = {CurrencyTypeMapper.class})
public interface CryptoSearchMapper {

    @Mapping(target = "sort", ignore = true)
    CryptoSearch toDao(CryptoSearchRequest dto);
}
