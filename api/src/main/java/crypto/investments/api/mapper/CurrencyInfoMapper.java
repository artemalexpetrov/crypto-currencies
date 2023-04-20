package crypto.investments.api.mapper;

import crypto.investments.api.model.CurrencyInfoDto;
import crypto.investments.api.persistency.model.CurrencyInfo;
import org.mapstruct.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CurrencyInfoMapper {

    /**
     * Converts {@link CurrencyInfo} to {@link CurrencyInfoDto}
     */
    CurrencyInfoDto currencyInfoToDto(CurrencyInfo info);

    List<CurrencyInfoDto> currenciesInfoToDto(Collection<CurrencyInfo> info);

}
