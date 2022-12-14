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

package net.sushiclient.client.modules;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class ResourceImageCategory implements Category {

    private final String name;
    private final Image icon;

    public ResourceImageCategory(String name, String icon) {
        this.name = name;
        this.icon = readImage(icon);
    }

    private static BufferedImage readImage(String resource) {
        try (InputStream in = ResourceImageCategory.class.getResourceAsStream(resource)) {
            if (in == null) return null;
            else return ImageIO.read(in);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Image getIcon() {
        return icon;
    }
}
