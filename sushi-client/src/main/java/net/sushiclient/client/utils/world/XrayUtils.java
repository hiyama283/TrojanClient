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

package net.sushiclient.client.utils.world;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;

import java.util.HashMap;
import java.util.Map;

public class XrayUtils {

    private static final HashMap<Block, BlockVisibility> visibilityMap = new HashMap<>();
    private static boolean enabled;

    public static BlockVisibility getBlockVisibility(Block block) {
        return visibilityMap.getOrDefault(block, BlockVisibility.VISIBLE);
    }

    public static void setBlockVisibility(Block block, BlockVisibility visibility) {
        if (block == Blocks.AIR) return;
        if (visibility == BlockVisibility.VISIBLE) visibilityMap.remove(block);
        else visibilityMap.put(block, visibility);
    }

    private static void load() {
        Minecraft.getMinecraft().renderGlobal.loadRenderers();
    }

    public static void apply() {
        enabled = true;
        load();
    }

    public static void reset() {
        enabled = false;
        visibilityMap.clear();
        load();
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static Map<Block, BlockVisibility> getAll() {
        return ImmutableMap.copyOf(visibilityMap);
    }
}
