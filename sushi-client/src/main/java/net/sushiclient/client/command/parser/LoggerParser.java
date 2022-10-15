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
import net.sushiclient.client.command.Logger;
import net.sushiclient.client.command.ParseException;

import java.util.Stack;

public class LoggerParser implements TypeParser<Logger> {
    @Override
    public Logger parse(int index, Stack<String> args) throws ParseException {
        return Sushi.getProfile().getLogger();
    }

    @Override
    public String getToken() {
        return "message";
    }

    @Override
    public Class<Logger> getType() {
        return Logger.class;
    }
}
