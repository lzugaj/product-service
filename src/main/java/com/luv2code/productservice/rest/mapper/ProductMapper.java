package com.luv2code.productservice.rest.mapper;

import com.luv2code.productservice.model.Product;
import com.luv2code.productservice.rest.dto.ProductRequest;
import com.luv2code.productservice.rest.dto.ProductResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.math.BigDecimal;

@Component
public class ProductMapper {

    public Product toEntity(ProductRequest productRequest, BigDecimal priceUsd) {
        Assert.notNull(productRequest, "The product DTO cannot be null");
        return Product.builder()
                .name(productRequest.name())
                .priceEur(productRequest.priceEur())
                .priceUsd(priceUsd)
                .available(productRequest.available())
                .build();
    }

    public ProductResponse toResponse(Product product) {
        Assert.notNull(product, "The product DTO cannot be null");
        return new ProductResponse(
                product.getCode(),
                product.getName(),
                product.getPriceEur(),
                product.getPriceUsd(),
                product.isAvailable()
        );
    }
}
