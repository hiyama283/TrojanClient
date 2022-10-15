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

package net.sushiclient.client.utils.player;

import net.minecraft.util.math.Vec3d;
import net.sushiclient.client.utils.world.BlockPlaceInfo;

import java.io.Closeable;

public class CloseablePositionOperator extends PositionOperator implements Closeable {

    @Override
    public void close() {
        PositionUtils.pop(this);
    }

    @Override
    public CloseablePositionOperator desyncMode(PositionMask positionMask) {
        super.desyncMode(positionMask);
        return this;
    }

    @Override
    public CloseablePositionOperator pos(double x, double y, double z) {
        super.pos(x, y, z);
        return this;
    }

    @Override
    public CloseablePositionOperator rotation(float yaw, float pitch) {
        super.rotation(yaw, pitch);
        return this;
    }

    @Override
    public CloseablePositionOperator move(double x, double y, double z, float yaw, float pitch, boolean onGround) {
        super.move(x, y, z, yaw, pitch, onGround);
        return this;
    }

    @Override
    public CloseablePositionOperator lookAt(Vec3d loc) {
        super.lookAt(loc);
        return this;
    }

    @Override
    public CloseablePositionOperator lookAt(BlockPlaceInfo info) {
        super.lookAt(info);
        return this;
    }
}
