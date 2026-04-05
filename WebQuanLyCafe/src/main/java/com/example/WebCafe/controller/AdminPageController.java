package com.example.WebCafe.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminPageController {

	@GetMapping("/admin")
	public String adminHome() {
		return "redirect:/admin/dashboard";
	}

	@GetMapping("/admin/dashboard")
	public String adminDashboardPage() {
		return "admin_dashboard";
	}

	@GetMapping("/admin/category")
	public String adminCategoryPage() {
		return "admin_category";
	}

	@GetMapping("/admin/products")
	public String adminProductsPage() {
		return "admin_products";
	}

	@GetMapping("/admin/staff")
	public String adminStaffPage() {
		return "admin_staff";
	}

	@GetMapping("/admin/revenue")
	public String adminRevenuePage() {
		return "admin_revenue";
	}
}
