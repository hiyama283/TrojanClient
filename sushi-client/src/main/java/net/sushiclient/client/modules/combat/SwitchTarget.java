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

package net.sushiclient.client.modules.combat;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.sushiclient.client.config.data.Named;

public enum SwitchTarget implements Named {
    GAPPLE("Gapple", Items.GOLDEN_APPLE),
    TOTEM("Totem", Items.TOTEM_OF_UNDYING),
    CRYSTAL("Crystal", Items.END_CRYSTAL);

    private final String name;
    private final Item item;

    SwitchTarget(String name, Item item) {
        this.name = name;
        this.item = item;
    }

    public Item getItem() {
        return item;
    }

    @Override
    public String getName() {
        return name;
    }
}
