package com.example.WebCafe.repository;

import com.example.WebCafe.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {

	Optional<Payment> findTopByOrder_IdOrderByIdDesc(Integer orderId);

	@Query("""
		select coalesce(sum(p.totalAmount), 0)
		from Payment p
		where p.paidAt >= :from and p.paidAt < :to
	""")
	BigDecimal sumRevenuePaidAtBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

	@Query("""
		select distinct p
		from Payment p
		join fetch p.order o
		left join fetch o.items oi
		left join fetch oi.product pr
		where p.paidAt >= :from and p.paidAt < :to
		order by p.paidAt asc
	""")
	List<Payment> findPaidDetailsBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

	@Query("""
		select distinct p
		from Payment p
		join fetch p.order o
		left join fetch o.items oi
		left join fetch oi.product pr
		order by p.paidAt asc
	""")
	List<Payment> findAllPaidDetails();
}
