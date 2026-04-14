package com.example.WebCafe.service;

import com.example.WebCafe.dto.request.LoginMode;
import com.example.WebCafe.dto.request.LoginRequest;
import com.example.WebCafe.model.User;
import com.example.WebCafe.repository.AdminRepository;
import com.example.WebCafe.repository.CustomerRepository;
import com.example.WebCafe.repository.StaffRepository;
import com.example.WebCafe.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

	@Mock
	private UserRepository userRepository;
	@Mock
	private StaffRepository staffRepository;
	@Mock
	private AdminRepository adminRepository;
	@Mock
	private CustomerRepository customerRepository;
	@Mock
	private PasswordEncoder passwordEncoder;
	@Mock
	private HttpServletRequest httpServletRequest;
	@Mock
	private HttpSession httpSession;

	@InjectMocks
	private AuthServiceImpl authService;

	@AfterEach
	void tearDown() {
		SecurityContextHolder.clearContext();
	}

	@Test
	void wbLogin01_customerGuest_blankUsernamePassword_createsGuestSession() {
		when(httpServletRequest.getSession(false)).thenReturn(httpSession);
		when(httpServletRequest.getSession(true)).thenReturn(httpSession);

		authService.login(new LoginRequest(LoginMode.CUSTOMER, "", ""), httpServletRequest);

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		assertNotNull(authentication);
		assertTrue(authentication.getAuthorities().stream()
				.anyMatch(a -> "ROLE_CUSTOMER".equals(a.getAuthority())));
		assertEquals("CustomerPrincipal", authentication.getPrincipal().getClass().getSimpleName());
		verify(httpServletRequest).getSession(true);
	}

	@Test
	void wbLogin02_customerAccount_validCredentials_authenticatesCustomerRole() {
		User user = user(11, "cust01", "encoded");
		when(userRepository.findByUsername("cust01")).thenReturn(Optional.of(user));
		when(passwordEncoder.matches("123456", "encoded")).thenReturn(true);
		when(customerRepository.existsById(11)).thenReturn(true);
		when(httpServletRequest.getSession(false)).thenReturn(httpSession);
		when(httpServletRequest.getSession(true)).thenReturn(httpSession);

		authService.login(new LoginRequest(LoginMode.CUSTOMER, "cust01", "123456"), httpServletRequest);

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		assertNotNull(authentication);
		assertEquals("cust01", authentication.getPrincipal());
		assertTrue(authentication.getAuthorities().stream()
				.anyMatch(a -> "ROLE_CUSTOMER".equals(a.getAuthority())));
	}

	@Test
	void wbLogin03_customerAccount_wrongRole_throwsBadCredentials() {
		User user = user(21, "staff01", "encoded");
		when(userRepository.findByUsername("staff01")).thenReturn(Optional.of(user));
		when(passwordEncoder.matches("123456", "encoded")).thenReturn(true);
		when(customerRepository.existsById(21)).thenReturn(false);

		BadCredentialsException ex = assertThrows(
				BadCredentialsException.class,
				() -> authService.login(new LoginRequest(LoginMode.CUSTOMER, "staff01", "123456"), httpServletRequest));

		assertTrue(ex.getMessage().contains("không phải khách hàng"));
	}

	@Test
	void wbLogin04_staffPath_validCredentials_authenticatesStaffRole() {
		User user = user(31, "staff01", "encoded");
		when(userRepository.findByUsername("staff01")).thenReturn(Optional.of(user));
		when(passwordEncoder.matches("123456", "encoded")).thenReturn(true);
		when(staffRepository.existsById(31)).thenReturn(true);
		when(httpServletRequest.getSession(false)).thenReturn(httpSession);
		when(httpServletRequest.getSession(true)).thenReturn(httpSession);

		authService.login(new LoginRequest(LoginMode.STAFF, "staff01", "123456"), httpServletRequest);

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		assertNotNull(authentication);
		assertEquals("staff01", authentication.getPrincipal());
		assertTrue(authentication.getAuthorities().stream()
				.anyMatch(a -> "ROLE_STAFF".equals(a.getAuthority())));
	}

	@Test
	void wbLogin04_adminPath_validCredentials_authenticatesAdminRole() {
		User user = user(41, "admin01", "encoded");
		when(userRepository.findByUsername("admin01")).thenReturn(Optional.of(user));
		when(passwordEncoder.matches("123456", "encoded")).thenReturn(true);
		when(adminRepository.existsById(41)).thenReturn(true);
		when(httpServletRequest.getSession(false)).thenReturn(httpSession);
		when(httpServletRequest.getSession(true)).thenReturn(httpSession);

		authService.login(new LoginRequest(LoginMode.ADMIN, "admin01", "123456"), httpServletRequest);

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		assertNotNull(authentication);
		assertEquals("admin01", authentication.getPrincipal());
		assertTrue(authentication.getAuthorities().stream()
				.anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority())));
	}

	private User user(int id, String username, String encodedPassword) {
		User user = new User();
		user.setId(id);
		user.setUsername(username);
		user.setPassword(encodedPassword);
		assertInstanceOf(Integer.class, user.getId());
		return user;
	}
}
