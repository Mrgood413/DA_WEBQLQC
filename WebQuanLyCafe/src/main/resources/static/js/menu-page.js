(function () {
	var allProducts = [];
	var activeCategory = "all";
	var searchQuery = "";
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

	function renderGrid() {
		var grid = document.getElementById("menu-grid");
		var list = filteredProducts();
		grid.innerHTML = "";
		if (list.length === 0) {
			grid.innerHTML =
				'<p class="col-span-full text-center text-on-surface-variant py-8">Không có món phù hợp.</p>';
			return;
		}
		list.forEach(function (p) {
			var imgUrl = p.imageUrl && String(p.imageUrl).trim() ? escapeHtml(p.imageUrl) : PLACEHOLDER_IMG;
			var desc = p.description
				? '<p class="text-xs text-on-surface-variant line-clamp-2 mt-1">' +
				  escapeHtml(p.description) +
				  "</p>"
				: "";
			var card = document.createElement("div");
			card.className =
				"group bg-surface-container-lowest rounded-xl overflow-hidden transition-all duration-300 hover:shadow-xl hover:-translate-y-1";
			card.setAttribute("data-product-id", String(p.id));
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
			wrap.className = "relative h-64 overflow-hidden bg-surface-container";
			wrap.appendChild(img);
			card.appendChild(wrap);
			var body = document.createElement("div");
			body.className = "p-5 flex flex-col gap-4";
			body.innerHTML =
				'<div class="flex justify-between items-start gap-2">' +
				'<div class="min-w-0"><h3 class="text-lg font-bold text-primary">' +
				escapeHtml(p.name) +
				"</h3>" +
				desc +
				'</div><span class="text-secondary font-bold shrink-0">' +
				formatVnd(p.price) +
				"</span></div>" +
				'<div class="flex items-center justify-between gap-4 mt-auto">' +
				'<div class="flex items-center bg-surface-container rounded-full px-2 py-1">' +
				'<button type="button" class="w-8 h-8 flex items-center justify-center text-primary hover:bg-surface-container-high rounded-full" aria-label="Giảm"><span class="material-symbols-outlined text-sm">remove</span></button>' +
				'<span class="px-3 font-semibold text-sm qty">0</span>' +
				'<button type="button" class="w-8 h-8 flex items-center justify-center text-primary hover:bg-surface-container-high rounded-full" aria-label="Tăng"><span class="material-symbols-outlined text-sm">add</span></button>' +
				"</div>" +
				'<button type="button" class="flex-grow bg-primary text-white py-2 rounded-full text-sm font-semibold hover:bg-primary-container transition-colors">Thêm</button>' +
				"</div>";
			card.appendChild(body);
			grid.appendChild(card);
		});
	}

	function renderChips() {
		var wrap = document.getElementById("category-chips");
		var names = [];
		allProducts.forEach(function (p) {
			var c = p.categoryName;
			if (c && names.indexOf(c) === -1) names.push(c);
		});
		names.sort();
		wrap.innerHTML = "";
		var allBtn = document.createElement("button");
		allBtn.type = "button";
		allBtn.className =
			"px-6 py-2 rounded-full font-medium text-sm chip-btn bg-secondary-container text-on-secondary-container";
		allBtn.setAttribute("data-cat", "all");
		allBtn.textContent = "Tất cả";
		wrap.appendChild(allBtn);
		names.forEach(function (name) {
			var b = document.createElement("button");
			b.type = "button";
			b.className =
				"px-6 py-2 rounded-full bg-surface-container text-on-surface hover:bg-surface-container-high transition-colors font-medium text-sm chip-btn";
			b.setAttribute("data-cat", name);
			b.textContent = name;
			wrap.appendChild(b);
		});
		wrap.querySelectorAll(".chip-btn").forEach(function (btn) {
			btn.addEventListener("click", function () {
				activeCategory = btn.getAttribute("data-cat") || "all";
				wrap.querySelectorAll(".chip-btn").forEach(function (x) {
					x.classList.remove("bg-secondary-container", "text-on-secondary-container");
					x.classList.add("bg-surface-container", "text-on-surface");
				});
				btn.classList.remove("bg-surface-container", "text-on-surface");
				btn.classList.add("bg-secondary-container", "text-on-secondary-container");
				renderGrid();
			});
		});
	}

	function showError(msg) {
		var el = document.getElementById("menu-error");
		el.textContent = msg;
		el.classList.remove("hidden");
	}

	document.addEventListener("DOMContentLoaded", function () {
		var searchInput = document.querySelector('header input[placeholder*="Tìm kiếm"]');
		if (searchInput) {
			searchInput.addEventListener("input", function () {
				searchQuery = searchInput.value.trim();
				renderGrid();
			});
		}
		fetch("/api/menu", { credentials: "same-origin" })
			.then(function (r) {
				if (!r.ok) throw new Error("Không tải được thực đơn (" + r.status + ")");
				return r.json();
			})
			.then(function (data) {
				allProducts = data;
				document.getElementById("menu-loading").classList.add("hidden");
				renderChips();
				renderGrid();
			})
			.catch(function (e) {
				document.getElementById("menu-loading").classList.add("hidden");
				showError(e.message || "Lỗi mạng");
			});
	});
})();
