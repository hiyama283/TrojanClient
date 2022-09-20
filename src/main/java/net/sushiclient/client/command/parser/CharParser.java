package net.sushiclient.client.command.parser;

import net.sushiclient.client.command.ParseException;

import java.util.Stack;

public class CharParser implements TypeParser<Character> {
    @Override
    public Character parse(int index, Stack<String> args) throws ParseException {
        if (args.isEmpty())
            throw new ParseException("Missing character at index " + index);
        String prefix = args.pop();
        if (prefix.length() > 1)
            throw new ParseException("Invalid character at index " + index);
        return prefix.charAt(0);
    }

    @Override
    public String getToken() {
        return "char";
    }

    @Override
    public Class<Character> getType() {
        return Character.class;
    }
}
