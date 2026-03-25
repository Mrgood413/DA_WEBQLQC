package com.example.WebCafe.repository;

import com.example.WebCafe.model.CafeOrder;
import com.example.WebCafe.model.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface CafeOrderRepository extends JpaRepository<CafeOrder, Integer> {

	List<CafeOrder> findByTable_TableNumberAndStatus(Integer tableNumber, OrderStatus status);

	List<CafeOrder> findByTable_TableNumberOrderByIdDesc(Integer tableNumber);

	java.util.Optional<CafeOrder> findByIdAndTable_TableNumber(Integer orderId, Integer tableNumber);

	@Query("""
		select distinct o
		from CafeOrder o
		left join fetch o.table t
		left join fetch o.items i
		left join fetch i.product p
		where o.status in :statuses
		order by o.id desc
	""")
	List<CafeOrder> findQueueOrdersWithDetails(@Param("statuses") Collection<OrderStatus> statuses);
}
