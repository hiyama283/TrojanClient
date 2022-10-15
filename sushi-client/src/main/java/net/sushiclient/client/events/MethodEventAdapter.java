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


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class MethodEventAdapter implements EventAdapter<Event> {

    private final Object obj;
    private final Method method;
    private final Class<Event> eventClass;
    private final List<EventTiming> timings;
    private final int priority;
    private final boolean ignoreCancelled;

    @SuppressWarnings("unchecked")
    public MethodEventAdapter(Object obj, Method method) {
        this.obj = obj;
        this.method = method;
        Class<?>[] parameters = method.getParameterTypes();
        if (parameters.length != 1)
            throw new IllegalArgumentException("Only 1 parameter type can exist");
        try {
            eventClass = (Class<Event>) parameters[0];
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Parameter type must implement Event");
        }
        EventHandler handler = method.getAnnotation(EventHandler.class);
        if (handler == null)
            throw new IllegalArgumentException("@EventHandler is missing");
        this.timings = Arrays.asList(handler.timing());
        this.priority = handler.priority();
        this.ignoreCancelled = handler.ignoreCancelled();
    }

    @Override
    public void call(Event event) {
        try {
            if (timings.contains(event.getTiming()))
                method.invoke(obj, event);
        } catch (InvocationTargetException e) {
            new RuntimeException("An exception occurred during the execution of " + method, e.getTargetException()).printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public boolean isIgnoreCancelled() {
        return ignoreCancelled;
    }

    @Override
    public Class<Event> getEventClass() {
        return eventClass;
    }


}
