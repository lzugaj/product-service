package com.luv2code.productservice.client;

import com.luv2code.productservice.rest.dto.HnbExchangeRateResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(
        name = "hnb-client",
        url = "${product-service.hnb.api.url}"
)
public interface HnbFeignClient {

    @GetMapping("/tecajn-eur/v3")
    List<HnbExchangeRateResponse> getExchangeRate(
            @RequestParam("valuta") String currency
    );
}