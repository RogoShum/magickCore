package com.rogoshum.magickcore.common.util;

import com.rogoshum.magickcore.api.block.IPanelTileEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;


public class PanelUtil {

    public static boolean isPanelClosed(IPanelTileEntity tileEntity)
    {
        if(tileEntity.getOutputFirst() != null && tileEntity.getOutputSecond() != null)
            return true;

        return false;
    }

    public static boolean isEntityTouchPanel(Entity entity, BlockPos p1, BlockPos p2, BlockPos p3, double distance)
    {
        AABB box = entity.getBoundingBox();
        Plane plane = new Plane(p1, p2, p3);
        if(testBoundingBoxPoint(box.minX, box.minY, box.minZ, plane, box)) return true;

        if(testBoundingBoxPoint(box.minX, box.minY, box.maxZ - 0.01, plane, box)) return true;

        if(testBoundingBoxPoint(box.maxX - 0.01, box.minY, box.maxZ - 0.01, plane, box)) return true;

        if(testBoundingBoxPoint(box.maxX - 0.01, box.minY, box.minZ, plane, box)) return true;

        if(testBoundingBoxPoint(box.minX, box.maxY - 0.01, box.minZ, plane, box)) return true;

        if(testBoundingBoxPoint(box.minX, box.maxY - 0.01, box.maxZ - 0.01, plane, box)) return true;

        if(testBoundingBoxPoint(box.maxX - 0.01, box.maxY - 0.01, box.maxZ - 0.01, plane, box)) return true;

        return testBoundingBoxPoint(box.maxX - 0.01, box.maxY - 0.01, box.minZ, plane, box);
    }

    public static boolean testBoundingBoxPoint(double x, double y, double z, Plane panel, AABB box) {
        Vec3 point = new Vec3(x, y, z);
        Vec3 foot = getPanelFoot(panel.p1, panel.p2, panel.p3, point, 2);
        if(box.contains(foot) && isFootInPlane(panel, foot))
            return true;
        foot = getPanelFoot(panel.p1, panel.p2, panel.p3, new Vec3(x, y, z), -2);

        return box.contains(foot) && isFootInPlane(panel, foot);
    }

    public static boolean isFootInPlane(Plane plane, Vec3 foot){
        double planeSpatium = getPlaneSpatium(plane.p1, plane.p2, plane.p3);

        double planeA = getPlaneSpatium(foot, plane.p2, plane.p3);
        double planeB = getPlaneSpatium(plane.p1, foot, plane.p3);
        double planeC = getPlaneSpatium(plane.p1, plane.p2, foot);

        double footPlane = planeA + planeB + planeC;
        planeSpatium = Double.parseDouble(String.format("%.3f",planeSpatium));
        footPlane = Double.parseDouble(String.format("%.3f",footPlane));

        if(footPlane == planeSpatium)
            return true;

        return false;
    }

    public static double getPlaneSpatium(Vec3 p1, Vec3 p2, Vec3 p3)
    {
        double a = p1.distanceTo(p2);
        double b = p2.distanceTo(p3);
        double c = p3.distanceTo(p1);

        double p = (a+b+c)/2;
        return Math.sqrt(p*(p-a)*(p-b)*(p-c));
    }

    public static Vec3 getPanelFoot(Vec3 p1, Vec3 p2, Vec3 p3, Vec3 pt, double scale){
        double a = ( (p2.y-p1.y)*(p3.z-p1.z)-(p2.z-p1.z)*(p3.y-p1.y) );

        double b = ( (p2.z-p1.z)*(p3.x-p1.x)-(p2.x-p1.x)*(p3.z-p1.z) );

        double c = ( (p2.x-p1.x)*(p3.y-p1.y)-(p2.y-p1.y)*(p3.x-p1.x) );

        double d = ( 0-(a*p1.x+b*p1.y+c*p1.z) );

        double q = a * pt.x + b * pt.y + c * pt.z + d;
        double t = q / (a * a + b * b + c * c);
        return new Vec3(pt.x - a*t, pt.y - b*t, pt.z - c*t);
    }

    public static double distancePanel(BlockPos p1, BlockPos p2, BlockPos p3, BlockPos pt){
        return distancePanel(transPosToVec(p1), transPosToVec(p2), transPosToVec(p3), transPosToVec(pt));
    }

    public static double distancePanel(Vec3 p1, Vec3 p2, Vec3 p3, Vec3 pt){
        double a = ( (p2.y-p1.y)*(p3.z-p1.z)-(p2.z-p1.z)*(p3.y-p1.y) );

        double b = ( (p2.z-p1.z)*(p3.x-p1.x)-(p2.x-p1.x)*(p3.z-p1.z) );

        double c = ( (p2.x-p1.x)*(p3.y-p1.y)-(p2.y-p1.y)*(p3.x-p1.x) );

        double d = ( 0-(a*p1.x+b*p1.y+c*p1.z) );

        return Math.abs(a*pt.x+b*pt.y+c*pt.z+d)/Math.sqrt(a*a+b*b+c*c);
    }

    public static Vec3 transPosToVec(BlockPos pos) {
        return new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }

    public static Vec3 getPanelNormal(BlockPos p1, BlockPos p2, BlockPos p3)
    {
        return getPanelNormal(transPosToVec(p1), transPosToVec(p2), transPosToVec(p3));
    }

    public static BlockPos getPanelCenter(BlockPos p1, BlockPos p2, BlockPos p3)
    {
        return new BlockPos((p1.getX() + p2.getX() + p3.getX()) / 3d, (p1.getY() + p2.getY() + p3.getY()) / 3d, (p1.getZ() + p2.getZ() + p3.getZ()) / 3d);
    }

    public static Vec3 getPanelNormal(Vec3 p1, Vec3 p2, Vec3 p3)
    {
        double a = ((p2.y-p1.y)*(p3.z-p1.z)-(p2.z-p1.z)*(p3.y-p1.y));

        double b = ((p2.z-p1.z)*(p3.x-p1.x)-(p2.x-p1.x)*(p3.z-p1.z));

        double c = ((p2.x-p1.x)*(p3.y-p1.y)-(p2.y-p1.y)*(p3.x-p1.x));

        return new Vec3(a,b,c);
    }

    public static class Plane{
        public final Vec3 p1;
        public final Vec3 p2;
        public final Vec3 p3;

        public Plane(BlockPos p1, BlockPos p2, BlockPos p3)
        {
            this.p1 = transPosToVec(p1);
            this.p2 = transPosToVec(p2);
            this.p3 = transPosToVec(p3);
        }
    }
}
