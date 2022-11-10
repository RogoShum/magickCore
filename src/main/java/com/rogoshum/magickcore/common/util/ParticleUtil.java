package com.rogoshum.magickcore.common.util;

import com.rogoshum.magickcore.MagickCore;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.PI;

public class ParticleUtil {
    public static List<Vector3d> drawSector(Vector3d center, Vector3d direction, double degrees, int frequency) {
        if(frequency < 2)
            frequency = 2;

        degrees = degrees/frequency;
        List<Vector3d> vectors = new ArrayList<>();
        for (int i = 1; i <= frequency; ++i) {
            List<Vector3d> part = new ArrayList<>(drawSectorPart(center, direction, degrees * i, i));
            vectors.addAll(part);
        }
        return vectors;
    }

    public static List<Vector3d> drawSectorPart(Vector3d center, Vector3d direction, double degrees, int frequency) {
        List<Vector3d> vectors = new ArrayList<>();

        degrees = MathHelper.abs((float) degrees);

        if(degrees >= 90) {
            direction = direction.scale(-1);
            degrees = 179 - degrees;
            if(degrees < 0) {
                Vector3d vec = center.add(direction);
                List<Vector3d> list = new ArrayList<>();
                list.add(vec);
                return list;
            }
        }
        if(frequency < 3)
            frequency = 3;
        double length = direction.length();
        Vector3d forward = center.add(direction);
        Vector2f pitchYaw = getRotationForVector(direction);
        float part = 360f / frequency;
        for (int i = 1; i < frequency; ++i) {
            float ratio = i * part * ((float)Math.PI / 180F);
            Vector3d pitchTransform = getVectorForRotation(pitchYaw.x, pitchYaw.y).scale(MathHelper.cos(ratio));
            Vector3d yawTransform = getVectorForRotation(0, pitchYaw.y-90).scale(MathHelper.sin(ratio));
            Vector3d rotate = pitchTransform.add(yawTransform).normalize();
            rotate = rotate.scale(Math.tan(degrees * Math.PI / 180F) * length);

            vectors.add(forward.add(rotate).subtract(center).normalize().scale(length).add(center));//
        }
        return vectors;
    }
    public static Vector3d[] drawCone(Vector3d center, Vector3d direction, double degrees, int frequency) {
        degrees = MathHelper.abs((float) degrees);
        if(degrees >= 90) {
            direction = direction.scale(-1);
            degrees = 179 - degrees;
            if(degrees < 0) {
                return new Vector3d[]{center.add(direction)};
            }
        }
        if(frequency < 3)
            frequency = 3;
        double length = direction.length();
        Vector3d forward = center.add(direction);
        Vector3d[] vector = new Vector3d[frequency];
        float part = 360f / frequency;
        Vector2f pitchYaw = getRotationForVector(direction);
        for (int i = 0; i < frequency; ++i) {
            float ratio = i * part * ((float)Math.PI / 180F);
            Vector3d pitchTransform = getVectorForRotation(pitchYaw.x-90, pitchYaw.y).scale(MathHelper.cos(ratio));
            Vector3d yawTransform = getVectorForRotation(0, pitchYaw.y-90).scale(MathHelper.sin(ratio));
            Vector3d rotate = pitchTransform.add(yawTransform).normalize();
            rotate = rotate.scale(Math.tan(degrees * Math.PI / 180F) * length);

            vector[i] = forward.add(rotate).subtract(center).normalize().scale(length).add(center);//
        }
        return vector;
    }

    public static List<Vector3d> drawRectangle(Vector3d center, float space, double length, double width, double height) {
        net.minecraft.util.math.vector.Vector3d copy = center;
        Vector3d axisMin = copy.subtract(length / 2, width / 2, height / 2);
        List<Vector3d> list = new ArrayList<>();
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

    public static Vector3d rotateVector(Vector3f axis, float angle, Vector3d direction) {
        Quaternion quaternion = axis.rotationDegrees(angle);
        double d = -quaternion.getX() * direction.x - quaternion.getY() * direction.y - quaternion.getZ() * direction.z;
        double d1 = quaternion.getW() * direction.x + quaternion.getY() * direction.z - quaternion.getZ() * direction.y;
        double d2 = quaternion.getW() * direction.y - quaternion.getX() * direction.z + quaternion.getZ() * direction.x;
        double d3 = quaternion.getW() * direction.z + quaternion.getX() * direction.y - quaternion.getY() * direction.x;
        double x = d1 * quaternion.getW() - d * quaternion.getX() - d2 * quaternion.getZ() + d3 * quaternion.getY();
        double y = d2 * quaternion.getW() - d * quaternion.getY() + d1 * quaternion.getZ() - d3 * quaternion.getX();
        double z = d3 * quaternion.getW() - d * quaternion.getZ() - d1 * quaternion.getY() + d2 * quaternion.getX();
        return new Vector3d(x, y, z);
    }

    public static Vector2f getRotationForVector(Vector3d vector3d) {
        float yaw = (float) (MathHelper.atan2(vector3d.z, vector3d.x) * 180 / PI);
        yaw-=90;
        if (yaw < 0)
            yaw += 360;

        float tmp = MathHelper.sqrt (vector3d.x * vector3d.x + vector3d.z * vector3d.z);
        float pitch = (float) (MathHelper.atan2(-vector3d.y, tmp) * 180 / PI);
        if (pitch < 0)
            pitch += 360;

        if(vector3d.x == 0 && vector3d.z == 0){
            if (vector3d.y > 0)
                pitch = 270;
            else
                pitch = 90;
        }
        return new Vector2f(pitch, yaw);
    }

    public static Vector3d getVectorForRotation(float pitch, float yaw) {
        float f = pitch * ((float) PI / 180F);
        float f1 = -yaw * ((float) PI / 180F);
        float f2 = MathHelper.cos(f1);
        float f3 = MathHelper.sin(f1);
        float f4 = MathHelper.cos(f);
        float f5 = MathHelper.sin(f);
        return new Vector3d((double)(f3 * f4), (double)(-f5), (double)(f2 * f4));
    }

    public static Vector3d drawLine(Vector3d start, Vector3d end, double factor) {
        double tx = start.getX() + (end.getX() - start.getX()) * factor + MagickCore.rand.nextGaussian() * 0.005;
        double ty = start.getY() + (end.getY() - start.getY()) * factor + MagickCore.rand.nextGaussian() * 0.005;
        double tz = start.getZ() + (end.getZ() - start.getZ()) * factor + MagickCore.rand.nextGaussian() * 0.005;
        return new Vector3d(tx, ty, tz);
    }

    public static Vector3d drawParabola(Vector3d start, Vector3d end, double factor, double height, Direction direction) {
        return drawParabola(start, end, factor, height, new Vector3d(direction.toVector3f()));
    }

    public static Vector3d drawParabola(Vector3d start, Vector3d end, double factor, double height, Vector3d direction) {
        direction = direction.normalize();
        double tx = start.getX() + (end.getX() - start.getX()) * factor + MagickCore.rand.nextGaussian() * 0.005;
        double ty = start.getY() + (end.getY() - start.getY()) * factor + MagickCore.rand.nextGaussian() * 0.005;
        double tz = start.getZ() + (end.getZ() - start.getZ()) * factor + MagickCore.rand.nextGaussian() * 0.005;
        factor = 1 - factor * 2;
        double y = factor * factor * height;
        return new Vector3d(tx, ty, tz).add(direction.scale(-y)).add(direction.scale(height));
    }
}
