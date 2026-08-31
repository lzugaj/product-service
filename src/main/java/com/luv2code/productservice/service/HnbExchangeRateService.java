package com.luv2code.productservice.service;

import com.luv2code.productservice.client.HnbFeignClient;
import com.luv2code.productservice.exception.ExchangeRateServiceException;
import com.luv2code.productservice.rest.dto.HnbExchangeRateResponse;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Component
@RequiredArgsConstructor
public class HnbExchangeRateService {

    private final HnbFeignClient hnbFeignClient;

    public BigDecimal convertEurToUsd(BigDecimal priceEur) {
        try {
            HnbExchangeRateResponse response = hnbFeignClient
                    .getExchangeRate("USD")
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new ExchangeRateServiceException(
                            "USD exchange rate was not found"
                    ));

            BigDecimal exchangeRate = new BigDecimal(
                    response.middleRate().replace(',', '.')
            );

            return priceEur
                    .multiply(exchangeRate)
                    .setScale(2, RoundingMode.HALF_UP);

        } catch (FeignException exception) {
            log.error("Unable to retrieve USD exchange rate from HNB", exception);

            throw new ExchangeRateServiceException(
                    "Unable to retrieve EUR/USD exchange rate."
            );
        }
    }
}