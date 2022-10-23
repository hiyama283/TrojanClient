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

import net.sushiclient.client.Sushi;
import net.sushiclient.client.command.LogLevel;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.config.data.IntRange;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.client.WorldLoadEvent;
import net.sushiclient.client.events.tick.GameTickEvent;
import net.sushiclient.client.modules.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Random;

public class SpammerXDModule extends BaseModule {
    private final Configuration<Boolean> tell;
    private final Configuration<String> command;
    private final Configuration<String> target;
    private final Configuration<IntRange> delay;
    public SpammerXDModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        delay = provider.get("delay", "Delay", null, IntRange.class,
                new IntRange(3, 20, 0, 1));
        tell = provider.get("tell", "Tell", null, Boolean.class, false);
        command = provider.get("command", "Command prefix", null, String.class, "w");
        target = provider.get("target", "Target", null, String.class, "Hiyokomame1443");
    }

    @EventHandler(timing = EventTiming.PRE)
    public void worldLoad(WorldLoadEvent e) {
        setEnabled(false);
    }

    private String content;
    @Override
    public void onEnable() {
        File baseDir = new File("./sushi");
        File spammer = new File(baseDir, "spammer.txt");

        if (Files.exists(spammer.toPath())) {
            try {
                content = FileUtils.readFileToString(spammer, "UTF-8");
            } catch (IOException e) {
                Sushi.getProfile().getLogger().send(LogLevel.ERROR, "Error:" + e.getMessage());
            }
        } else {
            try {
                Files.createFile(spammer.toPath());

                String data = "Hello!";
                FileUtils.writeStringToFile(spammer, data, "UTF-8");
                content = data;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private int delayCount = 0;
    @EventHandler(timing = EventTiming.PRE)
    public void tick(GameTickEvent e) {
        if (delay.getValue().getCurrent() == 0 || delayCount % delay.getValue().getCurrent() == 0) {
            delayCount = 0;
            StringBuilder s = new StringBuilder();
            if (tell.getValue()) {
                s.append("/")
                        .append(command.getValue())
                        .append(" ")
                        .append(target.getValue())
                        .append(" ");
            }

            s.append(content.trim());

            getPlayer().sendChatMessage(s.toString() + new Random().nextInt(100000));
        }
    }

    @Override
    public String getDefaultName() {
        return "SpammerXD";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.PLAYER;
    }
}
