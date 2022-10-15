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

package net.sushiclient.client.command.client;

import net.sushiclient.client.Profile;
import net.sushiclient.client.Sushi;
import net.sushiclient.client.command.LogLevel;
import net.sushiclient.client.command.Logger;
import net.sushiclient.client.command.annotation.CommandAlias;
import net.sushiclient.client.command.annotation.SubCommand;

@CommandAlias(value = "profile", description = "Edits profiles")
public class ProfileCommand {

    @SubCommand("list")
    public void onList(Logger logger) {
        logger.send(LogLevel.INFO, "Profiles: ");
        for (String str : Sushi.getProfiles().getAll()) {
            if (str.equals(Sushi.getProfiles().getName(Sushi.getProfile()))) {
                logger.send(LogLevel.INFO, "  " + str + " (Used)");
            } else {
                logger.send(LogLevel.INFO, "  " + str);
            }
        }
    }

    @SubCommand(value = "save", syntax = "<name>")
    public void onSave(Logger logger) {
        Sushi.getProfile().save();
        logger.send(LogLevel.INFO, "The profile has been saved");
    }

    @SubCommand(value = "clone", syntax = "<name>")
    public void onClone(Logger logger, String name) {
        String old = Sushi.getProfiles().getName(Sushi.getProfile());
        if (old == null) {
            logger.send(LogLevel.ERROR, "Current profile could not be cloned");
            return;
        }
        Sushi.getProfile().save();
        Profile profile = Sushi.getProfiles().clone(old, name);
        if (profile == null) {
            logger.send(LogLevel.INFO, "Could not clone the profile");
        } else {
            logger.send(LogLevel.INFO, "Cloned the profile");
        }
    }

    @SubCommand(value = "remove", syntax = "<name>")
    public void onRemove(Logger logger, String name) {
        boolean successful = Sushi.getProfiles().remove(name);
        if (successful) {
            logger.send(LogLevel.INFO, "Removed the profile");
        } else {
            logger.send(LogLevel.INFO, "Could not delete the profile");
        }
    }

    @SubCommand(value = "set", syntax = "<name>")
    public void onSet(Logger logger, String str) {
        Profile profile = Sushi.getProfiles().load(str);

        Sushi.getProfile().getModules().disable();
        Sushi.getProfile().save();

        Sushi.setProfile(profile);
        Sushi.getProfile().load();
        Sushi.getProfile().getModules().enable();
        logger.send(LogLevel.INFO, "Changed the profile to " + str);
    }
}
