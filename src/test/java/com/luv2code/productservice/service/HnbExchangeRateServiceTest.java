package com.luv2code.productservice.service;

import com.luv2code.productservice.client.HnbFeignClient;
import com.luv2code.productservice.exception.ExchangeRateServiceException;
import com.luv2code.productservice.rest.dto.HnbExchangeRateResponse;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HnbExchangeRateServiceTest {

    @Mock
    private HnbFeignClient hnbFeignClient;

    @Mock
    private FeignException exception;

    @InjectMocks
    private HnbExchangeRateService hnbExchangeRateService;

    @Test
    void convertEurToUsd_validExchangeRate_returnsConvertedPrice() {
        HnbExchangeRateResponse response = new HnbExchangeRateResponse("1,1650");

        when(hnbFeignClient.getExchangeRate("USD"))
                .thenReturn(List.of(response));

        BigDecimal result = hnbExchangeRateService.convertEurToUsd(
                new BigDecimal("12.34")
        );

        assertThat(result).isEqualByComparingTo("14.38");

        verify(hnbFeignClient).getExchangeRate("USD");
    }

    @Test
    void convertEurToUsd_exchangeRateNotFound_throwsException() {
        when(hnbFeignClient.getExchangeRate("USD"))
                .thenReturn(List.of());

        assertThatThrownBy(() ->
                hnbExchangeRateService.convertEurToUsd(
                        new BigDecimal("12.34")
                ))
                .isInstanceOf(ExchangeRateServiceException.class)
                .hasMessage("USD exchange rate was not found");

        verify(hnbFeignClient).getExchangeRate("USD");
    }

    @Test
    void convertEurToUsd_hnbUnavailable_throwsException() {
        when(hnbFeignClient.getExchangeRate("USD"))
                .thenThrow(exception);

        assertThatThrownBy(() ->
                hnbExchangeRateService.convertEurToUsd(
                        new BigDecimal("12.34")
                ))
                .isInstanceOf(ExchangeRateServiceException.class)
                .hasMessage("Unable to retrieve EUR/USD exchange rate.");

        verify(hnbFeignClient).getExchangeRate("USD");
    }

    @Test
    void convertEurToUsd_roundsResultToTwoDecimalPlaces() {
        HnbExchangeRateResponse response = new HnbExchangeRateResponse("1,1650");

        when(hnbFeignClient.getExchangeRate("USD"))
                .thenReturn(List.of(response));

        BigDecimal result = hnbExchangeRateService.convertEurToUsd(
                new BigDecimal("12.34")
        );

        assertThat(result)
                .isEqualByComparingTo("14.38");

        assertThat(result.scale()).isEqualTo(2);
    }
}