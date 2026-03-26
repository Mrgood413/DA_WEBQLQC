(function () {
	"use strict";

	var tableNumberCache = null;
	var TABLE_NUMBER_KEY = "webcafe.tableNumber";
	var ORDER_ID_KEY = "webcafe.orderId";
	var SENT_ITEMS_KEY = "webcafe.sentItems";
	var MORE_PENDING_KEY = "webcafe.morePending";
	var MORE_SNAPSHOT_KEY = "webcafe.moreSnapshot";
	var isAccountCustomer = false;
	var accountProfile = null;

	/** index = cart.status: 0…4 (4 bước milestone + trạng thái ban đầu) */
	var STATUS_WIDTHS = ["0%", "25%", "50%", "75%", "100%"];
	var PLACEHOLDER_IMG =
		"data:image/svg+xml," +
		encodeURIComponent(
			'<svg xmlns="http://www.w3.org/2000/svg" width="96" height="96" viewBox="0 0 96 96"><rect fill="#efe8d2" width="96" height="96"/><text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" fill="#504442" font-family="sans-serif" font-size="10">Anh mon</text></svg>'
		);

	function formatVnd(n) {
		var num = typeof n === "number" ? n : parseFloat(n);
		return new Intl.NumberFormat("vi-VN").format(isNaN(num) ? 0 : num) + "đ";
	}

	function escapeHtml(value) {
		var div = document.createElement("div");
		div.textContent = value == null ? "" : String(value);
		return div.innerHTML;
	}

	function getImageUrl(item) {
		return item && item.imageUrl ? escapeHtml(item.imageUrl) : PLACEHOLDER_IMG;
	}

	function formatTime(isoString) {
		if (!isoString) return "Chưa đặt đơn";
		var date = new Date(isoString);
		if (isNaN(date.getTime())) return "Chưa đặt đơn";
		return "Đặt lúc " + date.toLocaleTimeString("vi-VN", {
			hour: "2-digit",
			minute: "2-digit",
			hour12: false
		});
	}

	function getTableLabel(tableNumber) {
		if (isAccountCustomer) return "Đơn giao hàng";
		if (tableNumber != null && tableNumber !== "") {
			return "Bàn " + tableNumber;
		}
		return "Đơn hàng";
	}

	function safeJsonParse(raw, fallback) {
		if (raw == null) return fallback;
		try {
			return JSON.parse(raw);
		} catch (e) {
			return fallback;
		}
	}

	function apiJson(url, opts) {
		return fetch(url, Object.assign({ credentials: "same-origin" }, opts || {})).then(function (res) {
			return res.text().then(function (text) {
				var data = null;
				try {
					data = text ? JSON.parse(text) : null;
				} catch (e) {
					data = null;
				}
				if (!res.ok) {
					var msg = (data && data.message) ? data.message :
						(data && data.error) ? data.error :
							(text ? text : ("HTTP " + res.status));
					var err = new Error(msg);
					err.status = res.status;
					throw err;
				}
				return data;
			});
		});
	}

	function setupUserHeader() {
		var userEl = document.getElementById("current-username");
		var logoutBtn = document.getElementById("logout-btn");

		apiJson("/api/auth/me", { method: "GET" })
			.then(function (me) {
				if (!userEl) return;
				if (me && me.username) userEl.textContent = me.username;
				else if (me && me.fullName) userEl.textContent = me.fullName;
				else userEl.textContent = "Khách";
			})
			.catch(function () {
				if (userEl) userEl.textContent = "Khách";
			});

		if (logoutBtn) {
			logoutBtn.addEventListener("click", function () {
				fetch("/api/auth/logout", {
					method: "POST",
					credentials: "same-origin"
				}).finally(function () {
					try {
						localStorage.removeItem("webcafe.cart");
						localStorage.removeItem("webcafe.orderId");
						localStorage.removeItem("webcafe.tableNumber");
						localStorage.removeItem("webcafe.sentItems");
						localStorage.removeItem("webcafe.morePending");
						localStorage.removeItem("webcafe.moreSnapshot");
					} catch (e) {}
					window.location.href = "/login";
				});
			});
		}
	}

	function getSelectedPaymentMethod() {
		var checked = document.querySelector('input[name="paymentMethod"]:checked');
		return checked ? checked.value : "";
	}

	function getDeliveryAddressInput() {
		var input = document.getElementById("delivery-address-input");
		return input ? String(input.value || "").trim() : "";
	}

	function isUseProfileAddressChecked() {
		var cb = document.getElementById("use-profile-address");
		return !!(cb && cb.checked);
	}

	function applyAccountCustomerLayout() {
		var statusHeader = document.getElementById("order-status-header");
		var milestone = document.getElementById("milestone-block");
		var addMoreSection = document.getElementById("add-more-section");
		var checkoutBox = document.getElementById("customer-checkout-box");
		if (statusHeader) statusHeader.classList.toggle("hidden", isAccountCustomer);
		if (milestone) milestone.classList.toggle("hidden", isAccountCustomer);
		if (addMoreSection) addMoreSection.classList.toggle("hidden", isAccountCustomer);
		if (checkoutBox) checkoutBox.classList.toggle("hidden", !isAccountCustomer);

		var label = document.getElementById("use-profile-address-label");
		if (label && accountProfile && accountProfile.fullName) {
			label.textContent = "Sử dụng địa chỉ của bạn (" + accountProfile.fullName + ")";
		}
	}

	function formatDateTime(isoString) {
		if (!isoString) return "Vừa tạo";
		var d = new Date(isoString);
		if (isNaN(d.getTime())) return "Vừa tạo";
		return d.toLocaleString("vi-VN", {
			year: "numeric",
			month: "2-digit",
			day: "2-digit",
			hour: "2-digit",
			minute: "2-digit",
			hour12: false
		});
	}

	function mapDeliveryStatusLabel(status) {
		var s = status == null ? "" : String(status);
		if (s === "PENDING") return "Chờ xác nhận";
		if (s === "CONFIRMED") return "Đã xác nhận";
		if (s === "SHIPPING") return "Đang giao";
		if (s === "DELIVERED") return "Đã giao";
		if (s === "CANCELLED") return "Đã hủy";
		return s || "—";
	}

	function mapPaymentMethodLabel(method) {
		var m = method == null ? "" : String(method);
		if (m === "CASH") return "Tiền mặt";
		if (m === "BANKING") return "Chuyển khoản";
		return m || "—";
	}

	function renderDeliveryHistory(deliveries) {
		var listEl = document.getElementById("delivery-history-list");
		if (!listEl) return;
		var arr = Array.isArray(deliveries) ? deliveries : [];
		if (!arr.length) {
			listEl.innerHTML = "<p>Chưa có đơn giao hàng.</p>";
			return;
		}
		listEl.innerHTML = arr.map(function (d) {
			var items = Array.isArray(d.orderedItems) ? d.orderedItems : [];
			var itemsText = items.length ? items.join(", ") : "—";
			return (
				'<div class="rounded-xl bg-surface-container-high px-3 py-2">' +
				'<p class="font-semibold text-on-surface">Đơn #' + (d.orderId == null ? "—" : d.orderId) + "</p>" +
				'<p class="text-xs">Phương thức thanh toán: ' + escapeHtml(mapPaymentMethodLabel(d.paymentMethod)) + "</p>" +
				'<p class="text-xs">Địa chỉ: ' + escapeHtml(d.address || "—") + "</p>" +
				'<p class="text-xs">Món đã đặt: ' + escapeHtml(itemsText) + "</p>" +
				'<p class="text-xs">Tạo lúc: ' + escapeHtml(formatDateTime(d.createdAt)) + "</p>" +
				"</div>"
			);
		}).join("");
	}

	function loadDeliveryHistory() {
		if (!isAccountCustomer) return Promise.resolve();
		return apiJson("/api/customer/deliveries", { method: "GET" })
			.then(function (rows) {
				renderDeliveryHistory(rows);
			})
			.catch(function () {
				renderDeliveryHistory([]);
			});
	}

	function readItemQtyMap(storageKey) {
		var raw = window.localStorage ? window.localStorage.getItem(storageKey) : null;
		var parsed = safeJsonParse(raw, null);
		if (!parsed || typeof parsed !== "object") return {};
		return parsed;
	}

	function writeItemQtyMap(storageKey, map) {
		if (!window.localStorage) return;
		window.localStorage.setItem(storageKey, JSON.stringify(map || {}));
	}

	function getCartItemQtyMap(cart) {
		var map = {};
		if (!cart || !Array.isArray(cart.items)) return map;
		cart.items.forEach(function (it) {
			if (it == null || it.id == null) return;
			map[String(it.id)] = Math.max(0, parseInt(it.quantity, 10) || 0);
		});
		return map;
	}

	function readMorePending() {
		var raw = window.localStorage ? window.localStorage.getItem(MORE_PENDING_KEY) : null;
		return !!safeJsonParse(raw, false);
	}

	function writeMorePending(flag) {
		if (!window.localStorage) return;
		window.localStorage.setItem(MORE_PENDING_KEY, JSON.stringify(!!flag));
	}

	function getMilestones() {
		return [
			{
				el: document.getElementById("milestone-1"),
				label: document.getElementById("milestone-1").parentElement.querySelector(".milestone-label")
			},
			{
				el: document.getElementById("milestone-2"),
				label: document.getElementById("milestone-2").parentElement.querySelector(".milestone-label")
			},
			{
				el: document.getElementById("milestone-3"),
				label: document.getElementById("milestone-3").parentElement.querySelector(".milestone-label")
			},
			{
				el: document.getElementById("milestone-4"),
				label: document.getElementById("milestone-4").parentElement.querySelector(".milestone-label")
			}
		];
	}

	function applyProgress(state) {
		var progressLine = document.getElementById("progressLine");
		var milestones = getMilestones();
		var s = Math.max(0, Math.min(4, parseInt(state, 10) || 0));

		progressLine.style.width = STATUS_WIDTHS[s] || "0%";
		milestones.forEach(function (milestone, index) {
			var active = index < s;
			if (milestone.el) {
				milestone.el.classList.remove("animate-bounce-once", "animate-success-pulse");
				milestone.el.classList.toggle("milestone-active", active);
			}
			if (milestone.label) {
				milestone.label.classList.toggle("opacity-40", !active);
				milestone.label.classList.toggle("text-secondary", active);
				milestone.label.classList.toggle("opacity-100", active);
			}
		});
		if (s >= 1 && s <= 4) {
			var idx = s - 1;
			var m = milestones[idx];
			if (m && m.el) {
				window.requestAnimationFrame(function () {
					if (s === 4) m.el.classList.add("animate-success-pulse");
					else m.el.classList.add("animate-bounce-once");
				});
			}
		}
	}

	function syncMilestoneInteractivity(cart) {
		var milestones = getMilestones();
		// Khách không được bấm milestone (UI chỉ cập nhật qua staff event/polling).
		var lock = true;
		milestones.forEach(function (m) {
			if (!m || !m.el) return;
			m.el.classList.toggle("pointer-events-none", lock);
			m.el.classList.toggle("cursor-not-allowed", lock);
			if (m.label) {
				m.label.classList.toggle("pointer-events-none", lock);
				m.label.classList.toggle("cursor-not-allowed", lock);
			}
		});
	}

	function playCancelAnimation(onComplete) {
		var progressLine = document.getElementById("progressLine");
		var milestones = getMilestones();

		progressLine.style.width = "0%";

		milestones.forEach(function (milestone, index) {
			window.setTimeout(function () {
				if (milestone.el) {
					milestone.el.classList.remove("milestone-active", "animate-bounce-once", "animate-success-pulse");
				}
				if (milestone.label) {
					milestone.label.classList.add("opacity-40");
					milestone.label.classList.remove("text-secondary", "opacity-100");
				}
			}, index * 100);
		});

		window.setTimeout(function () {
			if (typeof onComplete === "function") {
				onComplete();
			}
		}, 400);
	}

	function updateButtonState(cart) {
		var button = document.getElementById("mainOrderBtn");
		var text = document.getElementById("mainOrderBtnText");
		var addMoreBtn = document.getElementById("add-more-btn");
		var addMoreTextEl = document.getElementById("add-more-btn-text");
		var hasItems = cart.items.length > 0;
		var canAddMore = (cart.status === 2 || cart.status === 3) && hasItems;

		if (isAccountCustomer) {
			if (addMoreBtn) addMoreBtn.disabled = true;
			button.disabled = !hasItems;
			button.classList.remove("btn-ordered", "opacity-60");
			button.classList.toggle("opacity-50", !hasItems);
			button.classList.toggle("cursor-not-allowed", !hasItems);
			text.textContent = hasItems ? "Đặt hàng" : "Chọn món ở thực đơn";
			return;
		}

		button.classList.remove("btn-ordered", "bg-secondary", "opacity-60");
		if (addMoreBtn) {
			addMoreBtn.disabled = !canAddMore;
			addMoreBtn.classList.toggle("opacity-50", !canAddMore);
			addMoreBtn.classList.toggle("cursor-not-allowed", !canAddMore);

			if (addMoreTextEl) {
				if (cart.status === 2 || cart.status === 3) {
					var morePending = readMorePending();
					addMoreTextEl.textContent = morePending ? "Gửi thêm" : "Gọi thêm";
				} else {
					addMoreTextEl.textContent = "Gọi thêm";
				}
			}
		}

		// Trạng thái 2–4 do nhân viên / thanh toán; khách không bấm nút chính để đổi milestone.
		if (cart.status === 2) {
			button.disabled = true;
			button.classList.add("opacity-60", "cursor-not-allowed");
			button.classList.remove("opacity-50");
			text.textContent = "ĐANG THỰC HIỆN...";
			return;
		}
		if (cart.status === 3) {
			button.disabled = true;
			button.classList.add("opacity-60", "cursor-not-allowed");
			button.classList.remove("opacity-50");
			text.textContent = "ĐÃ HOÀN THÀNH";
			return;
		}
		if (cart.status === 4) {
			button.disabled = true;
			button.classList.add("opacity-60", "cursor-not-allowed");
			button.classList.remove("opacity-50");
			text.textContent = "ĐÃ THANH TOÁN";
			return;
		}

		button.disabled = !hasItems;
		button.classList.toggle("opacity-50", !hasItems);
		button.classList.toggle("cursor-not-allowed", !hasItems);

		if (!hasItems) {
			text.textContent = "Chọn món ở thực đơn";
			return;
		}

		if (cart.status === 0) {
			text.textContent = "Đặt đơn";
			return;
		}

		if (cart.status === 1) {
			button.classList.add("btn-ordered");
			text.textContent = "HỦY ĐƠN";
			return;
		}
	}

	/**
	 * Hook để nhân viên xác nhận (milestone-2/3) cập nhật UI.
	 * Hiện tại backend đang TODO, nên hàm này chỉ được gọi khi bạn nối EventSource/WebSocket/polling.
	 */
	function applyStaffMilestone(milestone) {
		var next = parseInt(milestone, 10);
		if (isNaN(next)) return;
		if (next < 2 || next > 4) return;
		window.WebCafeCart.setStatus(next);
		// Sau khi thanh toán (milestone 4): không còn gọi thêm
		if (next === 4) {
			writeMorePending(false);
			window.localStorage.removeItem(MORE_SNAPSHOT_KEY);
			window.localStorage.removeItem(SENT_ITEMS_KEY);
		}
		renderCart();
	}

	function subscribeOrderEvents(orderId) {
		if (!orderId) return;
		if (typeof EventSource !== "function") return;
		try {
			var es = new EventSource("/api/customer/orders/" + orderId + "/events");
			es.addEventListener("milestone", function (e) {
				var milestone = parseInt(e.data, 10);
				applyStaffMilestone(milestone);
			});
			es.onerror = function () {
				// không làm gì, EventSource sẽ tự retry
			};
		} catch (err) {}
	}

	var orderStatusPollTimer = null;

	function stopOrderStatusPolling() {
		if (orderStatusPollTimer) {
			clearInterval(orderStatusPollTimer);
			orderStatusPollTimer = null;
		}
	}

	function mapServerStatusToCart(status) {
		var s = status == null ? "" : String(status);
		if (s === "PENDING") return 1; // milestone-1
		if (s === "PREPARING") return 2; // milestone-2
		if (s === "DONE") return 3; // milestone-3
		if (s === "PAID") return 4;
		return null;
	}

	function startOrderStatusPolling(orderId) {
		if (!orderId) return;
		stopOrderStatusPolling();
		orderStatusPollTimer = setInterval(function () {
			var cart = window.WebCafeCart ? window.WebCafeCart.getCart() : null;
			if (!cart) return;
			if (cart.status >= 4) return;

			apiJson("/api/customer/orders/" + orderId, { method: "GET" })
				.then(function (res) {
					if (!res || !res.status) return;
					var next = mapServerStatusToCart(res.status);
					if (next == null) return;
					if (next !== cart.status) {
						window.WebCafeCart.setStatus(next);
						renderCart();
					}
					if (next >= 4) stopOrderStatusPolling();
				})
				.catch(function () {
					// Không alert khi poll thất bại (có thể do 401 khi hết phiên)
				});
		}, 2500);
	}

	function changeCartItemQuantity(productId, delta) {
		var cart = window.WebCafeCart ? window.WebCafeCart.getCart() : { items: [], status: 0 };
		var item = null;

		if (cart.status !== 0) return;

		cart.items.forEach(function (cartItem) {
			if (String(cartItem.id) === String(productId)) {
				item = cartItem;
			}
		});

		if (!item) return;

		window.WebCafeCart.upsertCartItem(item, item.quantity + delta);
		renderCart();
	}

	function updateCartRow(row, item, index, editable) {
		var delayIndex = Math.min(index + 1, 4);
		var disabledClass = editable ? "" : " opacity-50 cursor-not-allowed";
		row.className = "flex justify-between items-center py-2 gap-4 animate-card delay-" + delayIndex;
		row.setAttribute("data-product-id", String(item.id));
		row.innerHTML =
			'<div class="flex items-center gap-4 min-w-0">' +
			'<img class="w-14 h-14 rounded-xl object-cover bg-surface-container shrink-0" src="' +
			getImageUrl(item) +
			'" alt="' +
			escapeHtml(item.name) +
			'">' +
			'<span class="text-xs font-bold w-8 text-secondary shrink-0">' +
			item.quantity +
			"x</span>" +
			'<div class="min-w-0">' +
			'<span class="text-sm font-medium block truncate">' +
			escapeHtml(item.name) +
			"</span>" +
			'<div class="flex items-center gap-2 mt-2">' +
			'<button type="button" class="order-qty-btn order-qty-decrease w-7 h-7 rounded-full bg-surface text-primary' +
			disabledClass +
			'"' +
			(editable ? "" : " disabled") +
			">-</button>" +
			'<button type="button" class="order-qty-btn order-qty-increase w-7 h-7 rounded-full bg-surface text-primary' +
			disabledClass +
			'"' +
			(editable ? "" : " disabled") +
			">+</button>" +
			"</div>" +
			"</div>" +
			"</div>" +
			'<span class="text-sm font-bold shrink-0">' +
			formatVnd(item.quantity * item.price) +
			"</span>";

		var image = row.querySelector("img");
		image.onerror = function () {
			this.onerror = null;
			this.src = PLACEHOLDER_IMG;
		};

		row.querySelector(".order-qty-decrease").addEventListener("click", function () {
			changeCartItemQuantity(item.id, -1);
		});
		row.querySelector(".order-qty-increase").addEventListener("click", function () {
			changeCartItemQuantity(item.id, 1);
		});
	}

	function renderCart() {
		var cart = window.WebCafeCart ? window.WebCafeCart.getCart() : { items: [], status: 0 };
		var itemsEl = document.getElementById("order-items");
		var totalEl = document.getElementById("order-total");
		var headerTotalEl = document.getElementById("order-header-total");
		var countBadgeEl = document.getElementById("mobile-cart-count");
		var orderCodeEl = document.getElementById("order-code");
		var orderTimeEl = document.getElementById("order-time");
		var totals = window.WebCafeCart ? window.WebCafeCart.getTotals(cart) : { itemCount: 0, total: 0 };
		var editable = cart.status === 0;

		orderCodeEl.textContent = getTableLabel(tableNumberCache);
		orderTimeEl.textContent = formatTime(cart.placedAt);
		if (countBadgeEl) countBadgeEl.textContent = totals.itemCount;
		totalEl.textContent = formatVnd(totals.total);
		if (headerTotalEl) headerTotalEl.textContent = formatVnd(totals.total);

		if (!cart.items.length) {
			itemsEl.innerHTML =
				'<div data-empty-state="true" class="py-6 text-center text-on-surface-variant">' +
				'<p class="text-sm font-medium">Giỏ hàng của bạn đang trống.</p>' +
				'<p class="text-xs mt-2">Quay lại trang thực đơn để thêm món.</p>' +
				"</div>";
			if (!isAccountCustomer) applyProgress(0);
			updateButtonState(cart);
			return;
		}

		Array.prototype.slice.call(itemsEl.querySelectorAll('[data-empty-state="true"]')).forEach(function (emptyState) {
			emptyState.remove();
		});

		// Gắn "Món thêm" theo baseline local:
		// - Khi morePending=true: so với MORE_SNAPSHOT_KEY để biết món nào được thêm sau lần gọi gần nhất.
		// - Khi morePending=false: không cần phân cách.
		itemsEl.querySelectorAll('[data-divider="more"]').forEach(function (d) {
			d.remove();
		});

		var morePending = (cart.status === 2 || cart.status === 3) && readMorePending();
		var baselineMap = morePending ? readItemQtyMap(MORE_SNAPSHOT_KEY) : readItemQtyMap(SENT_ITEMS_KEY);
		if (morePending && (!baselineMap || Object.keys(baselineMap).length === 0)) {
			baselineMap = getCartItemQtyMap(cart);
		}
		var dividerInserted = false;

		var seen = {};

		cart.items.forEach(function (item, index) {
			var productId = String(item.id);

			// Chèn divider ngay trước món "mới" (món có quantity > baseline)
			if (!dividerInserted && morePending) {
				var baseQty = baselineMap && baselineMap[productId] ? baselineMap[productId] : 0;
				if (item.quantity > baseQty) {
					var divider = document.createElement("div");
					divider.setAttribute("data-divider", "more");
					divider.className = "my-4 px-4 py-2 bg-surface-container-lowest border border-outline-variant/20 rounded-xl";
					divider.innerHTML = '<p class="text-[10px] font-bold uppercase tracking-tight text-secondary">Món được thêm</p>';
					itemsEl.appendChild(divider);
					dividerInserted = true;
				}
			}

			var row = itemsEl.querySelector('[data-product-id="' + productId + '"]');
			if (!row) {
				row = document.createElement("div");
			}
			updateCartRow(row, item, index, editable);
			itemsEl.appendChild(row);
			seen[productId] = true;
		});

		Array.prototype.slice.call(itemsEl.querySelectorAll("[data-product-id]")).forEach(function (row) {
			if (!seen[row.getAttribute("data-product-id")]) {
				row.remove();
			}
		});

		if (!isAccountCustomer) {
			applyProgress(cart.status || 0);
			syncMilestoneInteractivity(cart);
		}
		updateButtonState(cart);
	}

	function readStoredTableNumber() {
		try {
			var raw = window.localStorage.getItem(TABLE_NUMBER_KEY);
			if (raw == null || raw === "") return null;
			var n = parseInt(raw, 10);
			if (isNaN(n) || n < 1) return null;
			return n;
		} catch (e) {
			return null;
		}
	}

	function fetchTableNumber(done) {
		tableNumberCache = readStoredTableNumber();
		fetch("/api/customer/table", { credentials: "same-origin" })
			.then(function (r) {
				if (r.status === 401) {
					tableNumberCache = readStoredTableNumber();
					return null;
				}
				if (!r.ok) {
					tableNumberCache = readStoredTableNumber();
					return null;
				}
				return r.json();
			})
			.then(function (data) {
				if (!data) return;
				if (data.tableNumber != null && data.tableNumber !== "") {
					tableNumberCache = data.tableNumber;
					try {
						window.localStorage.setItem(TABLE_NUMBER_KEY, String(data.tableNumber));
					} catch (e) {}
				} else {
					tableNumberCache = readStoredTableNumber();
				}
			})
			.catch(function () {
				tableNumberCache = readStoredTableNumber();
			})
			.finally(function () {
				if (typeof done === "function") done();
			});
	}

	document.addEventListener("DOMContentLoaded", function () {
		function bootOrderPage() {
		var mainOrderBtn = document.getElementById("mainOrderBtn");
		var addMoreBtn = document.getElementById("add-more-btn");
		setupUserHeader();
		function setupGuestOrderTracking() {
			var storedOrderId = null;
			try {
				storedOrderId = parseInt(window.localStorage.getItem(ORDER_ID_KEY), 10);
			} catch (e) {}
			if (storedOrderId && !isNaN(storedOrderId)) {
				subscribeOrderEvents(storedOrderId);
				startOrderStatusPolling(storedOrderId);
			}
		}

		apiJson("/api/customer/profile", { method: "GET" })
			.then(function (profile) {
				accountProfile = profile || null;
				isAccountCustomer = !!accountProfile;
				var useProfile = document.getElementById("use-profile-address");
				var addrInput = document.getElementById("delivery-address-input");
				if (useProfile && addrInput) {
					useProfile.addEventListener("change", function () {
						var lock = !!useProfile.checked;
						addrInput.disabled = lock;
						if (lock && accountProfile && accountProfile.address) {
							addrInput.value = accountProfile.address;
						}
					});
				}
			})
			.catch(function () {
				isAccountCustomer = false;
				accountProfile = null;
			})
			.finally(function () {
				applyAccountCustomerLayout();
				if (isAccountCustomer) {
					tableNumberCache = null;
					stopOrderStatusPolling();
					renderCart();
					loadDeliveryHistory();
					return;
				}
				setupGuestOrderTracking();
				fetchTableNumber(renderCart);
			});

		// Chặn click milestone (khách không thao tác milestone).
		getMilestones().forEach(function (m) {
			if (!m || !m.el) return;
			function blockIfLocked(e) {
				e.preventDefault();
				e.stopPropagation();
			}
			m.el.addEventListener("click", blockIfLocked);
			if (m.label) m.label.addEventListener("click", blockIfLocked);
		});

		mainOrderBtn.addEventListener("click", function () {
			var cart = window.WebCafeCart.getCart();
			if (isAccountCustomer) {
				if (!cart.items.length) {
					window.location.href = "/menu";
					return;
				}
				var payload = {
					items: cart.items.map(function (it) {
						return { productId: it.id, quantity: it.quantity };
					}),
					paymentMethod: getSelectedPaymentMethod(),
					address: getDeliveryAddressInput(),
					useProfileAddress: isUseProfileAddressChecked()
				};
				apiJson("/api/customer/orders/0/confirm", {
					method: "POST",
					headers: { "Content-Type": "application/json" },
					body: JSON.stringify(payload)
				}).then(function () {
					window.WebCafeCart.clearCart();
					renderCart();
					loadDeliveryHistory();
					alert("Đặt hàng thành công");
				}).catch(function (err) {
					alert(err && err.message ? err.message : "Không thể đặt hàng");
				});
				return;
			}
			// Không còn hành động "HOÀN TẤT" reset giỏ / milestone (trạng thái 2–3 do nhân viên).
			if (cart.status === 2 || cart.status === 3 || cart.status === 4) {
				return;
			}
			if (!cart.items.length) {
				window.location.href = "/menu";
				return;
			}

			if (cart.status === 1) {
				var orderId = null;
				try {
					orderId = parseInt(window.localStorage.getItem(ORDER_ID_KEY), 10);
				} catch (e) {}
				if (!orderId || isNaN(orderId)) {
					playCancelAnimation(function () {
						window.localStorage.removeItem(SENT_ITEMS_KEY);
						window.localStorage.removeItem(MORE_SNAPSHOT_KEY);
						window.localStorage.removeItem(ORDER_ID_KEY);
						writeMorePending(false);
						window.WebCafeCart.setStatus(0);
						renderCart();
					});
					return;
				}

				apiJson("/api/customer/orders/" + orderId + "/cancel", {
					method: "POST"
				}).then(function () {
					playCancelAnimation(function () {
						window.localStorage.removeItem(SENT_ITEMS_KEY);
						window.localStorage.removeItem(MORE_SNAPSHOT_KEY);
						window.localStorage.removeItem(ORDER_ID_KEY);
						stopOrderStatusPolling();
						writeMorePending(false);
						window.WebCafeCart.setStatus(0);
						renderCart();
					});
				}).catch(function (err) {
					alert(err && err.message ? err.message : "Không thể hủy đơn");
				});
				return;
			}

			// milestone-2 và milestone-3 do nhân viên xác nhận => khách chỉ được đặt đơn (milestone-1).
			// Từ status 0 => set 1, không auto chạy lên 2/3.
			if (cart.status === 0) {
				var orderItems = cart.items.map(function (it) {
					return {
						productId: it.id,
						quantity: it.quantity
					};
				});

				apiJson("/api/customer/orders/0/confirm", {
					method: "POST",
					headers: { "Content-Type": "application/json" },
					body: JSON.stringify({
						items: orderItems,
						paymentMethod: null,
						address: null,
						useProfileAddress: false
					})
				}).then(function (res) {
					if (res && res.id != null) {
						window.localStorage.setItem(ORDER_ID_KEY, String(res.id));
						subscribeOrderEvents(res.id);
						startOrderStatusPolling(res.id);
					}

					// Snapshot các món ban đầu khi đặt đơn
					writeItemQtyMap(SENT_ITEMS_KEY, getCartItemQtyMap(cart));
					window.localStorage.removeItem(MORE_SNAPSHOT_KEY);
					writeMorePending(false);

					window.WebCafeCart.setStatus(1);
					renderCart();
				}).catch(function (err) {
					alert(err && err.message ? err.message : "Không thể đặt đơn");
				});
			}
		});

		addMoreBtn.addEventListener("click", function () {
			var cart = window.WebCafeCart.getCart();
			if (cart.status !== 2 && cart.status !== 3) return;
			if (!cart.items.length) {
				window.location.href = "/menu";
				return;
			}

			var morePending = readMorePending();
			if (!morePending) {
				// Lần 1: lưu baseline hiện tại, điều hướng menu để thêm món mới
				var sentMap = readItemQtyMap(SENT_ITEMS_KEY);
				if (!sentMap || Object.keys(sentMap).length === 0) {
					sentMap = getCartItemQtyMap(cart);
				}
				writeItemQtyMap(MORE_SNAPSHOT_KEY, sentMap);
				writeMorePending(true);
				window.location.href = "/menu";
				return;
			}

			// Lần 2 (đã quay lại): "gửi thêm" => cập nhật baseline đã gửi
			var orderId = null;
			try {
				orderId = parseInt(window.localStorage.getItem(ORDER_ID_KEY), 10);
			} catch (e) {}

			if (!orderId || isNaN(orderId)) {
				alert("Chưa có orderId để gửi thêm");
				return;
			}

			var baselineMap = readItemQtyMap(MORE_SNAPSHOT_KEY) || {};
			var currentMap = getCartItemQtyMap(cart);

			var deltas = [];
			Object.keys(currentMap).forEach(function (pid) {
				var baseQty = baselineMap[pid] ? baselineMap[pid] : 0;
				var delta = currentMap[pid] - baseQty;
				if (delta > 0) {
					deltas.push({
						pid: parseInt(pid, 10),
						delta: delta
					});
				}
			});

			var calls = deltas.map(function (x) {
				return apiJson("/api/customer/orders/" + orderId + "/items", {
					method: "POST",
					headers: { "Content-Type": "application/json" },
					body: JSON.stringify({ productId: x.pid, quantity: x.delta })
				});
			});

			Promise.all(calls).then(function () {
				writeItemQtyMap(SENT_ITEMS_KEY, currentMap);
				window.localStorage.removeItem(MORE_SNAPSHOT_KEY);
				writeMorePending(false);
				renderCart();
			}).catch(function (err) {
				alert(err && err.message ? err.message : "Không thể gửi thêm món");
			});
		});

		window.addEventListener("storage", function (event) {
			if (event.key && event.key !== "webcafe.cart") return;
			renderCart();
		});
		}

		var sessionPromise = window.WebCafeCustomerSession && window.WebCafeCustomerSession.ensure
			? window.WebCafeCustomerSession.ensure()
			: Promise.resolve();
		sessionPromise.then(function () {
			bootOrderPage();
		}).catch(function () {
			bootOrderPage();
		});
	});
})();
