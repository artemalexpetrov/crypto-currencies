package crypto.investments.api.service;

import crypto.investments.api.exception.CurrencyDoesntExistException;
import crypto.investments.api.exception.InsufficientCurrencyDataException;
import crypto.investments.api.mapper.CurrencyInfoMapper;
import crypto.investments.api.model.BestCurrencyInfoRequest;
import crypto.investments.api.model.CurrencyInfoDto;
import crypto.investments.api.model.CurrencyInfoRequest;
import crypto.investments.api.persistency.CurrencyInfoRepository;
import crypto.investments.api.persistency.model.CurrencyInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CurrencyInfoServiceImpl implements CurrencyInfoService {

    private final CurrencyInfoRepository repository;
    private final CurrencyInfoMapper mapper;

    @Override
    public CurrencyInfoDto getCurrencyInfo(CurrencyInfoRequest request) {
        ensureCurrencyExists(request.symbol());
        Optional<CurrencyInfo> currencyInfo = repository.getCurrencyInfo(request.symbol());

        return currencyInfo
            .map(mapper::currencyInfoToDto)
            .orElseThrow(() -> new InsufficientCurrencyDataException(request.symbol()));
    }

    @Override
    public List<CurrencyInfoDto> getAllCurrencies() {
        List<CurrencyInfo> allCurrencies = repository.getAllCurrencyInfo();
        return mapper.currenciesInfoToDto(allCurrencies);
    }

    @Override
    public CurrencyInfoDto getBestCurrencyForDate(BestCurrencyInfoRequest request) {
        Optional<CurrencyInfo> bestCurrency = repository.getBestCurrencyForDate(request.date());

        return bestCurrency
            .map(mapper::currencyInfoToDto)
            .orElseThrow(InsufficientCurrencyDataException::new);
    }

    private void ensureCurrencyExists(String symbol) {
        if (!repository.currencyExists(symbol)) {
            throw new CurrencyDoesntExistException(symbol);
        }
    }
}
