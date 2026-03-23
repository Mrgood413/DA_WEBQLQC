package com.example.WebCafe.repository;

import com.example.WebCafe.model.CafeTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CafeTableRepository extends JpaRepository<CafeTable, Integer> {

	Optional<CafeTable> findByTableNumber(Integer tableNumber);
}
