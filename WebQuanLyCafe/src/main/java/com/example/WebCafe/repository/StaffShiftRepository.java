package com.example.WebCafe.repository;

import com.example.WebCafe.model.StaffShift;
import com.example.WebCafe.model.enums.Weekday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StaffShiftRepository extends JpaRepository<StaffShift, Integer> {

	List<StaffShift> findByStaffUserId(Integer userId);

	@Query("""
		select ss
		from StaffShift ss
		join ss.shift sh
		where sh.dayOfWeek = :day
	""")
	List<StaffShift> findByShiftDayOfWeek(@Param("day") Weekday day);

	@Modifying
	@Query("DELETE FROM StaffShift ss WHERE ss.staff.userId = :uid")
	void deleteByStaffUserId(@Param("uid") Integer userId);
}
