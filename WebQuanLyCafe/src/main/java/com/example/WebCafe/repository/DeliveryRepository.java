package com.example.WebCafe.repository;

import com.example.WebCafe.model.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DeliveryRepository extends JpaRepository<Delivery, Integer> {
	@Query("""
		select distinct d
		from Delivery d
		join fetch d.customer c
		left join fetch d.payment p
		left join fetch d.order o
		left join fetch o.items oi
		left join fetch oi.product pr
		where c.userId = :userId
		order by d.id desc
	""")
	List<Delivery> findHistoryByCustomerUserId(@Param("userId") Integer userId);
}

