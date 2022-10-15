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

import java.util.List;

/**
 * Represents single command.
 */
public interface Command extends SubCommand {

    /**
     * Executes the command.
     *
     * @param out  the handler messages are output to
     * @param args command arguments
     */
    default void execute(Logger out, List<String> args) {
        execute(out, args, args);
    }

    /**
     * Gets all sub commands of the command.
     *
     * @return all sub commands of the command
     */
    default SubCommand[] getSubCommands() {
        return new SubCommand[0];
    }
}
