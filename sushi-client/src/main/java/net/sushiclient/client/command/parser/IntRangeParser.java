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
import net.sushiclient.client.config.data.IntRange;

import java.util.Stack;

public class IntRangeParser implements TypeParser<IntRange> {

    @Override
    public IntRange parse(int index, Stack<String> args, IntRange original) throws ParseException {
        if (args.isEmpty())
            throw new ParseException("Missing integer at index " + index);
        try {
            return new IntRange(Integer.parseInt(args.pop()),
                    original.getTop(), original.getBottom(), original.getStep());
        } catch (NumberFormatException e) {
            throw new ParseException("Invalid integer at index " + index);
        }
    }

    @Override
    public String getToken() {
        return "double_range";
    }

    @Override
    public Class<IntRange> getType() {
        return IntRange.class;
    }
}
