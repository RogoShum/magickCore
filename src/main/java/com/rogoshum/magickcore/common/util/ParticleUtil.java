package com.rogoshum.magickcore.common.util;

import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.enums.ParticleType;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.common.magick.MagickElement;
import com.rogoshum.magickcore.common.network.Networking;
import com.rogoshum.magickcore.common.network.ParticleSamplePack;
import com.rogoshum.magickcore.common.network.SimpleChannel;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.PI;

public class ParticleUtil {
    public static List<Vec3> drawSector(Vec3 center, Vec3 direction, double degrees, int frequency) {
        if(frequency < 2)
            frequency = 2;

        degrees = degrees/frequency;
        List<Vec3> vectors = new ArrayList<>();
        for (int i = 1; i <= frequency; ++i) {
            List<Vec3> part = new ArrayList<>(drawSectorPart(center, direction, degrees * i, i));
            vectors.addAll(part);
        }
        return vectors;
    }

    public static List<Vec3> drawSectorPart(Vec3 center, Vec3 direction, double degrees, int frequency) {
        List<Vec3> vectors = new ArrayList<>();

        degrees = Mth.abs((float) degrees);

        if(degrees >= 90) {
            direction = direction.scale(-1);
            degrees = 179 - degrees;
            if(degrees < 0) {
                Vec3 vec = center.add(direction);
                List<Vec3> list = new ArrayList<>();
                list.add(vec);
                return list;
            }
        }
        if(frequency < 3)
            frequency = 3;
        double length = direction.length();
        Vec3 forward = center.add(direction);
        Vec2 pitchYaw = getRotationForVector(direction);
        float part = 360f / frequency;
        for (int i = 1; i < frequency; ++i) {
            float ratio = i * part * ((float)Math.PI / 180F);
            Vec3 pitchTransform = getVectorForRotation(pitchYaw.x, pitchYaw.y).scale(Mth.cos(ratio));
            Vec3 yawTransform = getVectorForRotation(0, pitchYaw.y-90).scale(Mth.sin(ratio));
            Vec3 rotate = pitchTransform.add(yawTransform).normalize();
            rotate = rotate.scale(Math.tan(degrees * Math.PI / 180F) * length);

            vectors.add(forward.add(rotate).subtract(center).normalize().scale(length).add(center));//
        }
        return vectors;
    }

    public static Vec3[] drawCone(Vec3 center, Vec3 direction, double degrees, int frequency) {
        degrees = Mth.abs((float) degrees);
        if(degrees >= 90) {
            direction = direction.scale(-1);
            degrees = 179 - degrees;
            if(degrees < 0) {
                return new Vec3[]{center.add(direction)};
            }
        }
        if(frequency < 2)
            frequency = 2;
        double length = direction.length();
        Vec3 forward = center.add(direction);
        Vec3[] vector = new Vec3[frequency];
        float part = 360f / frequency;
        Vec2 pitchYaw = getRotationForVector(direction);
        for (int i = 0; i < frequency; ++i) {
            float ratio = i * part * ((float)Math.PI / 180F);
            Vec3 pitchTransform = getVectorForRotation(pitchYaw.x-90, pitchYaw.y).scale(Mth.cos(ratio));
            Vec3 yawTransform = getVectorForRotation(0, pitchYaw.y-90).scale(Mth.sin(ratio));
            Vec3 rotate = pitchTransform.add(yawTransform).normalize();
            rotate = rotate.scale(Math.tan(degrees * Math.PI / 180F) * length);

            vector[i] = forward.add(rotate).subtract(center).normalize().scale(length).add(center);//
        }
        return vector;
    }

    public static Vec3[] drawCircle(Vec3 center, Vec3 direction, double degrees, int frequency) {
        degrees = Mth.abs((float) degrees);
        if(degrees >= 90) {
            direction = direction.scale(-1);
            degrees = 179 - degrees;
            if(degrees < 0) {
                return new Vec3[]{center.add(direction)};
            }
        }
        if(frequency < 3)
            frequency = 3;
        double length = direction.length();
        Vec3 forward = center.add(direction);
        Vec3[] vector = new Vec3[frequency];
        float part = 360f / frequency;
        Vec2 pitchYaw = getRotationForVector(direction);
        for (int i = 0; i < frequency; ++i) {
            float ratio = i * part * ((float)Math.PI / 180F);
            Vec3 pitchTransform = getVectorForRotation(pitchYaw.x-90, pitchYaw.y).scale(Mth.cos(ratio));
            Vec3 yawTransform = getVectorForRotation(0, pitchYaw.y-90).scale(Mth.sin(ratio));
            Vec3 rotate = pitchTransform.add(yawTransform).normalize();
            rotate = rotate.scale(Math.tan(degrees * Math.PI / 180F) * length);

            vector[i] = forward.add(rotate).subtract(center).normalize().scale(length).add(center);//
        }
        return vector;
    }

    public static List<Vec3> drawRectangle(Vec3 center, float space, double length, double width, double height) {
        Vec3 copy = center;
        Vec3 axisMin = copy.subtract(length * 0.5, width * 0.5, height * 0.5);
        List<Vec3> list = new ArrayList<>();
        for (double x = 0; x <= length; x+=space) {
            for (double y = 0; y <= height; y+=space) {
                for (double z = 0; z <= width; z+=space) {
                    boolean xPass = (x == 0 || x + space > length);
                    boolean yPass = (y == 0 || y + space > height);
                    boolean zPass = (z == 0 || z + space > width);
                    if((xPass && yPass) || (zPass && yPass) || (xPass && zPass)) {
                        list.add(axisMin.add(x, y, z));
                    }
                }
            }
        }
        return list;
    }

    public static Vec3 rotateVector(Vector3f axis, float angle, Vec3 direction) {
        Quaternion quaternion = axis.rotationDegrees(angle);
        double d = -quaternion.i() * direction.x - quaternion.j() * direction.y - quaternion.k() * direction.z;
        double d1 = quaternion.r() * direction.x + quaternion.j() * direction.z - quaternion.k() * direction.y;
        double d2 = quaternion.r() * direction.y - quaternion.i() * direction.z + quaternion.k() * direction.x;
        double d3 = quaternion.r() * direction.z + quaternion.i() * direction.y - quaternion.j() * direction.x;
        double x = d1 * quaternion.r() - d * quaternion.i() - d2 * quaternion.k() + d3 * quaternion.j();
        double y = d2 * quaternion.r() - d * quaternion.j() + d1 * quaternion.k() - d3 * quaternion.i();
        double z = d3 * quaternion.r() - d * quaternion.k() - d1 * quaternion.j() + d2 * quaternion.i();
        return new Vec3(x, y, z);
    }

    public static Vec2 getRotationForVector(Vec3 vector3d) {
        float yaw = (float) (Mth.atan2(vector3d.z, vector3d.x) * 180 / PI);
        yaw-=90;
        if (yaw < 0)
            yaw += 360;

        float tmp = Mth.sqrt (vector3d.x * vector3d.x + vector3d.z * vector3d.z);
        float pitch = (float) (Mth.atan2(-vector3d.y, tmp) * 180 / PI);
        if (pitch < 0)
            pitch += 360;

        if(vector3d.x == 0 && vector3d.z == 0){
            if (vector3d.y > 0)
                pitch = 270;
            else
                pitch = 90;
        }
        return new Vec2(pitch, yaw);
    }

    public static Vec3 getVectorForRotation(float pitch, float yaw) {
        float f = pitch * ((float) PI / 180F);
        float f1 = -yaw * ((float) PI / 180F);
        float f2 = Mth.cos(f1);
        float f3 = Mth.sin(f1);
        float f4 = Mth.cos(f);
        float f5 = Mth.sin(f);
        return new Vec3((double)(f3 * f4), (double)(-f5), (double)(f2 * f4));
    }

    public static Vec3 drawLine(Vec3 start, Vec3 end, double factor) {
        double tx = start.x() + (end.x() - start.x()) * factor + MagickCore.rand.nextGaussian() * 0.005;
        double ty = start.y() + (end.y() - start.y()) * factor + MagickCore.rand.nextGaussian() * 0.005;
        double tz = start.z() + (end.z() - start.z()) * factor + MagickCore.rand.nextGaussian() * 0.005;
        return new Vec3(tx, ty, tz);
    }

    public static Vec3 drawParabola(Vec3 start, Vec3 end, double factor, double height, Direction direction) {
        return drawParabola(start, end, factor, height, new Vec3(direction.step()));
    }

    public static Vec3 drawParabola(Vec3 start, Vec3 end, double factor, double height, Vec3 direction) {
        direction = direction.normalize();
        double tx = start.x() + (end.x() - start.x()) * factor + MagickCore.rand.nextGaussian() * 0.005;
        double ty = start.y() + (end.y() - start.y()) * factor + MagickCore.rand.nextGaussian() * 0.005;
        double tz = start.z() + (end.z() - start.z()) * factor + MagickCore.rand.nextGaussian() * 0.005;
        factor = 1 - factor * 2;
        double y = factor * factor * height;
        return new Vec3(tx, ty, tz).add(direction.scale(-y)).add(direction.scale(height));
    }

    public static void spawnBlastParticle(Level world, Vec3 center, float force, MagickElement element, ParticleType type) {
        float count = (10 * force);
        float scale = Math.max(0.1f, 0.05f * force);
        if(!world.isClientSide) {
            ParticleSamplePack pack = new ParticleSamplePack(0, type, center, force, (byte) 0, element.type(), net.minecraft.world.phys.Vec3.ZERO);
            Networking.INSTANCE.send(
                    SimpleChannel.SendType.server(PlayerLookup.tracking((ServerLevel) world, new BlockPos(center)))
                    , pack);
        } else {
            ResourceLocation res = ParticleType.getResourceLocation(type, element);
            for (int i = 0; i < count; ++i) {
                double randX = MagickCore.getNegativeToOne() * 0.01 * force;
                double randY = MagickCore.getNegativeToOne() * 0.01 * force;
                double randZ = MagickCore.getNegativeToOne() * 0.01 * force;
                LitParticle par = new LitParticle(world, res
                        , new Vec3(center.x, center.y, center.z), scale, scale, 1.0f, 30, element.getRenderer());
                par.setParticleGravity(0);
                par.setLimitScale();
                par.setGlow();
                par.addMotion(randX * force, randY * force, randZ * force);
                MagickCore.addMagickParticle(par);
            }
        }
    }

    public static void spawnImpactParticle(Level world, Vec3 center, float force, Vec3 motion, MagickElement element, ParticleType type) {
        float count = (10 * force);
        float scale = Math.max(0.1f, 0.05f * force);
        if(!world.isClientSide) {
            ParticleSamplePack pack = new ParticleSamplePack(0, type, center, force, (byte) 1, element.type(), motion);
            Networking.INSTANCE.send(SimpleChannel.SendType.server(PlayerLookup.tracking((ServerLevel) world, new BlockPos(center))), pack);
        } else {
            ResourceLocation res = ParticleType.getResourceLocation(type, element);
            for (int i = 0; i < count; ++i) {
                double randX = MagickCore.getNegativeToOne() * 0.05;
                double randY = MagickCore.getNegativeToOne() * 0.05;
                double randZ = MagickCore.getNegativeToOne() * 0.05;
                LitParticle par = new LitParticle(world, res
                        , new Vec3(center.x, center.y, center.z), scale, scale, 1.0f, 20, element.getRenderer());
                par.setParticleGravity(0);
                par.setLimitScale();
                par.setGlow();
                par.addMotion(motion.x + randX * force, motion.y + randY * force, motion.z + randZ * force);
                MagickCore.addMagickParticle(par);
            }
        }
    }

    public static void spawnRaiseParticle(Level world, Vec3 center, float force, MagickElement element, ParticleType type) {
        float count = (10 * force);
        float scale = 1f;
        if(!world.isClientSide) {
            ParticleSamplePack pack = new ParticleSamplePack(0, type, center, force, (byte) 2, element.type(), Vec3.ZERO);
            Networking.INSTANCE.send(SimpleChannel.SendType.server(PlayerLookup.tracking((ServerLevel) world, new BlockPos(center))), pack);
        } else {
            ResourceLocation res = ParticleType.getResourceLocation(type, element);
            for (int i = 0; i < count * 10; ++i) {
                LitParticle par = new LitParticle(world, res
                        , new Vec3(Mth.sin(MagickCore.getNegativeToOne() * 0.3f) + center.x
                        , center.y + 0.2
                        , Mth.sin(MagickCore.getNegativeToOne() * 0.3f) + center.z)
                        , scale * 0.2f, scale * 2f, 0.5f, Math.max((int) (40 * MagickCore.rand.nextFloat()), 20), element.getRenderer());
                par.setGlow();
                par.setParticleGravity(-0.1f);
                par.setColor(Color.BLUE_COLOR);
                MagickCore.addMagickParticle(par);
            }
        }
    }
}
