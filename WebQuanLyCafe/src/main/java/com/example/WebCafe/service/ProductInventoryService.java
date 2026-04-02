package com.example.WebCafe.service;

import com.example.WebCafe.model.Product;
import org.springframework.stereotype.Service;

/**
 * Tồn kho: {@code quantity == null} = không giới hạn (không trừ kho).
 */
@Service
public class ProductInventoryService {

	public boolean hasUnlimitedStock(Product p) {
		return p != null && p.getQuantity() == null;
	}

	/**
	 * Số lượng có thể đáp ứng cho một dòng đặt (đã trừ phần đã phân bổ trong cùng request cho cùng product).
	 */
	public int allocateForNewOrderLine(Product p, int requestedQty, int alreadyAllocatedSameProductInRequest) {
		if (requestedQty <= 0 || p == null) {
			return 0;
		}
		if (!Boolean.TRUE.equals(p.getAvailable())) {
			return 0;
		}
		if (hasUnlimitedStock(p)) {
			return requestedQty;
		}
		int stock = Math.max(0, p.getQuantity());
		int remain = Math.max(0, stock - alreadyAllocatedSameProductInRequest);
		return Math.min(requestedQty, remain);
	}

	/**
	 * Đối chiếu tồn khi xác nhận đơn / gọi thêm (đọc trực tiếp từ entity đã refresh).
	 */
	public int fulfillableQuantity(Product p, int requestedQty) {
		if (requestedQty <= 0 || p == null) {
			return 0;
		}
		if (!Boolean.TRUE.equals(p.getAvailable())) {
			return 0;
		}
		if (hasUnlimitedStock(p)) {
			return requestedQty;
		}
		return Math.min(requestedQty, Math.max(0, p.getQuantity()));
	}

	public void deductStock(Product p, int qty) {
		if (qty <= 0 || p == null || hasUnlimitedStock(p)) {
			return;
		}
		int current = p.getQuantity() != null ? p.getQuantity() : 0;
		int next = Math.max(0, current - qty);
		p.setQuantity(next);
		if (next == 0) {
			p.setAvailable(false);
		}
	}

	public boolean isVisibleOnMenu(Product p) {
		if (p == null || !Boolean.TRUE.equals(p.getAvailable())) {
			return false;
		}
		return hasUnlimitedStock(p) || p.getQuantity() > 0;
	}
}
