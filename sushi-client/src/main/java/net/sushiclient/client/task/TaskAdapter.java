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

package net.sushiclient.client.task;

abstract public class TaskAdapter<I, R> implements Tickable {
    private boolean running;
    private I input;
    private R result;

    public void start(I input) throws Exception {
        if (running) throw new IllegalStateException("This task has already started");
        this.running = true;
        this.input = input;
    }

    public void stop(R result) {
        if (!running) throw new IllegalStateException("This task has already finished");
        this.running = false;
        this.result = result;
    }

    public boolean isRunning() {
        return running;
    }

    public I getInput() {
        return input;
    }

    public R getResult() {
        return result;
    }
}
