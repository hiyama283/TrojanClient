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

import java.util.function.Consumer;

public interface Configuration<T> {
    T getValue();

    void setValue(T value);

    String getId();

    String getName();

    String getDescription();

    Class<T> getValueClass();

    boolean isValid();

    ConfigurationCategory getCategory();

    boolean isTemporary();

    int getPriority();

    T getDefaultValue();

    void addHandler(ConfigurationHandler<T> handler);

    void removeHandler(ConfigurationHandler<T> handler);

    void addHandler(Consumer<T> handler);

    void removeHandler(Consumer<T> handler);

    default void reset() {
        if (!isTemporary()) setValue(getDefaultValue());
    }
}
