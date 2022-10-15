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

package net.sushiclient.client;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

class ProfileConfig {

    @SerializedName("prefix")
    private char prefix = '%';
    @SerializedName("version")
    private int version = Sushi.getVersion();

    public char getPrefix() {
        return prefix;
    }

    public void setPrefix(char prefix) {
        this.prefix = prefix;
    }

    public int getVersion() {
        return version;
    }

    public void load(Gson gson, File file) {
        try {
            if (!file.exists()) return;
            String contents = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            ProfileConfig config = gson.fromJson(contents, ProfileConfig.class);
            this.prefix = config.prefix;
            this.version = config.version;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save(Gson gson, File file) {
        try {
            FileUtils.writeStringToFile(file, gson.toJson(this), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
