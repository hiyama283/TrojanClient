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

package net.sushiclient.client.task.forge;

import net.sushiclient.client.task.TaskAdapter;

class TaskContext {
    private final TaskAdapter<?, ?> origin;
    private TaskAdapter<?, ?> next;
    private TaskAdapter<? super Exception, ?> fail;

    public TaskContext(TaskAdapter<?, ?> origin) {
        this.origin = origin;
    }

    public void next(TaskAdapter<?, ?> adapter) {
        next = adapter;
    }

    public void fail(TaskAdapter<? super Exception, ?> adapter) {
        fail = adapter;
    }

    public TaskAdapter<?, ?> origin() {
        return origin;
    }

    public TaskAdapter<?, ?> next() {
        return next;
    }

    public TaskAdapter<? super Exception, ?> fail() {
        return fail;
    }
}
