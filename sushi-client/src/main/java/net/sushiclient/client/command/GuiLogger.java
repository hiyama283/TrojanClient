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

import net.sushiclient.client.gui.hud.elements.NotificationComponent;

public final class GuiLogger {
    public static void send(int id, String message, long length) {
        NotificationComponent.self.send(id, message, length);
    }

    public static void send(String message, long length) {
        send(message.hashCode(), message, length);
    }

    public static void send(String message) {
        send(message.hashCode(), message, 1000);
    }
}
