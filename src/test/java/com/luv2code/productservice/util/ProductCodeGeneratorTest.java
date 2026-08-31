package com.luv2code.productservice.util;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ProductCodeGeneratorTest {

    private static final String ALLOWED_CHARACTERS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    @Test
    void generate_returnsCodeWithExactlyTenCharacters() {
        String code = ProductCodeGenerator.generate();

        assertThat(code)
                .isNotNull()
                .hasSize(10);
    }

    @Test
    void generate_returnsCodeContainingOnlyAllowedCharacters() {
        String code = ProductCodeGenerator.generate();

        assertThat(code.chars()
                .mapToObj(c -> (char) c)
                .allMatch(character -> ALLOWED_CHARACTERS.indexOf(character) >= 0))
                .isTrue();
    }

    @Test
    void generate_generatesDifferentCodes() {
        Set<String> codes = new HashSet<>();

        for (int i = 0; i < 100; i++) {
            codes.add(ProductCodeGenerator.generate());
        }

        assertThat(codes).hasSize(100);
    }
}