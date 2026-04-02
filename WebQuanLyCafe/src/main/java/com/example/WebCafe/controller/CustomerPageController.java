package com.example.WebCafe.controller;

import com.example.WebCafe.dto.response.ProductResponse;
import com.example.WebCafe.service.MenuService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class CustomerPageController {

	private final MenuService menuService;

	public CustomerPageController(MenuService menuService) {
		this.menuService = menuService;
	}

	/** Trang thực đơn (sau khi khách đã chọn bàn). */
	@GetMapping("/customer/menu")
	public String menuPage() {
		return "customer_menu";
	}

	/** Trang theo dõi đơn hàng. */
	@GetMapping("/customer/order")
	public String orderPage() {
		return "customer_order";
	}

	/** Danh sách món cho trang menu (JSON). */
	@GetMapping("/api/menu")
	@ResponseBody
	public List<ProductResponse> menuJson() {
		return menuService.listMenuItems();
	}
}

