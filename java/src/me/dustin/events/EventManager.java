package me.dustin.events;

import com.google.common.collect.Maps;
import me.dustin.events.core.EventListener;
import me.dustin.events.core.annotate.EventPointer;

import java.lang.reflect.Field;
import java.util.*;

//basically an Alpine clone I made years back
public class EventManager {

	/*
	 * Map of all objects in the call list
	 */
	private final Map<Object, Class<?>> classMAP = Maps.newConcurrentMap();

	/*
	 * Map of every field + the class of the event in the field.
	 */
	private final Map<Class<?>, List<EventListener>> eventMAP = Maps.newConcurrentMap();

	/*
	 * This is what runs the event to every class it is hooked in.
	 */
	public void run(Object event) {
		List<EventListener> eventListeners = eventMAP.get(event.getClass());
		if (eventListeners != null && eventListeners.size() > 0) {
			try {
				eventListeners.forEach(listener -> listener.invoke(event));
			} catch (ConcurrentModificationException e) {

			}
		}
	}

	/*
	 * Register an object to the call list Example:
	 * EventManager.register(Minecraft.getMinecraft()); or
	 * EventManager.register(this);
	 */
	public void register(Object o) {
		if (isRegistered(o))
			return;
		for (Field f : o.getClass().getDeclaredFields()) {
			if (f.isAnnotationPresent(EventPointer.class)) {
				if (!f.isAccessible()) {
					f.setAccessible(true);
				}
				try {
					EventListener eventListener = (EventListener) f.get(o);

					if (eventListener == null) {
						continue;
					}
					if (!eventMAP.containsKey(eventListener.getEventClass()))
						eventMAP.put(eventListener.getEventClass(), new ArrayList<>());

					eventMAP.get(eventListener.getEventClass()).add(eventListener);
					eventMAP.get(eventListener.getEventClass()).sort(Comparator.comparingInt(EventListener::getPriority));
				} catch (IllegalArgumentException | IllegalAccessException | NullPointerException e) {
					e.printStackTrace();
				}
			}
		}
		classMAP.put(o, o.getClass());
	}

	/*
	 * Remove the object from the call list
	 */
	public void unregister(Object o) {
		while (isRegistered(o)) {
			for (Field f : o.getClass().getDeclaredFields()) {
				if (f.isAnnotationPresent(EventPointer.class)) {
					if (!f.isAccessible()) {
						f.setAccessible(true);
					}
					try {
						EventListener eventListener = (EventListener) f.get(o);
						if (eventMAP.get(eventListener.getEventClass()) != null) {
							eventMAP.get(eventListener.getEventClass()).remove(eventListener);
						}
					} catch (IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
			classMAP.remove(o);
		}
	}

	public boolean isRegistered(Object o) {
		return classMAP.containsKey(o);
	}
}
