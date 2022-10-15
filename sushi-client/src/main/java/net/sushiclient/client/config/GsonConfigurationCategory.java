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

package net.sushiclient.client.config;

public class GsonConfigurationCategory extends GsonConfigurations implements ConfigurationCategory {

    private final GsonRootConfigurations root;
    private final String id;
    private final String name;
    private final String description;

    public GsonConfigurationCategory(GsonRootConfigurations root, String id, String name, String description) {
        this.root = root;
        this.id = id;
        this.name = name;
        this.description = description;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    protected ConfigurationCategory getConfigurationCategory() {
        return this;
    }

    @Override
    protected GsonRootConfigurations getRoot() {
        return root;
    }
}
