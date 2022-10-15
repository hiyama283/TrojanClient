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
import net.sushiclient.client.account.responses.BriefProfile;
import net.sushiclient.client.account.responses.NameHistory;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;

public interface MojangAPI {

    AuthResponse auth(MojangAccount acc) throws IOException;

    boolean validate(MojangAccount acc) throws IOException;

    RefreshResponse refresh(MojangAccount acc) throws IOException;

    boolean signout(MojangAccount acc) throws IOException;

    boolean invalidate(MojangAccount acc) throws IOException;

    boolean setName(MojangAccount acc, String name) throws IOException;

    boolean setPassword(MojangAccount acc, String password) throws IOException;

    Collection<NameHistory> getNameHistory(MojangAccount acc) throws IOException;

    BriefProfile getProfileAt(MojangAccount acc, Date date) throws IOException;

}
