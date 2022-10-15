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

package net.sushiclient.client.account.requests;

import net.sushiclient.client.account.MojangAccount;
import net.sushiclient.client.account.responses.AuthResponse;
import net.sushiclient.client.account.responses.MinecraftProfile;
import net.sushiclient.client.account.responses.MojangUser;

public class RefreshResponse {
    private final String accessToken;
    private final String clientToken;
    private final MinecraftProfile selectedProfile;
    private final MojangUser user;

    public RefreshResponse(String accessToken, String clientToken, MinecraftProfile selectedProfile, MojangUser user) {
        this.accessToken = accessToken;
        this.clientToken = clientToken;
        this.selectedProfile = selectedProfile;
        this.user = user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getClientToken() {
        return clientToken;
    }

    public MinecraftProfile getSelectedProfile() {
        return selectedProfile;
    }

    public MojangUser getUser() {
        return user;
    }

    public void update(MojangAccount acc) {
        AuthResponse.update(acc, user, selectedProfile, accessToken, clientToken);
    }
}
