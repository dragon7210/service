package com.example.springboot.dtos;

import jakarta.validation.constraints.NotBlank;

public record VegRecordDto(@NotBlank String firstname, @NotBlank String lastname, @NotBlank String address1, @NotBlank String country, @NotBlank String city, @NotBlank String state, @NotBlank String zip_code) {

}