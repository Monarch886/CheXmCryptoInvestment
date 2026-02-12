package kz.che.xm.crypto.mapping;

import kz.che.xm.crypto.dto.enums.CurrencyTypeDto;
import kz.che.xm.crypto.dto.view.AllSupportedCryptosView;
import kz.che.xm.crypto.dto.view.CryptoStatView;
import kz.che.xm.crypto.model.AllCryptosStatModel;
import kz.che.xm.crypto.model.CryptoStatModel;
import org.mapstruct.Mapper;

import static java.util.stream.Collectors.toList;
import static org.mapstruct.NullValueCheckStrategy.ALWAYS;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;
import static org.mapstruct.ReportingPolicy.ERROR;

@Mapper(componentModel = "spring",
        nullValueCheckStrategy = ALWAYS, unmappedTargetPolicy = ERROR, nullValuePropertyMappingStrategy = IGNORE,
        uses = {CryptoRateMapper.class, CurrencyTypeMapper.class})
public interface CryptoStatMapper {
    CryptoStatView toView(CryptoStatModel model);

    default AllSupportedCryptosView toView(AllCryptosStatModel model) {
        return new AllSupportedCryptosView(model.getStats().stream()
                .map(s -> s.getCurrency().name())
                .map(CurrencyTypeDto::fromString)
                .collect(toList()));
    }
}
