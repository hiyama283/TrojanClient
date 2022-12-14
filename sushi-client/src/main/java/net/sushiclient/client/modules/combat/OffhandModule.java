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

package net.sushiclient.client.modules.combat;

import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.config.data.DoubleRange;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.input.ClickType;
import net.sushiclient.client.events.input.MousePressEvent;
import net.sushiclient.client.events.input.MouseReleaseEvent;
import net.sushiclient.client.events.player.PlayerUpdateEvent;
import net.sushiclient.client.events.tick.ClientTickEvent;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.utils.EntityInfo;
import net.sushiclient.client.utils.EntityUtils;
import net.sushiclient.client.utils.combat.DamageUtils;
import net.sushiclient.client.utils.player.InventoryType;
import net.sushiclient.client.utils.player.InventoryUtils;
import net.sushiclient.client.utils.player.ItemSlot;
import org.lwjgl.input.Mouse;

import java.awt.*;

public class OffhandModule extends BaseModule {

    private final Configuration<SwitchTarget> defaultItem;
    private final Configuration<DoubleRange> totemHelth;
    private final Configuration<Boolean> crystalCheck;
    private final Configuration<Boolean> swordGap;
    private final Configuration<Boolean> rightClickGap;
    //    private final Configuration<Boolean> fallCheck;
    private final Configuration<Boolean> totemOnElytra;
    private final Configuration<Boolean> preferInventory;
    private final Configuration<Boolean> switchPlayerIsNear;
    private final Configuration<DoubleRange> switchRange;
    private final Configuration<Boolean> playerUpdate;

    public OffhandModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        defaultItem = provider.get("default_item", "Default Item", null, SwitchTarget.class, SwitchTarget.TOTEM);
        totemHelth = provider.get("totem_health", "Totem Health", null, DoubleRange.class, new DoubleRange(5, 20, 0, 0.1, 1));
        crystalCheck = provider.get("crystal_check", "Crystal Check", null, Boolean.class, true);
        swordGap = provider.get("sword_gapple", "Sword gap", null, Boolean.class, false);
        rightClickGap = provider.get("right_click_gapple", "Right click gapple", null, Boolean.class, true);
//        fallCheck = provider.get("fall_check", "Fall Check", null, Boolean.class, true);
        totemOnElytra = provider.get("totem_on_elytra", "Totem On Elytra", null, Boolean.class, true);
        preferInventory = provider.get("prefer_inventory", "Prefer Inventory", null, Boolean.class, true);
        switchPlayerIsNear = provider.get("switch_player_is_near", "Switch player is near", null, Boolean.class, false);
        switchRange = provider.get("switch_range", "Switch range", null, DoubleRange.class,
                new DoubleRange(5, 10, 0.1, 0.1, 1), switchPlayerIsNear::getValue, false, 0);
        playerUpdate = provider.get("player_update", "Player update", null, Boolean.class, true);
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
    }

    private double getHealth() {
        return getPlayer().getHealth() + getPlayer().getAbsorptionAmount();
    }

    private double getCrystalDamage() {
        double max = 0;
        for (EntityInfo<EntityEnderCrystal> info : EntityUtils.getNearbyEntities(getPlayer().getPositionVector(), EntityEnderCrystal.class)) {
            double damage = DamageUtils.applyModifier(getPlayer(), DamageUtils.getCrystalDamage(getPlayer(), info.getEntity().getPositionVector()), DamageUtils.EXPLOSION);
            if (damage > max) max = damage;
        }
        return max;
    }

    private boolean isItemValid(Item target) {
        ItemSlot slot = InventoryUtils.findItemSlot(target, InventoryType.MAIN, InventoryType.HOTBAR, InventoryType.OFFHAND);
        return slot != null;
    }

    public SwitchTarget getSwitchTarget() {
        if (totemOnElytra.getValue() && getPlayer().isElytraFlying()) {
            return SwitchTarget.TOTEM;
        } else if (getHealth() < totemHelth.getValue().getCurrent()) {
            return SwitchTarget.TOTEM;
        } else if (crystalCheck.getValue() && getHealth() - getCrystalDamage()
                < totemHelth.getValue().getCurrent()) {
            return SwitchTarget.TOTEM;
        } else if (swordGap.getValue() && (ItemSlot.current().getItemStack().getItem()
                == Items.DIAMOND_SWORD) && isItemValid(SwitchTarget.GAPPLE.getItem())) {
            return SwitchTarget.GAPPLE;
        } else if (rightClickGap.getValue() && Mouse.isButtonDown(1) && ItemSlot.current().getItemStack()
                .getItem() == Items.DIAMOND_SWORD && isItemValid(SwitchTarget.GAPPLE.getItem())) {
            return SwitchTarget.GAPPLE;
        }


        if (defaultItem.getValue() == SwitchTarget.TOTEM) return SwitchTarget.TOTEM;
        if (switchPlayerIsNear.getValue() && EntityUtils.getNearbyPlayers(
                switchRange.getValue().getCurrent()).size() == 0) return SwitchTarget.TOTEM;

        if (isItemValid(defaultItem.getValue().getItem()))
            return defaultItem.getValue();
        else
            return SwitchTarget.TOTEM;
    }

    public SwitchTarget getCurrent() {
        Item item = ItemSlot.offhand().getItemStack().getItem();
        for (SwitchTarget switchTarget : SwitchTarget.values()) {
            if (switchTarget.getItem().equals(item)) return switchTarget;
        }
        return null;
    }

    @EventHandler(timing = EventTiming.POST)
    public void onClientTick(ClientTickEvent e) {
        if (playerUpdate.getValue()) return;
        switching();
    }

    @EventHandler(timing = EventTiming.POST)
    public void onPlayerUpdate(PlayerUpdateEvent e) {
        if (!playerUpdate.getValue()) return;
        switching();
    }

    private void switching() {
        SwitchTarget target = getSwitchTarget();
        SwitchTarget current = getCurrent();
        if (target == current) return;
        ItemSlot itemSlot = null;
        if (preferInventory.getValue()) {
            itemSlot = InventoryUtils.findItemSlot(target.getItem(), InventoryType.MAIN);
        }
        if (itemSlot == null) {
            itemSlot = InventoryUtils.findItemSlot(target.getItem(), InventoryType.values());
        }
        if (itemSlot == null) {
            return;
        }
        InventoryUtils.moveTo(itemSlot, ItemSlot.offhand());
    }

    @Override
    public String getDefaultName() {
        return "Offhand";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.COMBAT;
    }

}
