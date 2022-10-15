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

package net.sushiclient.client.account;

import net.sushiclient.client.account.requests.RefreshResponse;
import net.sushiclient.client.account.responses.AuthResponse;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class PlainMojangAccounts implements MojangAccounts {

    private final ArrayList<MojangAccount> accounts = new ArrayList<>();
    private final Map<MojangAccount, AccountStatus> statuses = new WeakHashMap<>();
    private final MojangAPI mojangApi;
    private final File file;

    public PlainMojangAccounts(File file) {
        this(new SecureMojangAPI(), file);
    }

    public PlainMojangAccounts(MojangAPI mojangApi, File file) {
        this.mojangApi = mojangApi;
        this.file = file;
    }

    @Override
    public synchronized List<MojangAccount> getAll() {
        return new ArrayList<>(accounts);
    }

    @Override
    public synchronized boolean auth(MojangAccount acc) {
        try {
            AuthResponse response = mojangApi.auth(acc);
            response.update(acc);
            statuses.put(acc, AccountStatus.VALID);
            return true;
        } catch (IOException e) {
            statuses.put(acc, AccountStatus.INVALID);
            return false;
        }
    }

    @Override
    public synchronized void add(MojangAccount acc) {
        accounts.add(acc);
    }

    @Override
    public boolean remove(MojangAccount acc) {
        return accounts.remove(acc);
    }

    @Override
    public synchronized void refreshAll() {
        for (MojangAccount acc : accounts) {
            try {
                RefreshResponse response = mojangApi.refresh(acc);
                response.update(acc);
                statuses.put(acc, AccountStatus.VALID);
            } catch (IOException e) {
                statuses.put(acc, AccountStatus.INVALID);
            }
        }
    }

    @Override
    public synchronized void load() {
        accounts.clear();
        if (!file.isFile()) return;
        try {
            String[] lines = FileUtils.readFileToString(file, StandardCharsets.UTF_8).split("\n");
            for (String line : lines) {
                MojangAccount account = deserialize(line);
                if (account != null) {
                    accounts.add(account);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void save() {
        String contents = String.join("\n",
                accounts.stream()
                        .map(this::serialize)
                        .toArray(String[]::new));
        try {
            FileUtils.writeStringToFile(file, contents, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized AccountStatus getAccountStatus(MojangAccount acc) {
        return statuses.getOrDefault(acc, AccountStatus.UNKNOWN);
    }

    protected String serialize(MojangAccount acc) {
        return acc.getEmail() + ":" + acc.getPassword() + ":" + acc.getId() + ":" + acc.getName() + ":" + acc.getAccessToken() + ":" + acc.getClientToken();
    }

    protected MojangAccount deserialize(String line) {
        String[] split = line.split(":");
        if (split.length == 2) {
            return new MojangAccount(split[0], split[1]);
        } else if (split.length == 6) {
            return new MojangAccount(split[0], split[2], split[3], split[1], split[4], split[5]);
        } else {
            return null;
        }
    }
}
