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

public class MojangUser {
    private String id;
    private String email;
    private String username;
    private String registerIp;
    private String migratedFrom;
    private long migratedAt;
    private long registeredAt;
    private long passwordChangedAt;
    private long dateOfBirth;
    private boolean suspended;
    private boolean blocked;
    private boolean secured;
    private boolean migrated;
    private boolean emailVerified;
    private boolean legacyUser;
    private boolean verifiedByParent;

    public MojangUser() {
    }

    public MojangUser(String id, String email, String username, String registerIp, String migratedFrom, long migratedAt, long registeredAt, long passwordChangedAt, long dateOfBirth, boolean suspended, boolean blocked, boolean secured, boolean migrated, boolean emailVerified, boolean legacyUser, boolean verifiedByParent) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.registerIp = registerIp;
        this.migratedFrom = migratedFrom;
        this.migratedAt = migratedAt;
        this.registeredAt = registeredAt;
        this.passwordChangedAt = passwordChangedAt;
        this.dateOfBirth = dateOfBirth;
        this.suspended = suspended;
        this.blocked = blocked;
        this.secured = secured;
        this.migrated = migrated;
        this.emailVerified = emailVerified;
        this.legacyUser = legacyUser;
        this.verifiedByParent = verifiedByParent;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getRegisterIp() {
        return registerIp;
    }

    public String getMigratedFrom() {
        return migratedFrom;
    }

    public long getMigratedAt() {
        return migratedAt;
    }

    public long getRegisteredAt() {
        return registeredAt;
    }

    public long getPasswordChangedAt() {
        return passwordChangedAt;
    }

    public long getDateOfBirth() {
        return dateOfBirth;
    }

    public boolean isSuspended() {
        return suspended;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public boolean isSecured() {
        return secured;
    }

    public boolean isMigrated() {
        return migrated;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public boolean isLegacyUser() {
        return legacyUser;
    }

    public boolean isVerifiedByParent() {
        return verifiedByParent;
    }
}
