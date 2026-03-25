package com.example.WebCafe.repository;

import com.example.WebCafe.model.StaffShift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StaffShiftRepository extends JpaRepository<StaffShift, Integer> {

	List<StaffShift> findByStaffUserId(Integer userId);

	@Modifying
	@Query("DELETE FROM StaffShift ss WHERE ss.staff.userId = :uid")
	void deleteByStaffUserId(@Param("uid") Integer userId);
}
