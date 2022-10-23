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

package net.sushiclient.client.utils.player;

import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.input.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class InputUtils {
    public static void callKeyEvent() {
        if (Keyboard.isRepeatEvent()) return;
        int eventKey = Keyboard.getEventKey();
        if (!Keyboard.isKeyDown(eventKey)) return;
        if (eventKey == 0) return;
        KeyPressEvent event = new KeyPressEvent(eventKey, Keyboard.getEventCharacter());
        EventHandlers.callEvent(event);
    }

    public static void callMouseEvent() {
        if (Mouse.getEventButton() == -1) return;
        int mouse = Mouse.getEventButton();
        ClickType clickType;
        if (mouse == 0) clickType = ClickType.LEFT;
        else if (mouse == 1) clickType = ClickType.RIGHT;
        else if (mouse == 2) clickType = ClickType.MIDDLE;
        else return;
        MouseEvent event;
        if (Mouse.isButtonDown(mouse)) event = new MousePressEvent(clickType);
        else event = new MouseReleaseEvent(clickType);
        EventHandlers.callEvent(event);
    }
}
