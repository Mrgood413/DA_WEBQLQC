(function (window) {
	"use strict";

	/**
	 * Trang khách (/menu, /order) cần ROLE_CUSTOMER. Nếu đang đăng nhập STAFF/ADMIN
	 * hoặc chưa có phiên, gọi login mode CUSTOMER để tránh 403 khi gọi /api/customer/**.
	 */
	function loginCustomer() {
		return fetch("/api/auth/login", {
			method: "POST",
			headers: { "Content-Type": "application/json" },
			credentials: "same-origin",
			body: JSON.stringify({ mode: "CUSTOMER" })
		}).then(function (r) {
			if (!r.ok) throw new Error("Không tạo được phiên khách.");
			return r;
		});
	}

	function ensureCustomerSession() {
		return fetch("/api/auth/me", { credentials: "same-origin" })
			.then(function (r) {
				if (r.status === 401) {
					return loginCustomer();
				}
				return r.json().then(function (me) {
					if (me && me.mode === "CUSTOMER") {
						return;
					}
					return loginCustomer();
				});
			})
			.catch(function () {
				return loginCustomer();
			});
	}

	window.WebCafeCustomerSession = {
		ensure: ensureCustomerSession
	};
})(window);
