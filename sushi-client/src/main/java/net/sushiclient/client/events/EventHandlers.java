/*
 * Contact github.com/hiyama283
 * Project "sushi-client"
 *
 * Copyright 2022 hiyama283
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.sushiclient.client.events;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

public class EventHandlers {

    private static final Set<EventMap> eventMaps = new CopyOnWriteArraySet<>();

    @SuppressWarnings("unchecked")
    public static <T extends Event> void callEvent(T event) {
        LinkedList<EventAdapter<T>> adapters = new LinkedList<>();
        for (EventMap map : getAllEventMap(event.getClass())) {
            for (EventAdapter<?> adapter : map.adapters) {
                adapters.add((EventAdapter<T>) adapter);
            }
        }
        adapters.sort(Comparator.comparingInt(EventAdapter::getPriority));
        for (EventAdapter<T> adapter : adapters) {
            if (event instanceof Cancellable && ((Cancellable) event).isCancelled() && adapter.isIgnoreCancelled())
                continue;
            adapter.call(event);
        }
    }

    public static void register(Object obj, EventAdapter<?> adapter) {
        EventMap map = getEventMap(adapter.getEventClass());
        if (map == null) {
            map = new EventMap(obj, adapter.getEventClass(), new ArrayList<>());
            eventMaps.add(map);
        }
        map.adapters.add(adapter);
        map.adapters.sort(Comparator.comparingInt(EventAdapter::getPriority));
    }

    public static void register(Object o) {
        for (Method method : o.getClass().getMethods()) {
            try {
                register(o, new MethodEventAdapter(o, method));
            } catch (IllegalArgumentException e) {
                // skip
            }
        }
    }

    public static void unregister(Object o) {
        eventMaps.removeIf(map -> o.equals(map.obj));
    }

    private static EventMap getEventMap(Class<?> eventClass) {
        for (EventMap map : eventMaps) {
            if (eventClass.equals(map.getClass()))
                return map;
        }
        return null;
    }

    private static HashSet<EventMap> getAllEventMap(Class<?> eventClass) {
        HashSet<EventMap> result = new HashSet<>();
        for (EventMap map : eventMaps) {
            if (!map.eventClass.isAssignableFrom(eventClass)) continue;
            result.add(map);
        }
        return result;
    }

    private static class EventMap {
        final Object obj;
        final Class<?> eventClass;
        final List<EventAdapter<?>> adapters;

        EventMap(Object obj, Class<?> eventClass, List<EventAdapter<?>> adapters) {
            this.obj = obj;
            this.eventClass = eventClass;
            this.adapters = new CopyOnWriteArrayList<>(adapters);
        }
    }
}
