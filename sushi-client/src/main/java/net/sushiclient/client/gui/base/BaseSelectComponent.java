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

package net.sushiclient.client.gui.base;

import net.sushiclient.client.gui.Anchor;
import net.sushiclient.client.gui.Component;
import net.sushiclient.client.gui.Origin;
import net.sushiclient.client.gui.SelectComponent;

import java.util.ArrayList;

abstract public class BaseSelectComponent<T> extends BaseListComponent<T> implements SelectComponent<T> {

    private int maxSelect;

    public BaseSelectComponent(ArrayList<T> internal, int maxSelect) {
        super(internal);
        this.maxSelect = maxSelect;
    }

    public BaseSelectComponent(int x, int y, int width, int height, Anchor anchor, Origin origin, Component parent, ArrayList<T> internal, int maxSelect) {
        super(x, y, width, height, anchor, origin, parent, internal);
        this.maxSelect = maxSelect;
    }

    @Override
    public int getMaxSelect() {
        return maxSelect;
    }

    @Override
    public void setMaxSelect(int max) {
        this.maxSelect = max;
    }
}
