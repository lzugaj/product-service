package com.luv2code.productservice.service;

import com.luv2code.productservice.exception.ProductNotFoundException;
import com.luv2code.productservice.model.Product;
import com.luv2code.productservice.repository.ProductRepository;
import com.luv2code.productservice.rest.dto.ProductRequest;
import com.luv2code.productservice.rest.dto.ProductResponse;
import com.luv2code.productservice.rest.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {

    private final ProductMapper productMapper;
    private final ProductRepository productRepository;
    private final HnbExchangeRateService hnbExchangeRateService;

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Assert.notNull(request, "The product request cannot be null");

        BigDecimal priceUsd = hnbExchangeRateService.convertEurToUsd(request.priceEur());
        log.debug("EUR price {} converted to USD price {}", request.priceEur(), priceUsd);

        Product product = productMapper.toEntity(request, priceUsd);
        productRepository.save(product);

        return productMapper.toResponse(product);
    }

    public ProductResponse findByCode(String code) {
        Assert.hasText(code, "Product code cannot be empty");

        Product product = productRepository.findByCode(code)
                .orElseThrow(() -> new ProductNotFoundException(
                        "Product with code '%s' was not found".formatted(code)
                ));

        return productMapper.toResponse(product);
    }

    public List<ProductResponse> findAll() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }
}
