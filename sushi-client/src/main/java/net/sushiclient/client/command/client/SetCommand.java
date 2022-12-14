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

import net.sushiclient.client.command.*;
import net.sushiclient.client.command.parser.TypeParser;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.modules.Module;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class SetCommand extends BaseCommand {

    @Override
    public String getName() {
        return "set";
    }

    @Override
    public String getDescription() {
        return "Sets module settings to a specified value";
    }

    @Override
    protected String getSyntax() {
        return "<module> <key> <value>";
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void executeDefault(Logger out, List<String> args, List<String> original) {
        try {
            Stack<String> stack = new Stack<>();
            ArrayList<String> reverse = new ArrayList<>(args);
            Collections.reverse(reverse);
            stack.addAll(reverse);
            Module module = Commands.findParser(Module.class).parse(original.size() - stack.size(), stack);
            if (stack.isEmpty())
                throw new ParseException("A configuration name/id was missing at index " + original.size());
            String key = stack.pop();
            for (Configuration<?> conf : module.getConfigurations().getAll(true)) {
                if (!conf.getId().equalsIgnoreCase(key) && !conf.getName().equalsIgnoreCase(key)) continue;
                Object value = ((TypeParser<Object>) Commands.findParser(conf.getValueClass())).parse(original.size(), stack, conf.getValue());
                ((Configuration<Object>) conf).setValue(value);
                out.send(LogLevel.INFO, "Changed configuration " + conf.getName());
                return;
            }
            throw new ParseException("A configuration named " + key + " was not found");
        } catch (ParseException e) {
            out.send(LogLevel.ERROR, e.getMessage());
        }
    }
}
