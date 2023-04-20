package crypto.investments.api.controller;

import crypto.investments.api.model.BestCurrencyInfoRequest;
import crypto.investments.api.model.CurrencyInfoDto;
import crypto.investments.api.model.CurrencyInfoRequest;
import crypto.investments.api.service.CurrencyInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/currency")
public class CurrenciesControllerV1 {

    private final CurrencyInfoService infoService;

    @Operation(
        summary = "Get list of all currencies ordered by normalized range",
        description = """
            Get list of all currencies order by normalized range from greatest to lowest.
            Only unique last known data per currency is returned in response."""
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "List of currencies",
            content = { @Content(mediaType = "application/json")})
    })
    @GetMapping("")
    public List<CurrencyInfoDto> getCurrencies() {
        return infoService.getAllCurrencies();
    }

    @Operation(
        summary = "Get the best currency for the specified date",
        description = """
            Get the best currency for the specified date,
            The best currency is the currency with the max normalized range for the specified date
            """
    )

    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "List of currencies",
            content = { @Content(mediaType = "application/json")}),
        @ApiResponse(
            responseCode = "400",
            description = "Request cannot be parsed",
            content = { @Content(mediaType = "application/json")}),
        @ApiResponse(
            responseCode = "409",
            description = "Insufficient data to get the best currency for the requested period",
            content = { @Content(mediaType = "application/json")})
    })
    @GetMapping("/top")
    public CurrencyInfoDto getTopCurrency(@ModelAttribute BestCurrencyInfoRequest request) {
        return infoService.getBestCurrencyForDate(request);
    }

    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "List of currencies",
            content = { @Content(mediaType = "application/json")}),
        @ApiResponse(
            responseCode = "404",
            description = "Currency with the specified symbol was not found",
            content = { @Content(mediaType = "application/json")})
    })
    @GetMapping("/{symbol}")
    public CurrencyInfoDto getCurrency(@PathVariable String symbol) {
        CurrencyInfoRequest request = new CurrencyInfoRequest(symbol);
        return infoService.getCurrencyInfo(request);
    }
}
