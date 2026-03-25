package com.example.WebCafe.repository;

import com.example.WebCafe.model.Shift;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShiftRepository extends JpaRepository<Shift, Integer> {
}
