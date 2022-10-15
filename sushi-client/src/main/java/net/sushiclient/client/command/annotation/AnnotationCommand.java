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

package net.sushiclient.client.command.annotation;

import net.sushiclient.client.command.*;
import net.sushiclient.client.command.parser.TypeParser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Stream;

public class AnnotationCommand extends BaseCommand {

    private final String name;
    private final String[] aliases;
    private final String description;
    private final String syntax;
    private final ArrayList<Command> subCommands;
    private final Object object;
    private final Method method;
    private final TypeParser<?>[] parsers;

    private AnnotationCommand(String name, String[] aliases, String description, String syntax, ArrayList<Command> subCommands, Object object, Method method, TypeParser<?>[] parsers) {
        this.name = name;
        this.aliases = aliases;
        this.description = description;
        this.syntax = syntax;
        this.subCommands = subCommands;
        this.object = object;
        this.method = method;
        this.parsers = parsers;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String[] getAliases() {
        return aliases;
    }

    @Override
    public String getDescription() {
        return description.isEmpty() ? null : description;
    }

    @Override
    protected String getSyntax() {
        return syntax.isEmpty() ? null : syntax;
    }

    @Override
    public Command[] getSubCommands() {
        return subCommands.toArray(new Command[0]);
    }

    @Override
    protected void executeDefault(Logger out, List<String> args, List<String> original) {
        if (method == null || parsers == null) {
            super.executeDefault(out, args, original);
            return;
        }
        Stack<String> stack = new Stack<>();
        ArrayList<String> reversed = new ArrayList<>(args);
        Collections.reverse(reversed);
        stack.addAll(reversed);
        Object[] objects = new Object[parsers.length];
        int index = original.size() - args.size();
        for (int i = 0; i < parsers.length; i++) {
            int stackSize = stack.size();
            try {
                objects[i] = parsers[i].parse(index, stack);
                index += stackSize - stack.size();
            } catch (ParseException e) {
                out.send(LogLevel.ERROR, e.getMessage());
                return;
            }
        }
        try {
            method.invoke(object, objects);
        } catch (InvocationTargetException e) {
            e.getTargetException().printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<TypeParser<?>> findTypeParsers(Method method, Collection<TypeParser<?>> parsers) {
        ArrayList<TypeParser<?>> result = new ArrayList<>(method.getParameterCount());
        for (Parameter parameter : method.getParameters()) {
            Token token = parameter.getAnnotation(Token.class);
            Stream<TypeParser<?>> stream = parsers.stream()
                    .filter(p -> parameter.getType().isAssignableFrom(p.getType()))
                    .sorted(Comparator.comparingInt(TypeParser::getPriority));
            if (token != null)
                stream = stream.filter(p -> p.getToken().equals(token.value()));
            Optional<TypeParser<?>> parser = stream.findFirst();
            if (!parser.isPresent())
                throw new IllegalArgumentException("No matching parser for method " + method.getDeclaringClass().getCanonicalName() + "#" + method.getName());
            result.add(parser.get());
        }
        return result;
    }

    public static AnnotationCommand newCommand(Object o) {
        Set<TypeParser<?>> parsers = Commands.getTypeParsers();
        Class<?> c = o.getClass();
        CommandAlias alias = c.getAnnotation(CommandAlias.class);
        if (alias == null) return null;

        ArrayList<Command> subCommands = new ArrayList<>();
        Method defaultMethod = null;
        TypeParser<?>[] defaultParsers = null;
        for (Method method : c.getMethods()) {
            if (method.getAnnotation(Default.class) != null) {
                defaultMethod = method;
                defaultParsers = findTypeParsers(method, parsers).toArray(new TypeParser[0]);
            } else if (method.getAnnotation(SubCommand.class) != null) {
                SubCommand subCommand = method.getAnnotation(SubCommand.class);
                subCommands.add(new AnnotationCommand(subCommand.value(), subCommand.aliases(), subCommand.description(),
                        subCommand.syntax(), new ArrayList<>(), o, method, findTypeParsers(method, parsers).toArray(new TypeParser[0])));
            }
        }
        return new AnnotationCommand(alias.value(), alias.aliases(), alias.description(), alias.syntax(), subCommands, o, defaultMethod, defaultParsers);
    }
}
