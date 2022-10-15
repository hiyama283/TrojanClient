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

package net.sushiclient.client.modules.player;

import com.google.gson.annotations.SerializedName;
import net.sushiclient.client.config.data.Named;

public enum NoFallMode implements Named {
    @SerializedName("PACKET")
    PACKET("Packet"),
    @SerializedName("ON_GROUND")
    ON_GROUND("On Ground"),
    @SerializedName("FLY")
    FLY("Fly"),
    @SerializedName("FALL_DISTANCE")
    FALL_DISTANCE("Fall distance");

    private final String name;

    NoFallMode(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
