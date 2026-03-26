(function (window) {
	"use strict";

	var CART_KEY = "webcafe.cart";
	var MENU_KEY = "webcafe.menu";

	function safeParse(value, fallback) {
		if (!value) return fallback;
		try {
			return JSON.parse(value);
		} catch (error) {
			return fallback;
		}
	}

	function toNumber(value) {
		var num = typeof value === "number" ? value : parseFloat(value);
		return isNaN(num) ? 0 : num;
	}

	function nowIso() {
		return new Date().toISOString();
	}

	function readMenuProducts() {
		return safeParse(window.localStorage.getItem(MENU_KEY), []);
	}

	function writeMenuProducts(products) {
		window.localStorage.setItem(MENU_KEY, JSON.stringify(products));
	}

	function findStoredProduct(productId) {
		var id = String(productId);
		var products = readMenuProducts();
		for (var i = 0; i < products.length; i += 1) {
			if (String(products[i].id) === id) {
				return products[i];
			}
		}
		return null;
	}

	function normalizeProduct(product) {
		if (!product) return null;
		return {
			id: product.id,
			name: product.name || "",
			description: product.description || "",
			price: toNumber(product.price),
			imageUrl: product.imageUrl || "",
			categoryName: product.categoryName || ""
		};
	}

	function normalizeCart(raw) {
		var cart = raw && typeof raw === "object" ? raw : {};
		var items = Array.isArray(cart.items) ? cart.items : [];

		return {
			items: items
				.map(function (item) {
					var product = normalizeProduct(item);
					var quantity = Math.max(0, parseInt(item.quantity, 10) || 0);
					if (!product || quantity <= 0) return null;
					return {
						id: product.id,
						name: product.name,
						description: product.description,
						price: product.price,
						imageUrl: product.imageUrl,
						categoryName: product.categoryName,
						quantity: quantity
					};
				})
				.filter(Boolean),
			status: Math.max(0, Math.min(4, parseInt(cart.status, 10) || 0)),
			placedAt: cart.placedAt || null,
			updatedAt: cart.updatedAt || null
		};
	}

	function readCart() {
		return normalizeCart(safeParse(window.localStorage.getItem(CART_KEY), null));
	}

	function writeCart(cart) {
		var normalized = normalizeCart(cart);
		normalized.updatedAt = nowIso();
		window.localStorage.setItem(CART_KEY, JSON.stringify(normalized));
		return normalized;
	}

	function saveMenuProducts(products) {
		var normalized = Array.isArray(products)
			? products.map(normalizeProduct).filter(Boolean)
			: [];
		writeMenuProducts(normalized);

		var cart = readCart();
		if (!cart.items.length) return normalized;

		cart.items = cart.items.map(function (item) {
			var latest = findStoredProduct(item.id);
			if (!latest) return item;
			return {
				id: latest.id,
				name: latest.name,
				description: latest.description,
				price: latest.price,
				imageUrl: latest.imageUrl,
				categoryName: latest.categoryName,
				quantity: item.quantity
			};
		});
		writeCart(cart);
		return normalized;
	}

	function upsertCartItem(product, quantity) {
		var normalizedProduct = normalizeProduct(product) || findStoredProduct(product && product.id);
		if (!normalizedProduct || normalizedProduct.id == null) {
			return readCart();
		}

		var nextQuantity = Math.max(0, parseInt(quantity, 10) || 0);
		var cart = readCart();
		var nextItems = [];
		var found = false;

		for (var i = 0; i < cart.items.length; i += 1) {
			var item = cart.items[i];
			if (String(item.id) === String(normalizedProduct.id)) {
				found = true;
				if (nextQuantity > 0) {
					nextItems.push({
						id: normalizedProduct.id,
						name: normalizedProduct.name,
						description: normalizedProduct.description,
						price: normalizedProduct.price,
						imageUrl: normalizedProduct.imageUrl,
						categoryName: normalizedProduct.categoryName,
						quantity: nextQuantity
					});
				}
			} else {
				nextItems.push(item);
			}
		}

		if (!found && nextQuantity > 0) {
			nextItems.push({
				id: normalizedProduct.id,
				name: normalizedProduct.name,
				description: normalizedProduct.description,
				price: normalizedProduct.price,
				imageUrl: normalizedProduct.imageUrl,
				categoryName: normalizedProduct.categoryName,
				quantity: nextQuantity
			});
		}

		var nextStatus = nextItems.length > 0 ? cart.status : 0;
		var nextPlacedAt = nextStatus > 0 ? cart.placedAt : null;

		return writeCart({
			items: nextItems,
			status: nextStatus,
			placedAt: nextPlacedAt
		});
	}

	function getTotals(cart) {
		var source = cart ? normalizeCart(cart) : readCart();
		var itemCount = 0;
		var total = 0;
		source.items.forEach(function (item) {
			itemCount += item.quantity;
			total += item.quantity * toNumber(item.price);
		});
		return {
			itemCount: itemCount,
			total: total
		};
	}

	function getQuantity(productId) {
		var id = String(productId);
		var cart = readCart();
		for (var i = 0; i < cart.items.length; i += 1) {
			if (String(cart.items[i].id) === id) {
				return cart.items[i].quantity;
			}
		}
		return 0;
	}

	function canEditCart() {
		var status = readCart().status;
		// Giỏ chỉnh được ở menu khi chưa gửi, hoặc khi đang pha chế / hoàn thành chưa thanh toán (gọi thêm).
		return status === 0 || status === 2 || status === 3;
	}

	function setStatus(status) {
		var cart = readCart();
		cart.status = Math.max(0, Math.min(4, parseInt(status, 10) || 0));
		if (cart.status > 0 && !cart.placedAt) {
			cart.placedAt = nowIso();
		}
		if (cart.status === 0) {
			cart.placedAt = null;
		}
		return writeCart(cart);
	}

	function clearCart() {
		return writeCart({
			items: [],
			status: 0,
			placedAt: null
		});
	}

	window.WebCafeCart = {
		getCart: readCart,
		saveMenuProducts: saveMenuProducts,
		upsertCartItem: upsertCartItem,
		getTotals: getTotals,
		getQuantity: getQuantity,
		canEditCart: canEditCart,
		setStatus: setStatus,
		clearCart: clearCart,
		getMenuProducts: readMenuProducts
	};
})(window);
