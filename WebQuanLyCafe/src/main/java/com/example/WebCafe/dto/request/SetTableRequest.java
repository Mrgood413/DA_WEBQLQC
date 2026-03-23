package com.example.WebCafe.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record SetTableRequest(
		@NotNull @Min(1) Integer tableNumber
) {
}
