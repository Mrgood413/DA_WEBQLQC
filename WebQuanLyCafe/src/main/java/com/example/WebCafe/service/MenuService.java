package com.example.WebCafe.service;

import com.example.WebCafe.dto.response.ProductResponse;

import java.util.List;

public interface MenuService {

	List<ProductResponse> listMenuItems();
}
