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
import net.sushiclient.client.config.data.BlockName;

import java.util.Stack;

public class BlockArrayParser implements TypeParser<BlockName[]> {

    @Override
    public BlockName[] parse(int index, Stack<String> args) throws ParseException {
        if (args.isEmpty())
            throw new ParseException("Missing list at index " + index);
        String text = args.pop();
        String[] split = text.split(",");
        BlockName[] result = new BlockName[split.length];
        for (int i = 0; i < split.length; i++) {
            BlockName name = BlockName.fromName(split[i]);
            if (name == null)
                throw new ParseException(split[i] + " is not a valid block name");
            result[i] = name;
        }
        return result;
    }

    @Override
    public String getToken() {
        return "blocks";
    }

    @Override
    public Class<BlockName[]> getType() {
        return BlockName[].class;
    }
}
