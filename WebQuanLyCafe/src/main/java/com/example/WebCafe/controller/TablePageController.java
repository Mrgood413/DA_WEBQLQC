package com.example.WebCafe.controller;

import com.example.WebCafe.service.CustomerSessionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class TablePageController {

	private final CustomerSessionService customerSessionService;

	public TablePageController(CustomerSessionService customerSessionService) {
		this.customerSessionService = customerSessionService;
	}

	@GetMapping("/table/{tableNumber}/menu")
	public String tableMenu(@PathVariable int tableNumber, HttpSession session) {
		customerSessionService.setTable(session, tableNumber);
		return "customer_menu";
	}

	@GetMapping("/table/{tableNumber}/order")
	public String tableOrder(@PathVariable int tableNumber, HttpSession session) {
		customerSessionService.setTable(session, tableNumber);
		return "customer_order";
	}
}

