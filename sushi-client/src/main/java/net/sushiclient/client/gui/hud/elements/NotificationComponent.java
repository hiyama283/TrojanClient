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

package net.sushiclient.client.gui.hud.elements;

import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.Configurations;
import net.sushiclient.client.config.data.EspColor;
import net.sushiclient.client.config.data.IntRange;
import net.sushiclient.client.gui.hud.BaseHudElementComponent;
import net.sushiclient.client.utils.render.GuiUtils;
import net.sushiclient.client.utils.render.TextPreview;
import net.sushiclient.client.utils.render.TextSettings;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class NotificationComponent extends BaseHudElementComponent {
    public static NotificationComponent self;
    private final Configuration<EspColor> useColor;
    private final Configuration<String> font;
    private final Configuration<IntRange> pts;
    private final Configuration<Boolean> shadow;
    private final Configuration<IntRange> padding;
    private final Configuration<IntRange> widthSize;
    private final Configuration<IntRange> heightSize;
    private final Configuration<IntRange> baseSize;
    private final Configuration<IntRange> sideWidth;
    public NotificationComponent(Configurations configurations, String id, String name) {
        super(configurations, id, name);
        useColor = getConfiguration("color", "Color", null, EspColor.class,
                new EspColor(new Color(0, 255, 0, 255), false, false));
        padding = getConfiguration("padding", "Padding", null, IntRange.class,
                new IntRange(9, 30, 1, 1));
        widthSize = getConfiguration("width_size", "Width Size", null, IntRange.class,
                new IntRange(15, 40, 1, 1));
        heightSize = getConfiguration("height_size", "Height Size", null, IntRange.class,
                new IntRange(15, 40, 1, 1));
        font = getConfiguration("font", "Font", null, String.class, "LexendDeca");
        pts = getConfiguration("pts", "Pts", null, IntRange.class,
                new IntRange(9, 30, 1, 1));
        shadow = getConfiguration("shadow", "Shadow", null, Boolean.class, true);
        baseSize = getConfiguration("base_size", "Based size", null, IntRange.class,
                new IntRange(110, 300, 0, 10));
        sideWidth = getConfiguration("side_width", "Side width", null, IntRange.class,
                new IntRange(2, 10, 1, 1));
        self = this;
    }

    private final HashMap<Integer, NotifyInformation> notifyList = new HashMap<>();

    @Override
    public void onRender() {
        Color color = useColor.getValue().getCurrentColor();
        TextSettings settings = new TextSettings(font.getValue(), useColor.getValue(), pts.getValue().getCurrent(), shadow.getValue());

        AtomicInteger y = new AtomicInteger(0);
        notifyList.forEach((k, v) -> {
            boolean b = v.render(getWindowX(), getWindowY() + y.get(), color, settings,
                    widthSize.getValue().getCurrent(), heightSize.getValue().getCurrent(), -1,
                    baseSize.getValue().getCurrent(), sideWidth.getValue().getCurrent());
            if (b) {
                notifyList.remove(k);
            }

            double height = GuiUtils.prepareText(v.message, settings).getHeight();
            y.addAndGet((int) -(height + padding.getValue().getCurrent() + heightSize.getValue().getCurrent()));
        });
    }

    public void send(int id, String message, long length) {
        synchronized (notifyList) {
            if (notifyList.containsKey(id)) {
                NotifyInformation info = notifyList.get(id);
                info.update(message, length);
                notifyList.replace(id, info);
            } else {
                notifyList.put(id, new NotifyInformation(id, message, length));
            }
        }
    }

    @Override
    public void onRelocate() {
        setWidth(150);
        setHeight(35);
    }

    private static class NotifyInformation {
        private final int ID;
        private final long LENGTH;
        private final java.util.List<SimpleTimer> timer = new ArrayList<>();
        private String message;
        private RenderStep step;
        public NotifyInformation(int id, String message, long length) {
            ID = id;
            LENGTH = length;
            this.message = message;
            step = RenderStep.ENTRY;
            timer.add(new SimpleTimer(length)); // String view length
            timer.add(new SimpleTimer(50)); // Filled time
        }

        public long getLENGTH() {
            return LENGTH;
        }

        public int getID() {
            return ID;
        }

        public void update(String message) {
            timer.get(0).reset();
            this.message = message;
        }

        public void update(String message, long length) {
            timer.get(0).reset(length);
            this.message = message;
        }

        private double awa = -1;

        private void draw(TextPreview preview, int side, double windowX, double windowY, double margin, double height) {
            preview.draw(windowX + 5 + side, windowY + (height * 0.5 - 1.0 - preview.getHeight() * 0.5) + margin);
        }

        public boolean render(double windowX, double windowY, Color color, TextSettings settings, int width_size, int height_size,
                              double margin, int baseSize, int sideWidth) {
            TextPreview preview = GuiUtils.prepareText(message, settings);
            double width = Math.max(Math.ceil(preview.getWidth() + width_size), baseSize);
            double height = preview.getHeight() + height_size;
            double speed = message.length() * 0.25;

            if (!step.equals(RenderStep.HIDE_ALL)) {
                GuiUtils.drawRect(windowX, windowY, width, height, new Color(0, 0, 0, 100));
            }

            if (step.equals(RenderStep.ENTRY)) {
                if (awa == -1) awa = (windowX + width) - (width * 0.2);
                if (awa <= windowX) {
                    step = step.next();
                    awa = windowX;
                    timer.get(1).reset();
                    return false;
                }

                GuiUtils.drawRect(awa, windowY, (windowX + width) - awa, height, color);
                awa -= speed;
            } else if (step.equals(RenderStep.FILLED_BY_ENTRY)) {
                if (timer.get(1).isEnd()) {
                    step = step.next();
                    timer.get(0).reset();
                    return false;
                }

                draw(preview, sideWidth, windowX, windowY, margin, height);
                GuiUtils.drawRect(windowX, windowY, width, height, color);
            } else if (step.equals(RenderStep.SHOW_MSG)) {
                boolean slideIsEnd = awa >= windowX + width;
                if (slideIsEnd && timer.get(0).isEnd()) {
                    step = step.next();
                    awa = 0;
                    return false;
                }

                draw(preview, sideWidth, windowX, windowY, margin, height);
                GuiUtils.drawRect(windowX, windowY, sideWidth, height, color);

                if (!slideIsEnd) {
                    GuiUtils.drawRect(windowX, windowY, (windowX + width) - awa, height, color);
                    awa += speed;
                }
            } else if (step.equals(RenderStep.HIDE_MSG)) {
                if (awa >= width) {
                    step = step.next();
                    awa = (windowX + width) - (width * 0.2);
                    timer.get(1).reset();
                    return false;
                }

                draw(preview, sideWidth, windowX, windowY, margin, height);
                GuiUtils.drawRect(windowX, windowY, awa, height, color);
                awa += speed;
            } else if (step.equals(RenderStep.FILLED_BY_HIDE_MSG)) {
                if (timer.get(1).isEnd()) {
                    step = step.next();
                    timer.get(0).reset();
                    awa = 0;
                    return false;
                }
                GuiUtils.drawRect(windowX, windowY, width, height, color);
            } else if (step.equals(RenderStep.HIDE_ALL)) {
                if (awa >= width) {
                    return true;
                }

                GuiUtils.drawRect(windowX + awa, windowY, width - awa, height, new Color(0, 0, 0, 100));
                GuiUtils.drawRect(windowX + awa, windowY, width - awa, height, color);
                awa += speed;
            }

            return false;
        }
    }

    private static class SimpleTimer {
        private long time;
        private long startTime;
        public SimpleTimer(long time) {
            this.time = time;
            this.startTime = System.currentTimeMillis();
        }

        public boolean isEnd() {
            return (System.currentTimeMillis() - startTime) >= time;
        }

        public void reset() {
            startTime = System.currentTimeMillis();
        }

        public void reset(long time) {
            this.reset();
            this.time = time;
        }
    }

    private static class RenderStep {
        public static final RenderStep ENTRY = new RenderStep(0);
        public static final RenderStep FILLED_BY_ENTRY = new RenderStep(1);
        public static final RenderStep SHOW_MSG = new RenderStep(2);
        public static final RenderStep HIDE_MSG = new RenderStep(3);
        public static final RenderStep FILLED_BY_HIDE_MSG = new RenderStep(4);
        public static final RenderStep HIDE_ALL = new RenderStep(5);

        private final int step;
        private RenderStep(int integerStep) {
            this.step = integerStep;
        }

        public int getStep() {
            return step;
        }

        public RenderStep next() {
            switch (this.step) {
                case 0:
                    return FILLED_BY_ENTRY;
                case 1:
                    return SHOW_MSG;
                case 2:
                    return HIDE_MSG;
                case 3:
                    return FILLED_BY_HIDE_MSG;
                case 4:
                    return HIDE_ALL;
            }

            return null;
        }

        public boolean equals(RenderStep stepObj) {
            return this.getStep() == stepObj.getStep();
        }
    }
}
