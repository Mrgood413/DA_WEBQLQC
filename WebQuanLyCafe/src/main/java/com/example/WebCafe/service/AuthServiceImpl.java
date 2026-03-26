package com.example.WebCafe.service;


import com.example.WebCafe.dto.request.LoginRequest;
import com.example.WebCafe.dto.request.RegisterCustomerRequest;
import com.example.WebCafe.dto.response.AuthMeResponse;
import com.example.WebCafe.model.Customer;
import com.example.WebCafe.model.User;
import com.example.WebCafe.repository.AdminRepository;
import com.example.WebCafe.repository.CustomerRepository;
import com.example.WebCafe.repository.StaffRepository;
import com.example.WebCafe.repository.UserRepository;
import com.example.WebCafe.security.CustomerPrincipal;
import com.example.WebCafe.security.Roles;
import com.example.WebCafe.security.SessionKeys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;

@Service
public class AuthServiceImpl implements AuthService {

	private final UserRepository userRepository;
	private final StaffRepository staffRepository;
	private final AdminRepository adminRepository;
	private final CustomerRepository customerRepository;
	private final PasswordEncoder passwordEncoder;

	public AuthServiceImpl(UserRepository userRepository, StaffRepository staffRepository,
			AdminRepository adminRepository, CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.staffRepository = staffRepository;
		this.adminRepository = adminRepository;
		this.customerRepository = customerRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	@Transactional(readOnly = true)
	public void login(LoginRequest request, HttpServletRequest httpRequest) {
		switch (request.mode()) {
			case CUSTOMER -> loginCustomer(request, httpRequest);
			case STAFF -> loginStaff(request, httpRequest);
			case ADMIN -> loginAdmin(request, httpRequest);
		}
	}

	private void loginCustomer(LoginRequest request, HttpServletRequest httpRequest) {
		String username = request.username() == null ? "" : request.username().trim();
		String password = request.password() == null ? "" : request.password();
		// CUSTOMER có 2 mode:
		// - Guest tại quán: không truyền username/password
		// - Customer account: có username/password
		if (!username.isBlank() || !password.isBlank()) {
			User user = loadUserWithPasswordCheck(request);
			if (!customerRepository.existsById(user.getId())) {
				throw new BadCredentialsException("Tài khoản không phải khách hàng");
			}
			authenticateUser(user, Roles.CUSTOMER, httpRequest);
			return;
		}

		CustomerPrincipal guest = new CustomerPrincipal(UUID.randomUUID().toString());
		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
				guest,
				null,
				List.of(new SimpleGrantedAuthority(Roles.CUSTOMER)));
		auth.setDetails(new WebAuthenticationDetails(httpRequest));
		SecurityContextHolder.getContext().setAuthentication(auth);
		httpRequest.getSession(true);
	}

	private void loginStaff(LoginRequest request, HttpServletRequest httpRequest) {
		User user = loadUserWithPasswordCheck(request);
		if (!staffRepository.existsById(user.getId())) {
			throw new BadCredentialsException("Tài khoản không phải nhân viên");
		}
		authenticateUser(user, Roles.STAFF, httpRequest);
	}

	private void loginAdmin(LoginRequest request, HttpServletRequest httpRequest) {
		User user = loadUserWithPasswordCheck(request);
		if (!adminRepository.existsById(user.getId())) {
			throw new BadCredentialsException("Tài khoản không phải quản trị viên");
		}
		authenticateUser(user, Roles.ADMIN, httpRequest);
	}

	private User loadUserWithPasswordCheck(LoginRequest request) {
		String username = request.username();
		String password = request.password();
		if (username == null || username.isBlank() || password == null || password.isEmpty()) {
			throw new BadCredentialsException("Tên đăng nhập và mật khẩu không được để trống");
		}
		User user = userRepository.findByUsername(username.trim())
				.orElseThrow(() -> new BadCredentialsException("Sai tên đăng nhập hoặc mật khẩu"));
		String stored = user.getPassword();
		if (stored == null || stored.isEmpty()) {
			throw new BadCredentialsException("Tài khoản chưa đặt mật khẩu");
		}
		if (!passwordEncoder.matches(password, stored)) {
			throw new BadCredentialsException("Sai tên đăng nhập hoặc mật khẩu");
		}
		return user;
	}

	private void authenticateUser(User user, String role, HttpServletRequest httpRequest) {
		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
				user.getUsername(),
				null,
				List.of(new SimpleGrantedAuthority(role)));
		auth.setDetails(new WebAuthenticationDetails(httpRequest));
		SecurityContextHolder.getContext().setAuthentication(auth);
		httpRequest.getSession(true);
	}

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			new SecurityContextLogoutHandler().logout(request, response, auth);
		}
	}

	@Override
	public Optional<AuthMeResponse> getCurrentUser(HttpSession session) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated()) {
			return Optional.empty();
		}
		List<String> roles = auth.getAuthorities().stream()
				.map(a -> a.getAuthority().replace("ROLE_", ""))
				.collect(Collectors.toList());
		Integer tableNumber = (Integer) session.getAttribute(SessionKeys.TABLE_NUMBER);

		if (auth.getPrincipal() instanceof CustomerPrincipal cp) {
			return Optional.of(new AuthMeResponse("CUSTOMER", null, null, cp.guestId(), roles, tableNumber));
		}
		if (auth.getPrincipal() instanceof String username) {
			String mode = roles.contains("ADMIN") ? "ADMIN"
					: roles.contains("STAFF") ? "STAFF"
							: roles.contains("CUSTOMER") ? "CUSTOMER" : "UNKNOWN";
			String fullName = userRepository.findByUsername(username).map(User::getFullName).orElse(null);
			return Optional.of(new AuthMeResponse(mode, username, fullName, null, roles, tableNumber));
		}
		return Optional.empty();
	}

	@Override
	@Transactional
	public void registerCustomer(RegisterCustomerRequest request) {
		String username = request.username() != null ? request.username().trim() : "";
		String fullName = request.fullName() != null ? request.fullName().trim() : "";
		String address = request.address() != null ? request.address().trim() : "";
		if (username.isBlank() || fullName.isBlank() || address.isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Thiếu thông tin đăng ký");
		}
		if (userRepository.existsByUsername(username)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Tên đăng nhập đã tồn tại");
		}

		User u = new User();
		u.setUsername(username);
		u.setPassword(passwordEncoder.encode(request.password()));
		u.setFullName(fullName);
		u.setPhone(request.phone());
		userRepository.save(u);

		Customer customer = new Customer();
		customer.setUser(u);
		customer.setActive(true);
		customer.setAddress(address);
		customerRepository.save(customer);
	}
}
