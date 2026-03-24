(function () {
	"use strict";

	var STATUS_WIDTHS = ["0%", "12%", "50%", "100%"];
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
			minute: "2-digit"
		});
	}

	function getOrderCode(cart) {
		var seed = cart.placedAt || cart.updatedAt;
		if (!seed) return "Giỏ hàng tạm";
		var compact = seed.replace(/\D/g, "").slice(-6);
		return "#AL-" + compact;
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
			}
		];
	}

	function applyProgress(state) {
		var progressLine = document.getElementById("progressLine");
		var milestones = getMilestones();

		progressLine.style.width = STATUS_WIDTHS[state] || "0%";
		milestones.forEach(function (milestone, index) {
			var active = index < state;
			milestone.el.classList.toggle("milestone-active", active);
			milestone.label.classList.toggle("opacity-40", !active);
			milestone.label.classList.toggle("text-secondary", active);
			milestone.label.classList.toggle("opacity-100", active);
		});
	}

	function playCancelAnimation(onComplete) {
		var progressLine = document.getElementById("progressLine");
		var milestones = getMilestones();

		progressLine.style.width = "0%";

		milestones.forEach(function (milestone, index) {
			window.setTimeout(function () {
				milestone.el.classList.remove("milestone-active", "animate-bounce-once");
				milestone.label.classList.add("opacity-40");
				milestone.label.classList.remove("text-secondary", "opacity-100");
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
		var hasItems = cart.items.length > 0;
		var editable = cart.status === 0;

		button.disabled = !hasItems;
		button.classList.toggle("opacity-50", !hasItems);
		button.classList.toggle("cursor-not-allowed", !hasItems);
		button.classList.remove("btn-ordered", "bg-secondary");
		if (addMoreBtn) {
			addMoreBtn.disabled = !editable;
			addMoreBtn.classList.toggle("opacity-50", !editable);
			addMoreBtn.classList.toggle("cursor-not-allowed", !editable);
		}

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

		if (cart.status === 2) {
			text.textContent = "ĐANG THỰC HIỆN...";
			return;
		}

		button.classList.add("bg-secondary");
		text.textContent = "HOÀN TẤT";
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

		orderCodeEl.textContent = "Đơn " + getOrderCode(cart);
		orderTimeEl.textContent = formatTime(cart.placedAt || cart.updatedAt);
		countBadgeEl.textContent = totals.itemCount;
		totalEl.textContent = formatVnd(totals.total);
		if (headerTotalEl) headerTotalEl.textContent = formatVnd(totals.total);

		if (!cart.items.length) {
			itemsEl.innerHTML =
				'<div data-empty-state="true" class="py-6 text-center text-on-surface-variant">' +
				'<p class="text-sm font-medium">Giỏ hàng của bạn đang trống.</p>' +
				'<p class="text-xs mt-2">Quay lại trang thực đơn để thêm món.</p>' +
				"</div>";
			applyProgress(0);
			updateButtonState(cart);
			return;
		}

		Array.prototype.slice.call(itemsEl.querySelectorAll('[data-empty-state="true"]')).forEach(function (emptyState) {
			emptyState.remove();
		});

		var seen = {};

		cart.items.forEach(function (item, index) {
			var productId = String(item.id);
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

		applyProgress(cart.status || 0);
		updateButtonState(cart);
	}

	document.addEventListener("DOMContentLoaded", function () {
		var mainOrderBtn = document.getElementById("mainOrderBtn");
		var addMoreBtn = document.getElementById("add-more-btn");

		mainOrderBtn.addEventListener("click", function () {
			var cart = window.WebCafeCart.getCart();
			if (!cart.items.length) {
				window.location.href = "/menu";
				return;
			}

			if (cart.status === 1) {
				playCancelAnimation(function () {
					window.WebCafeCart.setStatus(0);
					renderCart();
				});
				return;
			}

			if (cart.status === 3) {
				window.WebCafeCart.clearCart();
				renderCart();
				return;
			}

			window.WebCafeCart.setStatus((cart.status || 0) + 1);
			renderCart();
		});

		addMoreBtn.addEventListener("click", function () {
			window.location.href = "/menu";
		});

		window.addEventListener("storage", function (event) {
			if (event.key && event.key !== "webcafe.cart") return;
			renderCart();
		});

		renderCart();
	});
})();
