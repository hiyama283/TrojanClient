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

package net.sushiclient.client.gui;

import com.google.gson.annotations.SerializedName;
import net.sushiclient.client.config.data.Named;

public enum Origin implements Named {
    @SerializedName("TOP_LEFT")
    TOP_LEFT("Top Left", false, false, Anchor.TOP_LEFT),
    @SerializedName("BOTTOM_LEFT")
    BOTTOM_LEFT("Bottom Right", false, true, Anchor.BOTTOM_LEFT),
    @SerializedName("TOP_RIGHT")
    TOP_RIGHT("Top Right", true, false, Anchor.TOP_RIGHT),
    @SerializedName("BOTTOM_RIGHT")
    BOTTOM_RIGHT("Bottom Right", true, true, Anchor.BOTTOM_RIGHT);

    private final String name;
    private final boolean fromRight;
    private final boolean fromBottom;
    private final Anchor anchor;

    Origin(String name, boolean fromRight, boolean fromBottom, Anchor anchor) {
        this.name = name;
        this.fromRight = fromRight;
        this.fromBottom = fromBottom;
        this.anchor = anchor;
    }

    public boolean isFromRight() {
        return fromRight;
    }

    public boolean isFromBottom() {
        return fromBottom;
    }

    @Override
    public String getName() {
        return name;
    }

    public Anchor toAnchor() {
        return anchor;
    }

    public Origin getOpposite() {
        for (Origin origin : values()) {
            if (isFromRight() != origin.isFromRight() && isFromBottom() != origin.isFromBottom())
                return origin;
        }
        return null;
    }
}
