package com.slamracing.ecommerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenResponse {
    @JsonProperty("access_token")
    @Schema(description = "Token de acceso JWT", example = "eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoiSnVhbiIsInN1YiI6Imp1YW5AZ21haWwuY29tIiwiaWF0IjoxNzMxMTkwNzQ5LCJleHAiOjE3MzEyNzcxNDl9.HTbv4ofPcMUEikWWmCfJf-S4ex4ds6CsM-gk45e7oAo")
    String accessToken;
    @JsonProperty("refresh_token")
    @Schema(description = "Token de refresco JWT", example = "eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoiSnVhbiIsInN1YiI6Imp1YW5AZ21haWwuY29tIiwiaWF0IjoxNzMxMTkwNzQ5LCJleHAiOjE3MzEyNzcxNDl9.HTbv4ofPcMUEikWWmCfJf-S4ex4ds6CsM-gk45e7oAo")
    String refreshToken;
}