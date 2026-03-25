package com.example.WebCafe.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Bắn event milestone cho từng orderId.
 * Hiện tại chạy theo kiểu in-memory (phù hợp bài tập/demo).
 */
@Service
public class OrderMilestoneEventService {

	private final ConcurrentMap<Integer, SseEmitter> emitters = new ConcurrentHashMap<>();

	public SseEmitter register(Integer orderId) {
		SseEmitter emitter = new SseEmitter(0L); // không timeout
		SseEmitter old = emitters.put(orderId, emitter);
		if (old != null) {
			try {
				old.complete();
			} catch (Exception ignore) {}
		}

		emitter.onCompletion(() -> emitters.remove(orderId, emitter));
		emitter.onTimeout(() -> emitters.remove(orderId, emitter));
		emitter.onError((ex) -> emitters.remove(orderId, emitter));
		return emitter;
	}

	public void emitMilestone(Integer orderId, int milestone) {
		SseEmitter emitter = emitters.get(orderId);
		if (emitter == null) return;
		try {
			emitter.send(SseEmitter.event().name("milestone").data(milestone));
		} catch (IOException e) {
			emitters.remove(orderId, emitter);
			try {
				emitter.complete();
			} catch (Exception ignore) {}
		}
	}
}

