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

import net.sushiclient.client.account.Agent;

public class AuthRequest {
    private final String captcha;
    private final String captchaSupported;
    private final String username;
    private final String password;
    private final Agent agent;
    private final boolean requestUser;

    public AuthRequest(String captcha, String captchaSupported, String username, String password, Agent agent, boolean requestUser) {
        this.captcha = captcha;
        this.captchaSupported = captchaSupported;
        this.username = username;
        this.password = password;
        this.agent = agent;
        this.requestUser = requestUser;
    }

    public String getCaptcha() {
        return captcha;
    }

    public String getCaptchaSupported() {
        return captchaSupported;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Agent getAgent() {
        return agent;
    }

    public boolean isRequestUser() {
        return requestUser;
    }
}
