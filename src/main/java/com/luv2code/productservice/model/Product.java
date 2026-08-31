package com.luv2code.productservice.model;

import com.luv2code.productservice.util.ProductCodeGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.math.BigDecimal;

import static jakarta.persistence.GenerationType.SEQUENCE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(
            strategy = SEQUENCE,
            generator = "products_id_seq"
    )
    @SequenceGenerator(
            name = "products_id_seq",
            sequenceName = "products_id_seq",
            allocationSize = 1
    )
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(min = 10, max = 10)
    @Column(name = "code", nullable = false, unique = true, length = 10)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @PositiveOrZero
    @Column(name = "price_eur", nullable = false, precision = 10, scale = 2)
    private BigDecimal priceEur;

    @PositiveOrZero
    @Column(name = "price_usd", nullable = false, precision = 10, scale = 2)
    private BigDecimal priceUsd;

    @Column(name = "is_available", nullable = false)
    private boolean available;

    @Builder
    public Product(String name, BigDecimal priceEur, BigDecimal priceUsd, boolean available) {
        Assert.hasText(name, "Product name cannot be empty");
        Assert.notNull(priceEur, "Product price in EUR cannot be null");
        Assert.notNull(priceUsd, "Product price in USD cannot be null");
        Assert.isTrue(priceEur.compareTo(BigDecimal.ZERO) >= 0,
                "Product price in EUR cannot be negative");
        Assert.isTrue(priceUsd.compareTo(BigDecimal.ZERO) >= 0,
                "Product price in USD cannot be negative");

        this.name = name;
        this.code = ProductCodeGenerator.generate();
        this.priceEur = priceEur;
        this.priceUsd = priceUsd;
        this.available = available;
    }
}
