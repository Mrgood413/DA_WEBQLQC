package com.example.WebCafe.controller;

import com.example.WebCafe.security.CustomerPrincipal;
import com.example.WebCafe.security.SessionKeys;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EntryRedirectController {

	@GetMapping("/menu")
	public String menu(HttpSession session) {
		return resolveTarget("/menu", session);
	}

	@GetMapping("/order")
	public String order(HttpSession session) {
		return resolveTarget("/order", session);
	}

	private String resolveTarget(String page, HttpSession session) {
		var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated()) {
			return "redirect:/login";
		}

		Object principal = auth.getPrincipal();
		boolean isCustomerRole = auth.getAuthorities().stream()
				.anyMatch(a -> "ROLE_CUSTOMER".equals(a.getAuthority()));
		boolean isStaffRole = auth.getAuthorities().stream()
				.anyMatch(a -> "ROLE_STAFF".equals(a.getAuthority()));
		boolean isAdminRole = auth.getAuthorities().stream()
				.anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

		if (isAdminRole) {
			return "redirect:/admin/dashboard";
		}
		if (isStaffRole) {
			return "redirect:" + ("/menu".equals(page) ? "/staff/menu" : "/staff/order");
		}

		if (isCustomerRole) {
			if (principal instanceof CustomerPrincipal) {
				Integer tableNumber = null;
				Object attr = session.getAttribute(SessionKeys.TABLE_NUMBER);
				if (attr instanceof Integer i) tableNumber = i;
				if (tableNumber == null) return "redirect:/login";
				return "redirect:" + ("/menu".equals(page)
						? ("/table/" + tableNumber + "/menu")
						: ("/table/" + tableNumber + "/order"));
			}
			if (principal instanceof String) {
				return "redirect:" + ("/menu".equals(page) ? "/customer/menu" : "/customer/order");
			}
		}

		return "redirect:/login";
	}
}

