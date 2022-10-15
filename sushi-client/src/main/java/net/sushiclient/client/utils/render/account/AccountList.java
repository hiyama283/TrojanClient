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
import net.minecraft.client.gui.GuiListExtended;
import net.sushiclient.client.account.MojangAccount;
import net.sushiclient.client.account.MojangAccounts;

import java.util.ArrayList;

public class AccountList extends GuiListExtended {

    public static final MojangAccount EMPTY_ENTRY = new MojangAccount(null, null, null, null, null, null);
    private final GuiAccounts owner;
    private final ArrayList<AccountEntry> entries = new ArrayList<>();
    private final AccountEmptyEntry accountEmptyEntry = new AccountEmptyEntry(mc);

    public AccountList(GuiAccounts owner, Minecraft mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn) {
        super(mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
        this.owner = owner;
    }

    public void refresh(MojangAccounts accounts) {
        entries.clear();
        for (MojangAccount account : accounts.getAll()) {
            entries.add(new AccountEntry(owner, account));
        }
    }

    @Override
    public IGuiListEntry getListEntry(int index) {
        if (entries.isEmpty() && index == 0) {
            return accountEmptyEntry;
        } else {
            return entries.get(index);
        }
    }

    public int indexOf(IGuiListEntry entry) {
        if (entry instanceof AccountEntry) {
            return entries.indexOf(entry);
        } else {
            return -1;
        }
    }

    @Override
    protected int getSize() {
        return Math.max(entries.size(), 1);
    }
}
