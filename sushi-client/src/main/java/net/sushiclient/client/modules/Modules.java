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

package net.sushiclient.client.modules;

import java.util.ArrayList;
import java.util.List;

public interface Modules {

    default List<Module> getModules(Category category) {
        ArrayList<Module> result = new ArrayList<>();
        for (Module module : getAll()) {
            if (module.getCategory().equals(category))
                result.add(module);
        }
        return result;
    }

    Module getModule(String id);

    ModuleFactory getModuleFactory(String id);

    List<Module> getAll();

    Module addModule(String id, ModuleFactory factory);

    Module cloneModule(String id, String newId);

    List<Module> restoreAll();

    Module restore(String id);

    void removeModule(String id);

    void save();

    void load();

    void enable();

    void disable();

    void addHandler(ModulesHandler handler);

    boolean removeHandler(ModulesHandler handler);

    List<ModulesHandler> getHandlers();
}
