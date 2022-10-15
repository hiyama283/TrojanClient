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

package net.sushiclient.client.command.parser;

import net.sushiclient.client.Sushi;
import net.sushiclient.client.command.ParseException;
import net.sushiclient.client.modules.Module;

import java.util.Stack;

public class ModuleParser implements TypeParser<Module> {
    @Override
    public Module parse(int index, Stack<String> args) throws ParseException {
        if (args.isEmpty())
            throw new ParseException("Missing module name/id at index " + index);
        String name = args.pop();
        for (Module module : Sushi.getProfile().getModules().getAll()) {
            if (!module.getName().equalsIgnoreCase(name) && !module.getId().equalsIgnoreCase(name)) continue;
            return module;
        }
        throw new ParseException("A module named " + name + " was not found");
    }

    @Override
    public String getToken() {
        return "module";
    }

    @Override
    public Class<Module> getType() {
        return Module.class;
    }
}
