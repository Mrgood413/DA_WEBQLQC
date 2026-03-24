package com.example.WebCafe.service;

import jakarta.servlet.http.HttpSession;

public interface CustomerSessionService {

	void setTable(HttpSession session, int tableNumber);

	/** Số bàn đã gán trong phiên, hoặc {@code null}. */
	Integer getTableNumber(HttpSession session);
}
