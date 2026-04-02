package com.example.WebCafe.controller;

import com.example.WebCafe.security.CustomerPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CustomerProfilePageController {

	@GetMapping("/customer/profile")
	public String profilePage() {
		var auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof CustomerPrincipal) {
			// Guest đặt tại quán theo bàn: không có trang chỉnh sửa tài khoản.
			return "redirect:/menu";
		}
		return "customer_profile";
	}
}

