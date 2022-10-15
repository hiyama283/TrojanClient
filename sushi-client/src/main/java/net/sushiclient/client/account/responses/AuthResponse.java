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

package net.sushiclient.client.account.responses;

import net.sushiclient.client.account.MojangAccount;

import java.util.List;

public class AuthResponse {
    private String accessToken;
    private String clientToken;
    private List<MinecraftProfile> availableProfiles;
    private MinecraftProfile selectedProfile;
    private MojangUser user;

    public AuthResponse() {
    }

    public AuthResponse(String accessToken, String clientToken, List<MinecraftProfile> availableProfiles, MinecraftProfile selectedProfile, MojangUser user) {
        this.accessToken = accessToken;
        this.clientToken = clientToken;
        this.availableProfiles = availableProfiles;
        this.selectedProfile = selectedProfile;
        this.user = user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getClientToken() {
        return clientToken;
    }

    public List<MinecraftProfile> getAvailableProfiles() {
        return availableProfiles;
    }

    public MinecraftProfile getSelectedProfile() {
        return selectedProfile;
    }

    public MojangUser getUser() {
        return user;
    }

    public void update(MojangAccount acc) {
        update(acc, user, selectedProfile, accessToken, clientToken);
    }

    public static void update(MojangAccount target, MojangUser user, MinecraftProfile profile, String accessToken, String clientToken) {
        if (user != null && user.getEmail() != null) {
            target.setEmail(user.getEmail());
        }
        if (profile != null) {
            if (profile.getId() != null) target.setId(profile.getId());
            if (profile.getName() != null) target.setName(profile.getName());
        }
        if (accessToken != null) target.setAccessToken(accessToken);
        if (clientToken != null) target.setClientToken(clientToken);
    }
}
