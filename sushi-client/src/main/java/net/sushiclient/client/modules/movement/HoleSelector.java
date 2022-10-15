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

package net.sushiclient.client.modules.movement;

import com.google.gson.annotations.SerializedName;
import net.sushiclient.client.config.data.Named;
import net.sushiclient.client.utils.render.hole.HoleType;

public enum HoleSelector implements Named {
    @SerializedName("BOTH")
    BOTH("Both", HoleType.SAFE, HoleType.UNSAFE),
    @SerializedName("BEDROCK")
    BEDROCK("Bedrock", HoleType.SAFE);

    private final String name;
    private final HoleType[] allowed;

    HoleSelector(String name, HoleType... allowed) {
        this.name = name;
        this.allowed = allowed;
    }

    @Override
    public String getName() {
        return name;
    }

    public HoleType[] getAllowedTypes() {
        return allowed;
    }
}
