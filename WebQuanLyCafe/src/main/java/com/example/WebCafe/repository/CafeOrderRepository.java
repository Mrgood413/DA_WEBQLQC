package com.example.WebCafe.repository;

import com.example.WebCafe.model.CafeOrder;
import com.example.WebCafe.model.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CafeOrderRepository extends JpaRepository<CafeOrder, Integer> {

	List<CafeOrder> findByTable_TableNumberAndStatus(Integer tableNumber, OrderStatus status);

	List<CafeOrder> findByTable_TableNumberOrderByIdDesc(Integer tableNumber);

	java.util.Optional<CafeOrder> findByIdAndTable_TableNumber(Integer orderId, Integer tableNumber);
}
