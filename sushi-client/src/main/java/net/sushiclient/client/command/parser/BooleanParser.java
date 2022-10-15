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

import net.sushiclient.client.command.ParseException;

import java.util.Stack;

public class BooleanParser implements TypeParser<Boolean> {
    @Override
    public Boolean parse(int index, Stack<String> args) throws ParseException {
        if (args.isEmpty())
            throw new ParseException("Missing boolean at index " + index);
        try {
            return Boolean.parseBoolean(args.pop());
        } catch (NumberFormatException e) {
            throw new ParseException("Invalid boolean at index " + index);
        }
    }

    @Override
    public Boolean parse(int index, Stack<String> args, Boolean original) throws ParseException {
        if (args.isEmpty())
            throw new ParseException("Missing boolean at index " + index);
        String bool = args.pop();
        return bool.equalsIgnoreCase("toggle") ? !original : Boolean.parseBoolean(bool);
    }

    @Override
    public String getToken() {
        return "double";
    }

    @Override
    public Class<Boolean> getType() {
        return Boolean.class;
    }
}
