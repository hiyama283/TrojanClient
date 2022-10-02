package net.sushiclient.client.gui.theme.simple.config;

import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.FakeConfiguration;
import net.sushiclient.client.config.data.EspColor;
import net.sushiclient.client.config.data.IntRange;
import net.sushiclient.client.gui.Insets;
import net.sushiclient.client.gui.*;
import net.sushiclient.client.gui.layout.FlowDirection;
import net.sushiclient.client.gui.layout.FlowLayout;
import net.sushiclient.client.gui.theme.ThemeConstants;
import net.sushiclient.client.gui.theme.simple.SimpleColorPickerComponent;
import net.sushiclient.client.gui.theme.simple.SimpleColorPickerHeaderComponent;
import net.sushiclient.client.utils.render.GuiUtils;

import java.awt.*;

public class SimpleEspColorComponent extends AnyPanelComponent implements ConfigComponent<EspColor> {

    private final ThemeConstants constants;
    private final Configuration<EspColor> configuration;
    private final Configuration<Color> color;
    private final Configuration<Boolean> rainbow;
    private final Configuration<IntRange> alpha;
    private final SimpleColorPickerComponent colorPickerComponent;
    private final SimpleBooleanComponent rainbowComponent;
    private final SimpleIntRangeComponent alphaComponent;
    private boolean ignoreUpdate;

    private Color applyAlpha(Color c) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha.getValue().getCurrent());
    }

    public SimpleEspColorComponent(ThemeConstants constants, Configuration<EspColor> c) {
        this.constants = constants;
        this.configuration = c;
        EspColor espColor = c.getValue();
        this.color = new FakeConfiguration<>("color", c.getName(), null, Color.class, espColor.getColor());
        this.rainbow = new FakeConfiguration<>("rainbow", "Rainbow", null, Boolean.class, espColor.isRainbow());
        this.alpha = new FakeConfiguration<>("alpha", "Alpha", null, IntRange.class, new IntRange(espColor.getColor().getAlpha(), 255, 0, 1));

        this.colorPickerComponent = new SimpleColorPickerComponent(constants, c.getName(), color.getValue()) {
            @Override
            protected void onChange(Color c) {
                if (!color.getValue().equals(c)) {
                    ignoreUpdate = true;
                    color.setValue(c);
                    ignoreUpdate = false;
                }
            }
        };
        this.rainbowComponent = new SimpleBooleanComponent(constants, rainbow);
        this.alphaComponent = new SimpleIntRangeComponent(constants, alpha);

        c.addHandler(esp -> {
            if (!ignoreUpdate && !applyAlpha(esp.getColor()).equals(applyAlpha(color.getValue()))) {
                color.setValue(applyAlpha(esp.getColor()));
            }
            if (esp.isRainbow() != rainbow.getValue()) {
                rainbow.setValue(esp.isRainbow());
            }
        });

        color.addHandler(it -> c.setValue(c.getValue().setColor(applyAlpha(it))));
        rainbow.addHandler(it -> c.setValue(c.getValue().setRainbow(it)));
        alpha.addHandler(it -> c.setValue(c.getValue().setAlpha(it.getCurrent())));

        setLayout(new FlowLayout(this, FlowDirection.DOWN));

        double marginLeft = colorPickerComponent.getMarginLeft();
        double marginRight = colorPickerComponent.getMarginRight();
        colorPickerComponent.setMargin(new Insets(0, 0, 0, 0));
        rainbowComponent.setMargin(new Insets(0, marginLeft, 0, marginRight));
        alphaComponent.setMargin(new Insets(0, marginLeft, 2, marginRight));

        SmoothCollapseComponent<?> collapseComponent = new SmoothCollapseComponent<>(new AnyPanelComponent() {{
            add(colorPickerComponent);
            add(rainbowComponent);
            add(alphaComponent);
            setLayout(new FlowLayout(this, FlowDirection.DOWN));
        }}, CollapseMode.DOWN, 100);

        add(new SimpleColorPickerHeaderComponent(constants, collapseComponent, color::getValue, c.getName()));
        add(collapseComponent);
    }

    @Override
    public void onRender() {
        GuiUtils.drawRect(getWindowX(), getWindowY(), getWidth(), getHeight(), constants.outlineColor.getValue());
        super.onRender();
    }

    @Override
    public Configuration<EspColor> getValue() {
        return configuration;
    }
}
