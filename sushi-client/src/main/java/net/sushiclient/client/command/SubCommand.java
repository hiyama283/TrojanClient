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

public interface SubCommand {

    /**
     * Gets the command name.
     *
     * @return the command name of this command
     */
    String getName();

    /**
     * Gets the aliases for this command.
     * Aliases can be used instead of command name.
     *
     * @return the aliases of this command
     */
    default String[] getAliases() {
        return new String[0];
    }

    /**
     * Gets the description of this command.
     *
     * <p>This description cannot be used to pragmatically process anything.
     * This method is purely for convenience for users.</p>
     *
     * @return the description of this method
     */
    String getDescription();

    /**
     * Gets the command syntax.
     * the arguments must be split by space.
     *
     * @return the command syntax
     */
    List<String> complete(List<String> args);

    void execute(Logger out, List<String> args, List<String> original);
}
