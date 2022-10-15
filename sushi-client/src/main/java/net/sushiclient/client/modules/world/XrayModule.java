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

package net.sushiclient.client.modules.world;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.sushiclient.client.config.Config;
import net.sushiclient.client.config.ConfigInjector;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.config.data.BlockName;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.utils.world.BlockVisibility;
import net.sushiclient.client.utils.world.XrayUtils;

import java.util.Arrays;

public class XrayModule extends BaseModule {

    private static final Block[] INIT_VALUES = {Blocks.DIAMOND_ORE, Blocks.GOLD_ORE, Blocks.REDSTONE_ORE, Blocks.IRON_ORE, Blocks.COAL_ORE};

    @Config(id = "blocks", name = "Blocks")
    public BlockName[] blocks = Arrays.stream(INIT_VALUES).map(BlockName::new).toArray(BlockName[]::new);

    public XrayModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        new ConfigInjector(provider).inject(this);
    }

    @Override
    public void onEnable() {
        for (Block block : Block.REGISTRY) {
            if (!Arrays.asList(blocks).contains(new BlockName(block))) {
                XrayUtils.setBlockVisibility(block, BlockVisibility.INVISIBLE);
            }
        }
        XrayUtils.apply();
    }

    @Override
    public void onDisable() {
        XrayUtils.reset();
    }

    @Override
    public String getDefaultName() {
        return "Xray";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.WORLD;
    }
}
