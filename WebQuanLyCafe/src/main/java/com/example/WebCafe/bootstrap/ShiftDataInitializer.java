package com.example.WebCafe.bootstrap;

import com.example.WebCafe.model.Shift;
import com.example.WebCafe.model.enums.ShiftTime;
import com.example.WebCafe.model.enums.Weekday;
import com.example.WebCafe.repository.ShiftRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Tự động seed 21 ca (7 ngày × 3 khung giờ) khi bảng {@code shifts} đang trống.
 */
@Component
public class ShiftDataInitializer implements ApplicationRunner {

	private static final Logger log = LoggerFactory.getLogger(ShiftDataInitializer.class);

	private final ShiftRepository shiftRepository;

	public ShiftDataInitializer(ShiftRepository shiftRepository) {
		this.shiftRepository = shiftRepository;
	}

	@Override
	@Transactional
	public void run(ApplicationArguments args) {
		if (shiftRepository.count() > 0) {
			return;
		}
		for (Weekday day : Weekday.values()) {
			for (ShiftTime time : ShiftTime.values()) {
				Shift s = new Shift();
				s.setDayOfWeek(day);
				s.setShiftTime(time);
				shiftRepository.save(s);
			}
		}
		log.info("Đã khởi tạo {} ca làm mặc định (shifts).", shiftRepository.count());
	}
}
