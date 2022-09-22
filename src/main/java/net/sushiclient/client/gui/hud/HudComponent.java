package net.sushiclient.client.gui.hud;

import net.sushiclient.client.Sushi;
import net.sushiclient.client.config.ConfigurationCategory;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.gui.Anchor;
import net.sushiclient.client.gui.Component;
import net.sushiclient.client.gui.Origin;
import net.sushiclient.client.gui.base.BasePanelComponent;
import net.sushiclient.client.gui.hud.elements.*;
import net.sushiclient.client.modules.Module;
import net.sushiclient.client.utils.render.GuiUtils;

import java.util.HashSet;

public class HudComponent extends BasePanelComponent<HudElementComponent> {

    private final RootConfigurations conf;
    private final Module module;
    private final HashSet<HudElementComponent> moduleComponents = new HashSet<>();
    private Component current;

    public HudComponent(RootConfigurations conf, Module module) {
        this.conf = conf;
        this.module = module;
        addVirtual(new HotbarHudElementComponent());
        addElement(ModuleListComponent::new, "modules", "Modules");
        addElement(ArmorComponent::new, "armor", "Armor");
        addElement(CoordinatesComponent::new, "coordinates", "Coordinates");
        addElement(OverworldComponent::new, "overworld", "Overworld");
        addElement(NetherComponent::new, "nether", "Nether");
        addElement(FpsComponent::new, "fps", "FPS");
        addElement(TpsComponent::new, "tps", "TPS");
        addElement(SpeedComponent::new, "speed", "Speed");
        addElement(WatermarkComponent::new, "watermark", "Watermark");
    }

    private void addVirtual(VirtualHudElementComponent component) {
        add(component, true);
    }

    private void setup(HudElementComponent component) {
        if (current != null) {
            component.setX(0);
            component.setY(0);
            component.setParent(current);
            component.setOrigin(Origin.TOP_LEFT);
            component.setAnchor(Anchor.BOTTOM_LEFT);
        }
        current = component;
    }

    private void addElement(ElementConstructor constructor, String id, String name) {
        ConfigurationCategory category = conf.getCategory(id, name, null);
        HudElementComponent component = constructor.newElement(category, id, name);
        setup(component);
        component.addHandler(new ConfigHandler(component, this, category));
        add(component, true);
    }

    public HudElementComponent getHudElementComponent(String id) {
        for (HudElementComponent component : this) {
            if (component.getId().equals(id))
                return component;
        }
        return null;
    }

    @Override
    public void onShow() {
        moduleComponents.clear();
        for (Module m : Sushi.getProfile().getModules().getAll()) {
            for (ElementFactory factory : m.getElementFactories()) {
                String id = factory.getId();
                String name = factory.getName();
                ConfigurationCategory category = conf.getCategory(id, name, null);
                HudElementComponent component = factory.getElementConstructor().newElement(category, id, name);
                setup(component);
                component.addHandler(new ConfigHandler(component, this, category));
                moduleComponents.add(component);
                add(component, true);
            }
        }
    }

    @Override
    public void setFocusedComponent(HudElementComponent component) {
        super.setFocusedComponent(component);
        remove(component);
        add(0, component);
    }

    @Override
    public void onRender() {
        for (HudElementComponent component : this) {
            component.setVisible(component.isActive());
        }
        super.onRender();
    }

    @Override
    public void onRelocate() {
        setWidth(GuiUtils.getWidth());
        setHeight(GuiUtils.getHeight());
        super.onRelocate();
    }

    @Override
    public boolean onKeyPressed(int keyCode, char key) {
        return false;
    }

    @Override
    public boolean onKeyReleased(int keyCode) {
        return false;
    }

    @Override
    public void onClose() {
        module.setEnabled(false);
        removeAll(moduleComponents);
    }

}
