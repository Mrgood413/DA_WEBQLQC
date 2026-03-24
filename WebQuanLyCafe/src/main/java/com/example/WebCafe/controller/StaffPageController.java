package com.example.WebCafe.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StaffPageController {

	@GetMapping("/staff/menu")
	public String staffMenuPage() {
		return "staff_menu";
	}

	@GetMapping("/staff/order")
	public String staffOrderPage() {
		return "staff_order";
	}
}
