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

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GsonRootConfigurations extends GsonConfigurations implements RootConfigurations {

    private final Gson gson;
    private final ArrayList<ConfigurationCategory> categories = new ArrayList<>();
    private final HashMap<String, Object> defaults = new HashMap<>();
    private final HashMap<String, Object> objects = new HashMap<>();
    private JsonObject root;

    public GsonRootConfigurations(Gson gson) {
        this.gson = gson;
    }

    public void load(JsonObject object) {
        this.root = object;
    }

    public JsonObject save() {
        getAll().forEach(it -> ((GsonConfiguration<?>) it).save());
        for (Map.Entry<String, Object> entry : objects.entrySet()) {
            setRawValue(root, entry.getKey(), entry.getValue(), true);
        }

        for (Map.Entry<String, Object> entry : defaults.entrySet()) {
            setRawValue(root, entry.getKey(), entry.getValue(), false);
        }
        return root;
    }

    private void setRawValue(JsonObject obj, String key, Object o, boolean override) {
        if (key.contains(".")) {
            String child = key.split("\\.")[0];
            JsonElement childObj = obj.get(child);
            if (childObj == null || !childObj.isJsonObject()) {
                childObj = new JsonObject();
                obj.add(child, childObj);
            }
            setRawValue(childObj.getAsJsonObject(), key.replaceFirst(child + "\\.", ""), o, override);
        } else if (obj.get(key) == null || override) {
            obj.add(key, gson.toJsonTree(o));
        }
    }

    private <T> T getRawValue(JsonObject object, String id, Class<T> tClass) {
        try {
            if (id.contains(".")) {
                String key = id.split("\\.")[0];
                JsonElement element = object.get(key);
                if (element != null && element.isJsonObject()) {
                    T rawValue = getRawValue(element.getAsJsonObject(), id.replaceFirst(key + "\\.", ""), tClass);
                    if (rawValue != null) return rawValue;
                }
            } else {
                JsonElement element = object.get(id);
                if (element != null) {
                    return gson.fromJson(object.get(id), tClass);
                }
            }
        } catch (JsonParseException e) {
            // use default
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    protected <T> T getRawValue(String id, Class<T> tClass) {
        Object result = objects.get(id);
        if (result != null && tClass.isAssignableFrom(result.getClass())) return (T) result;

        T rawValue = getRawValue(root, id, tClass);
        if (rawValue != null) {
            objects.put(id, rawValue);
            return rawValue;
        }

        result = defaults.get(id);
        if (result != null && tClass.isAssignableFrom(result.getClass())) return (T) result;
        else return null;
    }

    protected void setRawValue(String id, Object o) {
        objects.put(id, o);
    }

    protected void putDefault(String id, Object obj) {
        defaults.put(id, obj);
    }

    @Override
    public List<ConfigurationCategory> getCategories() {
        return categories;
    }

    @Override
    public List<Configuration<?>> getAll(boolean includeCategorized) {
        if (!includeCategorized) return super.getAll();
        ArrayList<Configuration<?>> result = new ArrayList<>(super.getAll());
        for (ConfigurationCategory category : getCategories()) {
            result.addAll(category.getAll());
        }
        return result;
    }

    @Override
    public List<Configuration<?>> getAll() {
        return getAll(true);
    }

    @Override
    public ConfigurationCategory getCategory(String id, String name, String description) {
        GsonConfigurationCategory category = new GsonConfigurationCategory(this, id, name, description);
        categories.add(category);
        getHandlers().forEach(it -> {
            if (it instanceof RootConfigurationsHandler) {
                ((RootConfigurationsHandler) it).getCategory(category);
            }
        });
        return category;
    }

    @Override
    protected ConfigurationCategory getConfigurationCategory() {
        return null;
    }

    @Override
    protected GsonRootConfigurations getRoot() {
        return this;
    }
}
