package com.example.WebCafe.service;

import com.example.WebCafe.dto.request.LoginRequest;
import com.example.WebCafe.dto.request.RegisterCustomerRequest;
import com.example.WebCafe.dto.response.AuthMeResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.Optional;

public interface AuthService {

	void login(LoginRequest request, HttpServletRequest httpRequest);

	void logout(HttpServletRequest request, HttpServletResponse response);

	Optional<AuthMeResponse> getCurrentUser(HttpSession session);

	void registerCustomer(RegisterCustomerRequest request);
}
