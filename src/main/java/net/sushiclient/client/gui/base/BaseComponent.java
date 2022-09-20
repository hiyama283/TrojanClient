package net.sushiclient.client.gui.base;

import net.sushiclient.client.events.input.ClickType;
import net.sushiclient.client.gui.*;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

public class BaseComponent implements Component {

    private boolean focused;
    private Anchor anchor;
    private Origin origin;
    private Component parent;
    private ComponentContext<?> context;
    private double x;
    private double y;
    private double width;
    private double height;
    private boolean visible;
    private Insets margin = new Insets(0, 0, 0, 0);
    private final ArrayList<ComponentHandler> handlers = new ArrayList<>();

    public BaseComponent() {
        this.anchor = Anchor.TOP_LEFT;
        this.origin = Origin.TOP_LEFT;
    }

    public BaseComponent(int x, int y, int width, int height, Anchor anchor, Origin origin, Component parent) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.anchor = anchor;
        this.origin = origin;
        this.parent = parent;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public void setX(double x) {
        this.x = x;
        handlers.forEach(c -> c.setX(x));
    }

    @Override
    public void setY(double y) {
        this.y = y;
        handlers.forEach(c -> c.setY(y));
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getHeight() {
        return height;
    }

    @Override
    public void setWidth(double width) {
        this.width = width;
        handlers.forEach(c -> c.setWidth(width));
    }

    @Override
    public void setHeight(double height) {
        this.height = height;
        handlers.forEach(c -> c.setHeight(height));
    }

    @Override
    public Insets getMargin() {
        return margin;
    }

    @Override
    public void setMargin(Insets margin) {
        this.margin = margin;
        handlers.forEach(c -> c.setMargin(margin));
    }

    @Override
    public Anchor getAnchor() {
        return anchor;
    }

    @Override
    public void setAnchor(Anchor anchor) {
        this.anchor = anchor;
        handlers.forEach(c -> c.setAnchor(anchor));
    }

    @Override
    public Origin getOrigin() {
        return origin;
    }

    @Override
    public void setOrigin(Origin origin) {
        this.origin = origin;
        handlers.forEach(c -> c.setOrigin(origin));
    }

    @Override
    public Component getParent() {
        return parent;
    }

    @Override
    public void setParent(Component parent) {
        this.parent = parent;
        handlers.forEach(c -> c.setParent(parent));
    }

    @Override
    public ComponentContext<?> getContext() {
        return context;
    }

    @Override
    public void setContext(ComponentContext<?> context) {
        this.context = context;
        handlers.forEach(c -> c.setContext(context));
    }

    @Override
    public boolean isFocused() {
        return focused;
    }

    @Override
    public void setFocused(boolean focused) {
        this.focused = focused;
        handlers.forEach(c -> c.setFocused(focused));
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void setVisible(boolean visible) {
        boolean currentVisible = this.visible;
        this.visible = visible;
        if (!currentVisible && visible)
            onShow();
        if (currentVisible && !visible)
            onClose();
    }

    @Override
    public void onRelocate() {
        handlers.forEach(ComponentHandler::onRelocate);
    }

    @Override
    public void onRender() {
        handlers.forEach(ComponentHandler::onRender);
    }

    @Override
    public void onClick(int x, int y, ClickType type) {
        handlers.forEach(c -> c.onClick(x, y, type));
    }

    @Override
    public void onHover(int x, int y) {
        handlers.forEach(c -> c.onHover(x, y));
    }

    @Override
    public void onHold(int fromX, int fromY, int toX, int toY, ClickType type, MouseStatus status) {
        handlers.forEach(c -> c.onHold(fromX, fromY, toX, toY, type, status));
    }

    @Override
    public void onScroll(int deltaX, int deltaY) {
        handlers.forEach(c -> c.onScroll(deltaX, deltaY));
    }

    @Override
    public boolean onKeyPressed(int keyCode, char key) {
        handlers.forEach(c -> c.onKeyPressed(keyCode, key));
        return false;
    }

    @Override
    public boolean onKeyReleased(int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            getContext().close();
            return true;
        }
        handlers.forEach(c -> c.onKeyReleased(keyCode));
        return false;
    }

    @Override
    public void onShow() {
        handlers.forEach(ComponentHandler::onShow);
    }

    @Override
    public void onClose() {
        handlers.forEach(ComponentHandler::onClose);
    }

    @Override
    public void addHandler(ComponentHandler handler) {
        handlers.add(handler);
    }

    @Override
    public boolean removeHandler(ComponentHandler handler) {
        return handlers.remove(handler);
    }

    @Override
    public List<ComponentHandler> getHandlers() {
        return handlers;
    }
}
