(function () {
	var allProducts = [];
	var activeCategory = "all";
	var searchQuery = "";
	var selectedQuantities = {};
	var PLACEHOLDER_IMG =
		"data:image/svg+xml," +
		encodeURIComponent(
			'<svg xmlns="http://www.w3.org/2000/svg" width="400" height="400" viewBox="0 0 400 400"><rect fill="#efe8d2" width="400" height="400"/><text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" fill="#504442" font-family="sans-serif" font-size="18">Ảnh món</text></svg>'
		);

	function formatVnd(n) {
		if (n == null) return "";
		var num = typeof n === "number" ? n : parseFloat(n);
		if (isNaN(num)) return "";
		return new Intl.NumberFormat("vi-VN").format(num) + "đ";
	}

	function escapeHtml(s) {
		if (s == null) return "";
		var d = document.createElement("div");
		d.textContent = s;
		return d.innerHTML;
	}

	function getProductId(product) {
		return String(product.id != null ? product.id : product.name || "");
	}

	function findProductById(productId) {
		for (var i = 0; i < allProducts.length; i += 1) {
			if (getProductId(allProducts[i]) === String(productId)) {
				return allProducts[i];
			}
		}
		return null;
	}

	function getSelectedQuantity(productId) {
		var key = String(productId);
		if (!selectedQuantities[key]) {
			selectedQuantities[key] = 1;
		}
		return selectedQuantities[key];
	}

	function setSelectedQuantity(productId, quantity) {
		selectedQuantities[String(productId)] = Math.max(1, parseInt(quantity, 10) || 1);
	}

	function getCartQuantity(productId) {
		if (!window.WebCafeCart) return 0;
		return window.WebCafeCart.getQuantity(productId);
	}

	function canEditCart() {
		return !window.WebCafeCart || window.WebCafeCart.canEditCart();
	}

	function getCategoryNames() {
		var names = [];
		allProducts.forEach(function (p) {
			var c = (p.categoryName || "").trim();
			if (c && names.indexOf(c) === -1) names.push(c);
		});
		names.sort(function (a, b) {
			return a.localeCompare(b, "vi");
		});
		return names;
	}

	function getCategoryImageByName(name) {
		var target = String(name || "").trim();
		if (!target) return "";
		for (var i = 0; i < allProducts.length; i += 1) {
			var p = allProducts[i];
			if ((p.categoryName || "").trim() === target && p.categoryImageUrl) {
				return String(p.categoryImageUrl).trim();
			}
		}
		return "";
	}

	function filteredProducts() {
		return allProducts.filter(function (p) {
			if (activeCategory !== "all" && (p.categoryName || "") !== activeCategory) return false;
			if (searchQuery) {
				var q = searchQuery.toLowerCase();
				var name = (p.name || "").toLowerCase();
				var desc = (p.description || "").toLowerCase();
				if (name.indexOf(q) === -1 && desc.indexOf(q) === -1) return false;
			}
			return true;
		});
	}

	function updateCartSummary() {
		var countEl = document.getElementById("cart-summary-count");
		var totalEl = document.getElementById("cart-summary-total");
		var totals = window.WebCafeCart
			? window.WebCafeCart.getTotals()
			: { itemCount: 0, total: 0 };

		if (countEl) countEl.textContent = totals.itemCount + " món";
		if (totalEl) totalEl.textContent = formatVnd(totals.total || 0);
	}

	function updateMenuCard(productId) {
		var card = document.querySelector('[data-product-id="' + String(productId) + '"]');
		if (!card) return;

		var qtyEl = card.querySelector(".qty");
		var cartNoteEl = card.querySelector(".cart-note");
		var lockNoteEl = card.querySelector(".lock-note");
		var controls = card.querySelectorAll(".qty-decrease, .qty-increase, .add-to-cart");
		var selectedQty = getSelectedQuantity(productId);
		var cartQty = getCartQuantity(productId);
		var editable = canEditCart();

		if (qtyEl) qtyEl.textContent = selectedQty;
		if (cartNoteEl) {
			cartNoteEl.textContent = "Đã có " + cartQty + " món trong giỏ";
			cartNoteEl.classList.toggle("hidden", cartQty === 0);
		}
		if (lockNoteEl) {
			lockNoteEl.classList.toggle("hidden", editable);
		}
		Array.prototype.forEach.call(controls, function (control) {
			control.disabled = !editable;
			control.classList.toggle("opacity-50", !editable);
			control.classList.toggle("cursor-not-allowed", !editable);
		});
	}

	function changeQuantity(productId, delta) {
		if (!canEditCart()) return;
		var nextQty = getSelectedQuantity(productId) + delta;
		setSelectedQuantity(productId, nextQty);
		updateMenuCard(productId);
	}

	function animateProductImage(productId) {
		var card = document.querySelector('[data-product-id="' + String(productId) + '"]');
		var image = card ? card.querySelector("img") : null;
		if (!image || typeof image.animate !== "function") {
			return false;
		}

		image.animate(
			[
				{ opacity: 1 },
				{ opacity: 0.35 },
				{ opacity: 1 }
			],
			{
				duration: 320,
				easing: "ease-in-out"
			}
		);
		return true;
	}

	function addToCart(productId) {
		var product = findProductById(productId);
		var selectedQty = getSelectedQuantity(productId);
		var currentCartQty = getCartQuantity(productId);
		if (!window.WebCafeCart || !product || !canEditCart()) return;

		window.WebCafeCart.upsertCartItem(product, currentCartQty + selectedQty);
		setSelectedQuantity(productId, 1);
		if (animateProductImage(productId)) {
			window.setTimeout(function () {
				updateMenuCard(productId);
				updateCartSummary();
			}, 320);
			return;
		}
		updateMenuCard(productId);
		updateCartSummary();
	}

	function renderMenuList() {
		var listEl = document.getElementById("menu-list");
		var list = filteredProducts();
		var delayClasses = ["delay-1", "delay-2", "delay-3", "delay-4", "delay-5", "delay-6"];
		listEl.innerHTML = "";
		if (list.length === 0) {
			listEl.innerHTML =
				'<p class="text-center text-on-surface-variant py-8">Không có món phù hợp.</p>';
			return;
		}

		list.forEach(function (p) {
			var productId = getProductId(p);
			var qty = getSelectedQuantity(productId);
			var cartQty = getCartQuantity(productId);
			var imgUrl = p.imageUrl && String(p.imageUrl).trim() ? escapeHtml(p.imageUrl) : PLACEHOLDER_IMG;
			var desc = p.description
				? '<p class="text-sm text-on-surface-variant mt-1">' +
				  escapeHtml(p.description) +
				  "</p>"
				: "";
			var cartNoteClass = cartQty > 0 ? "" : " hidden";
			var disabledClass = canEditCart() ? "" : " opacity-50 cursor-not-allowed";
			var card = document.createElement("div");
			card.className =
				"group bg-surface-container-lowest rounded-xl overflow-hidden transition-all duration-300 hover:shadow-xl flex flex-col sm:flex-row h-auto sm:h-48 border border-transparent hover:border-outline-variant animate-card " +
				delayClasses[Math.min(list.indexOf(p), delayClasses.length - 1)];
			card.setAttribute("data-product-id", productId);
			var img = document.createElement("img");
			img.className =
				"w-full h-full object-cover transition-transform duration-500 group-hover:scale-110";
			img.alt = p.name || "";
			img.loading = "lazy";
			img.src = p.imageUrl && String(p.imageUrl).trim() ? p.imageUrl : PLACEHOLDER_IMG;
			img.onerror = function () {
				this.onerror = null;
				this.src = PLACEHOLDER_IMG;
			};
			var wrap = document.createElement("div");
			wrap.className = "relative w-full sm:w-64 h-48 sm:h-full overflow-hidden flex-shrink-0 bg-surface-container";
			wrap.appendChild(img);
			card.appendChild(wrap);
			var body = document.createElement("div");
			body.className = "p-6 flex flex-col flex-grow justify-between";
			body.innerHTML =
				'<div class="flex justify-between items-start gap-4">' +
				'<div class="min-w-0"><h3 class="text-xl font-bold text-primary">' +
				escapeHtml(p.name) +
				"</h3>" +
				desc +
				'<p class="cart-note text-xs text-secondary font-semibold mt-2' +
				cartNoteClass +
				'">Đã có ' +
				cartQty +
				" món trong giỏ</p>" +
				'<p class="lock-note text-xs text-error font-semibold mt-2' +
				(canEditCart() ? " hidden" : "") +
				'">Đơn đã xác nhận, không thể thêm món.</p>' +
				'</div><span class="text-secondary font-bold text-lg shrink-0">' +
				formatVnd(p.price) +
				"</span></div>" +
				'<div class="flex items-center justify-end gap-4 mt-4">' +
				'<div class="flex items-center bg-surface-container rounded-full px-2 py-1">' +
				'<button type="button" class="qty-decrease w-8 h-8 flex items-center justify-center text-primary hover:bg-surface-container-high rounded-full' +
				disabledClass +
				'" aria-label="Giảm"' +
				(canEditCart() ? "" : " disabled") +
				'><span class="material-symbols-outlined text-sm">remove</span></button>' +
				'<span class="px-3 font-semibold text-sm qty">' +
				qty +
				"</span>" +
				'<button type="button" class="qty-increase w-8 h-8 flex items-center justify-center text-primary hover:bg-surface-container-high rounded-full' +
				disabledClass +
				'" aria-label="Tăng"' +
				(canEditCart() ? "" : " disabled") +
				'><span class="material-symbols-outlined text-sm">add</span></button>' +
				"</div>" +
				'<button type="button" class="add-to-cart px-8 bg-primary text-white py-2 rounded-full text-sm font-semibold hover:bg-primary-container transition-colors active:scale-95' +
				disabledClass +
				'"' +
				(canEditCart() ? "" : " disabled") +
				">Thêm</button>" +
				"</div>";

			body.querySelector(".qty-decrease").addEventListener("click", function () {
				changeQuantity(productId, -1);
			});
			body.querySelector(".qty-increase").addEventListener("click", function () {
				changeQuantity(productId, 1);
			});
			body.querySelector(".add-to-cart").addEventListener("click", function () {
				addToCart(productId);
			});

			card.appendChild(body);
			listEl.appendChild(card);
		});
	}

	function renderCategories() {
		var wrap = document.getElementById("menu-category-list");
		var names = getCategoryNames();

		wrap.innerHTML = "";

		[{ name: "Tất cả", value: "all" }].concat(
			names.map(function (name) {
				return { name: name, value: name };
			})
		).forEach(function (item) {
			var isActive = activeCategory === item.value;
			var button = document.createElement("button");
			button.type = "button";
			button.className =
				"menu-category-btn flex items-center gap-3 px-4 py-3 rounded-xl transition-all text-sm text-left whitespace-nowrap md:whitespace-normal " +
				(isActive
					? "bg-secondary-container text-on-secondary-container font-semibold"
					: "text-on-surface hover:bg-surface-container font-medium");
			button.setAttribute("data-cat", item.value);
			if (item.value === "all") {
				button.innerHTML =
					'<span class="material-symbols-outlined text-lg">grid_view</span><span class="truncate">' +
					escapeHtml(item.name) +
					"</span>";
			} else {
				var iconUrl = getCategoryImageByName(item.name);
				button.innerHTML =
					(iconUrl
						? '<img src="' + escapeHtml(iconUrl) + '" alt="" class="w-6 h-6 rounded-full object-cover bg-surface-container shrink-0"/>'
						: '<span class="material-symbols-outlined text-lg">category</span>') +
					'<span class="truncate">' + escapeHtml(item.name) + "</span>";
			}
			button.addEventListener("click", function () {
				activeCategory = item.value;
				renderCategories();
				renderMenuList();
			});
			wrap.appendChild(button);
		});
	}

	function showError(msg) {
		var el = document.getElementById("menu-error");
		el.textContent = msg;
		el.classList.remove("hidden");
	}

	function hideError() {
		var el = document.getElementById("menu-error");
		el.classList.add("hidden");
		el.textContent = "";
	}

	document.addEventListener("DOMContentLoaded", function () {
		function bootMenuPage() {
		var searchInput = document.getElementById("menu-search-input");
		var cartButton = document.getElementById("cart-summary-button");

		if (searchInput) {
			searchInput.addEventListener("input", function () {
				searchQuery = searchInput.value.trim();
				renderMenuList();
			});
		}

		if (cartButton) {
			cartButton.addEventListener("click", function () {
				window.location.href = "/order";
			});
		}

		window.addEventListener("storage", function (event) {
			if (event.key && event.key !== "webcafe.cart") return;
			renderMenuList();
			updateCartSummary();
		});

		updateCartSummary();

		fetch("/api/menu", { credentials: "same-origin" })
			.then(function (r) {
				if (!r.ok) throw new Error("Không tải được thực đơn (" + r.status + ")");
				return r.json();
			})
			.then(function (data) {
				allProducts = Array.isArray(data) ? data : [];
				if (window.WebCafeCart) {
					window.WebCafeCart.saveMenuProducts(allProducts);
				}
				document.getElementById("menu-loading").classList.add("hidden");
				hideError();
				renderCategories();
				renderMenuList();
				updateCartSummary();
			})
			.catch(function (e) {
				document.getElementById("menu-loading").classList.add("hidden");
				showError(e.message || "Lỗi mạng");
			});
		}

		var sessionPromise = window.WebCafeCustomerSession && window.WebCafeCustomerSession.ensure
			? window.WebCafeCustomerSession.ensure()
			: Promise.resolve();
		sessionPromise.then(function () {
			bootMenuPage();
		}).catch(function () {
			bootMenuPage();
		});
	});
})();
