package kz.che.xm.crypto.mapping;

import kz.che.xm.crypto.dto.view.CryptoRateView;
import kz.che.xm.crypto.enity.CryptoRateEntity;
import kz.che.xm.crypto.model.CryptoRateModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

import static org.mapstruct.NullValueCheckStrategy.ALWAYS;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;
import static org.mapstruct.ReportingPolicy.ERROR;

@Mapper(componentModel = "spring",
        nullValueCheckStrategy = ALWAYS, unmappedTargetPolicy = ERROR, nullValuePropertyMappingStrategy = IGNORE)
public interface CryptoRateMapper {

    CryptoRateModel toModel(CryptoRateEntity entity);

    List<CryptoRateModel> toModel(List<CryptoRateEntity> accountExtracts);

    @Mapping(target = "id", ignore = true)
    CryptoRateEntity toEntity(CryptoRateModel entity);

    List<CryptoRateEntity> toEntity(List<CryptoRateModel> accountExtracts);

    CryptoRateView toView(CryptoRateModel model);

    List<CryptoRateView> toView(List<CryptoRateModel> accountExtracts);
}
