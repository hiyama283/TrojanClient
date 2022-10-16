package net.sushiclient.client.gui.theme.simple;

import net.sushiclient.client.events.input.ClickType;
import net.sushiclient.client.gui.Component;
import net.sushiclient.client.gui.FrameComponent;
import net.sushiclient.client.gui.Insets;
import net.sushiclient.client.gui.MouseStatus;
import net.sushiclient.client.gui.base.BasePanelComponent;
import net.sushiclient.client.gui.layout.Layout;
import net.sushiclient.client.gui.theme.ThemeConstants;
import net.sushiclient.client.utils.render.GuiUtils;

import java.awt.Color;

public class SimpleFrameComponent<T extends Component> extends BasePanelComponent<T> implements FrameComponent<T>, Layout {

    private static final double MARGIN = 2;
    private static final double BAR_WIDTH = 15;
    private static final double BAR_HEIGHT = 8;
    private final ThemeConstants constants;
    private final T component;
    private boolean hover;
    private boolean hold;

    public SimpleFrameComponent(ThemeConstants constants, T component) {
        this.constants = constants;
        this.component = component;
        add(component);
        setLayout(this);
    }

    @Override
    public T getValue() {
        return component;
    }

    @Override
    public void onRender() {
        boolean outline = true;
        Color backgroundColor = constants.crossMarkBackgroundColor.getValue();
        if (hover) {
            outline = false;
            backgroundColor = constants.hoverCrossMarkBackgroundColor.getValue();
        }
        if (hold) {
            outline = false;
            backgroundColor = constants.selectedCrossMarkBackgroundColor.getValue();
        }
        hover = false;
        hold = false;
        GuiUtils.drawRect(component.getWindowX() - MARGIN, component.getWindowY() - MARGIN, component.getWidth() + 2 * MARGIN, component.getHeight() + 2 * MARGIN, constants.menuBarColor.getValue());
        GuiUtils.drawRect(getWindowX(), getWindowY(), getWidth(), BAR_HEIGHT + 2 * MARGIN, constants.menuBarColor.getValue());
        if (outline)
            GuiUtils.drawOutline(getWindowX() + getWidth() - BAR_WIDTH - MARGIN, getWindowY() + MARGIN, BAR_WIDTH, BAR_HEIGHT, backgroundColor, 1);
        else
            GuiUtils.drawRect(getWindowX() + getWidth() - BAR_WIDTH - MARGIN, getWindowY() + MARGIN, BAR_WIDTH, BAR_HEIGHT, backgroundColor);
        super.onRender();
    }

    private boolean isMenuBar(int x, int y) {
        return getWindowX() <= x &&
                getWindowY() <= y &&
                x <= getWindowX() + getWidth() &&
                y <= getWindowY() + BAR_HEIGHT + 2 * MARGIN;
    }

    private boolean isCrossMark(int x, int y) {
        return getWindowX() + getWidth() - BAR_WIDTH - MARGIN <= x &&
                getWindowY() + MARGIN <= y &&
                x <= getWindowX() + getWidth() - MARGIN &&
                y <= getWindowY() + BAR_HEIGHT + MARGIN;
    }

    @Override
    public void onHover(int x, int y) {
        if (isCrossMark(x, y)) {
            hover = true;
        }
        super.onHover(x, y);
    }

    @Override
    public void onClick(int x, int y, ClickType type) {
        if (isCrossMark(x, y)) {
            getContext().close();
        } else {
            super.onClick(x, y, type);
        }
    }

    @Override
    public void onHold(int fromX, int fromY, int toX, int toY, ClickType type, MouseStatus status) {
        if (type != ClickType.LEFT) {
            super.onHold(fromX, fromY, toX, toY, type, status);
            return;
        }
        if (isCrossMark(fromX, fromY)) {
            hold = true;
            if (status == MouseStatus.END) {
                getContext().close();
            }
        } else if (isMenuBar(fromX, fromY)) {
            setWindowX(toX - fromX + getWindowX());
            setWindowY(toY - fromY + getWindowY());
        } else {
            super.onHold(fromX, fromY, toX, toY, type, status);
        }
    }

    @Override
    public void relocate() {
        component.setParent(this);
        component.setX(MARGIN);
        component.setY(BAR_HEIGHT + 2 * MARGIN);
        component.setWidth(getWidth() - 2 * MARGIN);
        component.setHeight(getHeight() - 2 * MARGIN);
        component.onRelocate();
    }

    @Override
    public Insets getFrame() {
        return new Insets(BAR_HEIGHT + 2 * MARGIN, MARGIN, MARGIN, MARGIN);
    }
}
