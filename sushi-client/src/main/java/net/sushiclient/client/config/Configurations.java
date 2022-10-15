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

package net.sushiclient.client.config;

import java.util.List;
import java.util.function.Supplier;

public interface Configurations {
    <T> Configuration<T> get(String id, String name, String description, Class<T> t, T defaultValue, Supplier<Boolean> valid, boolean temporary, int priority);

    default <T> Configuration<T> get(String id, String name, String description, Class<T> t, T value) {
        return get(id, name, description, t, value, () -> true, false, 0);
    }

    default <T> Configuration<T> temp(String id, String name, String description, Class<T> t, T defaultValue) {
        return get(id, name, description, t, defaultValue, () -> true, true, 0);
    }

    List<Configuration<?>> getAll();

    default void reset() {
        for (Configuration<?> conf : getAll()) {
            conf.reset();
        }
        getHandlers().forEach(ConfigurationsHandler::reset);
    }

    void addHandler(ConfigurationsHandler handler);

    boolean removeHandler(ConfigurationsHandler handler);

    List<ConfigurationsHandler> getHandlers();
}
