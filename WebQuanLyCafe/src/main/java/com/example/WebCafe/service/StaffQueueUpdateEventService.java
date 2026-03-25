package com.example.WebCafe.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class StaffQueueUpdateEventService {

	private final Set<SseEmitter> emitters = ConcurrentHashMap.newKeySet();

	public SseEmitter register() {
		SseEmitter emitter = new SseEmitter(0L); // không timeout
		emitters.add(emitter);

		emitter.onCompletion(() -> emitters.remove(emitter));
		emitter.onTimeout(() -> emitters.remove(emitter));
		emitter.onError((ex) -> emitters.remove(emitter));
		return emitter;
	}

	public void emitQueueUpdated() {
		for (SseEmitter emitter : emitters) {
			try {
				emitter.send(SseEmitter.event().name("queue-updated").data("updated"));
			} catch (IOException e) {
				emitters.remove(emitter);
				try {
					emitter.complete();
				} catch (Exception ignore) {}
			}
		}
	}
}

