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
import net.sushiclient.client.task.TaskChain;

class ForgeTaskChain<I> implements TaskChain<I> {

    private final TaskExecutor taskExecutor;
    private final TaskAdapter<?, ?> parent;

    ForgeTaskChain(TaskExecutor taskExecutor, TaskAdapter<?, ?> parent) {
        this.taskExecutor = taskExecutor;
        this.parent = parent;
    }

    @Override
    public <R> TaskChain<R> then(TaskAdapter<? super I, R> task) {
        getTaskExecutor().next(getParent(), task);
        return new ForgeTaskChain<>(getTaskExecutor(), task);
    }

    @Override
    public <R> TaskChain<R> fail(TaskAdapter<? super Exception, R> task) {
        getTaskExecutor().fail(getParent(), task);
        return new ForgeTaskChain<>(getTaskExecutor(), task);
    }

    @Override
    public TaskChain<I> last(TaskAdapter<? super I, I> task) {
        getTaskExecutor().last(task);
        return new ForgeTaskChain<>(getTaskExecutor(), task);
    }

    @Override
    public TaskChain<I> abortIf(TaskAdapter<? super I, Boolean> task) {
        getTaskExecutor().abort(getParent(), task);
        return new ForgeTaskChain<>(getTaskExecutor(), task);
    }

    @Override
    public void execute() {
        getTaskExecutor().execute();
    }

    public TaskExecutor getTaskExecutor() {
        return taskExecutor;
    }

    public TaskAdapter<?, ?> getParent() {
        return parent;
    }
}
