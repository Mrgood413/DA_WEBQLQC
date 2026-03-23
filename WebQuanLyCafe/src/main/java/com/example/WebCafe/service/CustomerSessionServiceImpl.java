package com.example.WebCafe.service;

import com.example.WebCafe.repository.CafeTableRepository;
import com.example.WebCafe.security.SessionKeys;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CustomerSessionServiceImpl implements CustomerSessionService {

	private final CafeTableRepository cafeTableRepository;

	public CustomerSessionServiceImpl(CafeTableRepository cafeTableRepository) {
		this.cafeTableRepository = cafeTableRepository;
	}

	@Override
	public void setTable(HttpSession session, int tableNumber) {
		cafeTableRepository.findByTableNumber(tableNumber)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bàn không tồn tại"));
		session.setAttribute(SessionKeys.TABLE_NUMBER, tableNumber);
	}
}
