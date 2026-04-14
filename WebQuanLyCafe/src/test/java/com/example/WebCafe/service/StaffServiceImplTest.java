package com.example.WebCafe.service;

import com.example.WebCafe.model.CafeOrder;
import com.example.WebCafe.model.OrderItem;
import com.example.WebCafe.model.Product;
import com.example.WebCafe.model.enums.OrderStatus;
import com.example.WebCafe.repository.CafeOrderRepository;
import com.example.WebCafe.repository.CategoryRepository;
import com.example.WebCafe.repository.PaymentRepository;
import com.example.WebCafe.repository.ProductRepository;
import com.example.WebCafe.repository.StaffRepository;
import com.example.WebCafe.repository.StaffShiftRepository;
import com.example.WebCafe.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StaffServiceImplTest {

	@Mock
	private ProductRepository productRepository;
	@Mock
	private CategoryRepository categoryRepository;
	@Mock
	private CafeOrderRepository cafeOrderRepository;
	@Mock
	private PaymentRepository paymentRepository;
	@Mock
	private OrderMilestoneEventService orderMilestoneEventService;
	@Mock
	private StaffQueueUpdateEventService staffQueueUpdateEventService;
	@Mock
	private UserRepository userRepository;
	@Mock
	private StaffRepository staffRepository;
	@Mock
	private StaffShiftRepository staffShiftRepository;
	@Mock
	private ProductInventoryService productInventoryService;

	@InjectMocks
	private StaffServiceImpl staffService;

	@Test
	void wbStaff01_confirmPendingOrder_movesToPreparing() {
		Product product = new Product();
		product.setId(100);
		product.setAvailable(true);
		product.setQuantity(10);

		OrderItem item = new OrderItem();
		item.setProduct(product);
		item.setQuantity(2);
		item.setPrice(BigDecimal.valueOf(25000));

		CafeOrder order = new CafeOrder();
		order.setId(501);
		order.setStatus(OrderStatus.PENDING);
		order.setItems(List.of(item));

		when(cafeOrderRepository.findWithItemsById(501)).thenReturn(Optional.of(order));
		when(productRepository.findById(100)).thenReturn(Optional.of(product));
		when(productInventoryService.fulfillableQuantity(product, 2)).thenReturn(2);

		staffService.confirmOrder(501);

		assertEquals(OrderStatus.PREPARING, order.getStatus());
		verify(cafeOrderRepository).save(order);
		verify(orderMilestoneEventService).emitMilestone(501, 2);
		verify(staffQueueUpdateEventService).emitQueueUpdated();
	}

	@Test
	void wbStaff02_confirmPreparingOrder_movesToDone() {
		CafeOrder order = new CafeOrder();
		order.setId(502);
		order.setStatus(OrderStatus.PREPARING);

		when(cafeOrderRepository.findWithItemsById(502)).thenReturn(Optional.of(order));

		staffService.confirmOrder(502);

		assertEquals(OrderStatus.DONE, order.getStatus());
		verify(cafeOrderRepository).save(order);
		verify(orderMilestoneEventService).emitMilestone(502, 3);
		verify(staffQueueUpdateEventService).emitQueueUpdated();
	}

	@Test
	void wbStaff03_confirmDoneOrder_keepsStatusAndReturns() {
		CafeOrder order = new CafeOrder();
		order.setId(503);
		order.setStatus(OrderStatus.DONE);

		when(cafeOrderRepository.findWithItemsById(503)).thenReturn(Optional.of(order));

		staffService.confirmOrder(503);

		assertEquals(OrderStatus.DONE, order.getStatus());
		verify(cafeOrderRepository, never()).save(order);
		verify(orderMilestoneEventService, never()).emitMilestone(503, 3);
		verify(orderMilestoneEventService, never()).emitMilestone(503, 2);
		verify(staffQueueUpdateEventService, never()).emitQueueUpdated();
	}
}
