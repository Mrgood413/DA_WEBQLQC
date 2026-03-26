package com.example.WebCafe.controller;

import com.example.WebCafe.dto.request.LoginRequest;
import com.example.WebCafe.dto.request.RegisterCustomerRequest;
import com.example.WebCafe.dto.response.AuthMeResponse;
import com.example.WebCafe.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.security.web.context.SecurityContextRepository;

@Controller
@Validated
public class AuthController {

	private final AuthService authService;
	private final SecurityContextRepository securityContextRepository;

	public AuthController(AuthService authService, SecurityContextRepository securityContextRepository) {
		this.authService = authService;
		this.securityContextRepository = securityContextRepository;
	}

	/** Vào trang chủ → điều hướng tới đăng nhập. */
	@GetMapping("/")
	public String home() {
		return "redirect:/login";
	}

	/** Trang đăng nhập / chọn bàn (khách). */
	@GetMapping("/login")
	public String loginPage() {
		return "login";
	}

	/** Trang đăng ký khách hàng. */
	@GetMapping("/register")
	public String registerPage() {
		return "register";
	}

	/**
	 * GET vào URL API đăng nhập (bookmark, prefetch) — chuyển tới trang đăng nhập thay vì 405 + WARN trong log.
	 */
	@GetMapping("/api/auth/login")
	public String loginApiGetRedirect() {
		return "redirect:/login";
	}

	/**
	 * Đăng nhập: {@code mode=CUSTOMER} (khách, không cần user trong DB), {@code STAFF}, {@code ADMIN}.
	 * Phiên lưu bằng cookie {@code JSESSIONID}.
	 */
	@PostMapping("/api/auth/login")
	@ResponseBody
	public ResponseEntity<Void> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) {
		authService.login(request, httpRequest);
		// Ghi SecurityContext vào session — không có bước này request sau (vd. chọn bàn) không có ROLE_CUSTOMER.
		securityContextRepository.saveContext(SecurityContextHolder.getContext(), httpRequest, httpResponse);
		return ResponseEntity.ok().build();
	}

	/** GET nhầm tới URL logout API — chỉ chuyển trang (đăng xuất thật dùng POST). */
	@GetMapping("/api/auth/logout")
	public String logoutApiGetRedirect() {
		return "redirect:/login";
	}

	/** Xóa phiên và cookie phiên. */
	@PostMapping("/api/auth/logout")
	@ResponseBody
	public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
		authService.logout(request, response);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/api/auth/me")
	@ResponseBody
	public ResponseEntity<AuthMeResponse> me(HttpSession session) {
		return authService.getCurrentUser(session)
				.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
	}

	@PostMapping("/api/auth/register")
	@ResponseBody
	public ResponseEntity<Void> register(@Valid @RequestBody RegisterCustomerRequest request) {
		authService.registerCustomer(request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
}
