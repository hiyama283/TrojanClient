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

package net.sushiclient.client.utils.render.account;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiListExtended;
import net.sushiclient.client.account.MojangAccount;
import net.sushiclient.client.utils.render.GuiUtils;

import java.awt.*;

public class AccountEntry implements GuiListExtended.IGuiListEntry {

    private final Minecraft mc;
    private final FontRenderer fr;
    private final GuiAccounts owner;
    private final MojangAccount account;

    public AccountEntry(GuiAccounts owner, MojangAccount account) {
        this.mc = owner.mc;
        this.fr = mc.fontRenderer;
        this.owner = owner;
        this.account = account;
    }

    public MojangAccount getAccount() {
        return account;
    }

    @Override
    public void updatePosition(int slotIndex, int x, int y, float partialTicks) {
    }

    @Override
    public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
        if (equals(owner.getSelected())) {
            GuiUtils.drawOutline(x, y, listWidth, slotHeight, Color.GRAY, 1);
        }
        fr.drawString(account.getEmail(), x + 4, y + 4, 16777215);
        if (account.getName() != null) {
            fr.drawString(account.getName(), x + 4, y + 14, 16777215);
        }
    }

    @Override
    public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {
        owner.setSelected(this);
        return true;
    }

    @Override
    public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
    }
}
