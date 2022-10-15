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

package net.sushiclient.client;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.*;

@Mod(modid = "sushi", name = "Sushi Client", version = "1")
public class SushiMod {

    private static final Initializer INITIALIZER = new SushiInitializer();

    @EventHandler
    public void construct(FMLConstructionEvent event) {
        INITIALIZER.construct(event);
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        INITIALIZER.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        INITIALIZER.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        INITIALIZER.postInit(event);
    }

    @EventHandler
    public void complete(FMLLoadCompleteEvent event) {
        INITIALIZER.complete(event);
    }
}
