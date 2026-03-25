package com.example.WebCafe.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminPageController {

	@GetMapping("/admin")
	public String adminHome() {
		return "redirect:/admin/category";
	}

	@GetMapping("/admin/category")
	public String adminCategoryPage() {
		return "admin_category";
	}
}
