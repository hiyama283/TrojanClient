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

package net.sushiclient.client.task.tasks;

import com.google.gson.annotations.SerializedName;
import net.sushiclient.client.config.data.Named;

public enum ItemSwitchMode implements Named {
    @SerializedName("ALL")
    INVENTORY("All"),
    @SerializedName("HOTBAR")
    HOTBAR("Hotbar"),
    @SerializedName("NONE")
    NONE("None");

    private final String name;

    ItemSwitchMode(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
