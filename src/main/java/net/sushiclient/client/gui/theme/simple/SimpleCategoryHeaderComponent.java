package net.sushiclient.client.gui.theme.simple;

import net.sushiclient.client.events.input.ClickType;
import net.sushiclient.client.gui.Component;
import net.sushiclient.client.gui.MouseStatus;
import net.sushiclient.client.gui.base.BaseComponent;
import net.sushiclient.client.gui.theme.ThemeConstants;
import net.sushiclient.client.modules.Category;
import net.sushiclient.client.utils.render.GuiUtils;
import net.sushiclient.client.utils.render.TextPreview;

public class SimpleCategoryHeaderComponent extends BaseComponent {

    private final ThemeConstants constants;
    private final Component parent;
    private final Category category;
    private int holdX;
    private int holdY;

    public SimpleCategoryHeaderComponent(ThemeConstants constants, Category category, Component parent) {
        this.constants = constants;
        this.category = category;
        this.parent = parent;
        setHeight(16);
    }

    @Override
    public void onRender() {
        GuiUtils.drawRect(getWindowX(), getWindowY(), getWidth(), getHeight(), constants.headerColor.getValue());
        TextPreview preview = GuiUtils.prepareText(category.getName(), constants.font.getValue(), constants.textColor.getValue(), 10, true);
        preview.draw(getWindowX() + (getWidth() - preview.getWidth()) / 2 - 1, getWindowY() + (getHeight() - preview.getHeight()) / 2 - 1);
    }

    @Override
    public void onHold(int fromX, int fromY, int toX, int toY, ClickType type, MouseStatus status) {
        if (type != ClickType.LEFT) return;
        if (status == MouseStatus.START) {
            this.holdX = (int) (toX - getWindowX());
            this.holdY = (int) (toY - getWindowY());
            return;
        }
        parent.setWindowX(toX - holdX);
        parent.setWindowY(toY - holdY);
    }
}
