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

public class PositionOperator {

    private PositionMask positionMask = PositionMask.NONE;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private boolean onGround;

    public PositionOperator desyncMode(PositionMask positionMask) {
        this.positionMask = positionMask;
        return this;
    }

    public PositionOperator pos(double x, double y, double z) {
        setPos(x, y, z);
        return this;
    }

    public PositionOperator rotation(float yaw, float pitch) {
        setRotation(yaw, pitch);
        return this;
    }

    public PositionOperator move(double x, double y, double z, float yaw, float pitch, boolean onGround) {
        PositionUtils.move(x, y, z, yaw, pitch, onGround, positionMask, this);
        return this;
    }

    public PositionOperator lookAt(Vec3d loc) {
        PositionUtils.lookAt(loc, this);
        return this;
    }

    public PositionOperator lookAt(BlockPlaceInfo info) {
        PositionUtils.lookAt(info, this);
        return this;
    }

    public PositionMask getDesyncMode() {
        return positionMask;
    }

    public void setDesyncMode(PositionMask positionMask) {
        this.positionMask = positionMask;
    }

    public void setPos(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setRotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }
}
