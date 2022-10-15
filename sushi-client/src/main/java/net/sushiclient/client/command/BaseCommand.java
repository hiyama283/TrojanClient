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

package net.sushiclient.client.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

abstract public class BaseCommand implements Command {

    private SubCommand getSubCommand(String arg, boolean equals) {
        arg = arg.toLowerCase();
        for (SubCommand command : getSubCommands()) {
            for (String alias : command.getAliases()) {
                if (alias.equalsIgnoreCase(arg) || (!equals && alias.toLowerCase().startsWith(arg))) {
                    return command;
                }
            }
            if (command.getName().equalsIgnoreCase(arg) || (!equals && command.getName().toLowerCase().startsWith(arg)))
                return command;
        }
        return null;
    }

    private String complete(String arg) {
        arg = arg.toLowerCase();
        for (SubCommand command : getSubCommands()) {
            for (String alias : command.getAliases()) {
                if (alias.toLowerCase().startsWith(arg)) {
                    return alias;
                }
            }
            if (command.getName().toLowerCase().startsWith(arg)) {
                return command.getName();
            }
        }
        return null;
    }

    @Override
    public List<String> complete(List<String> args) {
        if (args.isEmpty()) {
            String syntax = getSyntax();
            if (syntax == null)
                return Collections.emptyList();
            else
                return Arrays.asList(getSyntax().split("\\s+"));
        }

        SubCommand command = getSubCommand(args.get(0), false);
        if (command == null) return Collections.emptyList();
        List<String> complete = command.complete(args.subList(1, args.size()));
        ArrayList<String> result = new ArrayList<>(complete.size() + 1);
        result.add(complete(args.get(0)));
        result.addAll(complete);
        return result;
    }

    @Override
    public void execute(Logger out, List<String> args, List<String> original) {
        if (args.isEmpty()) {
            executeDefault(out, args, original);
            return;
        }
        SubCommand command = getSubCommand(args.get(0), true);
        if (command == null) {
            executeDefault(out, args, original);
        } else {
            command.execute(out, args.subList(1, args.size()), args);
        }
    }

    abstract protected String getSyntax();

    protected void executeDefault(Logger out, List<String> args, List<String> original) {
        out.send(LogLevel.INFO, "Sub commands:");
        for (SubCommand subCommand : getSubCommands()) {
            String description = subCommand.getDescription();
            if (description == null)
                out.send(LogLevel.INFO, "  " + subCommand.getName());
            else
                out.send(LogLevel.INFO, "  " + subCommand.getName() + " - " + subCommand.getDescription());
        }
    }
}
