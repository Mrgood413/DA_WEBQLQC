package com.example.WebCafe.dto.request;

import com.example.WebCafe.model.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;

public record PaymentRequest(
		@NotNull PaymentMethod method
) {
}
