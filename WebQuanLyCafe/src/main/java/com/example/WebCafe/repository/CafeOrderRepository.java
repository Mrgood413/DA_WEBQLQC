package com.example.WebCafe.repository;

import com.example.WebCafe.model.CafeOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CafeOrderRepository extends JpaRepository<CafeOrder, Integer> {
}
