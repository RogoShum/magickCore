package com.rogoshum.magickcore.common.util;

import com.rogoshum.magickcore.common.tileentity.IPanelTileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;


public class PanelUtil {

    public static boolean isPanelClosed(IPanelTileEntity tileEntity)
    {
        if(tileEntity.getOutputFirst() != null && tileEntity.getOutputSecond() != null)
            return true;

        return false;
    }

    public static boolean isEntityTouchPanel(Entity entity, BlockPos p1, BlockPos p2, BlockPos p3, double distance)
    {
        AxisAlignedBB box = entity.getBoundingBox();
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

    public static boolean testBoundingBoxPoint(double x, double y, double z, Plane panel, AxisAlignedBB box) {
        Vector3d point = new Vector3d(x, y, z);
        Vector3d foot = getPanelFoot(panel.p1, panel.p2, panel.p3, point, 2);
        if(box.contains(foot) && isFootInPlane(panel, foot))
            return true;
        foot = getPanelFoot(panel.p1, panel.p2, panel.p3, new Vector3d(x, y, z), -2);

        return box.contains(foot) && isFootInPlane(panel, foot);
    }

    public static boolean isFootInPlane(Plane plane, Vector3d foot){
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

    public static double getPlaneSpatium(Vector3d p1, Vector3d p2, Vector3d p3)
    {
        double a = p1.distanceTo(p2);
        double b = p2.distanceTo(p3);
        double c = p3.distanceTo(p1);

        double p = (a+b+c)/2;
        return Math.sqrt(p*(p-a)*(p-b)*(p-c));
    }

    public static Vector3d getPanelFoot(Vector3d p1, Vector3d p2, Vector3d p3, Vector3d pt, double scale){
        double a = ( (p2.y-p1.y)*(p3.z-p1.z)-(p2.z-p1.z)*(p3.y-p1.y) );

        double b = ( (p2.z-p1.z)*(p3.x-p1.x)-(p2.x-p1.x)*(p3.z-p1.z) );

        double c = ( (p2.x-p1.x)*(p3.y-p1.y)-(p2.y-p1.y)*(p3.x-p1.x) );

        double d = ( 0-(a*p1.x+b*p1.y+c*p1.z) );

        double q = a * pt.x + b * pt.y + c * pt.z + d;
        double t = q / (a * a + b * b + c * c);
        return new Vector3d(pt.x - a*t, pt.y - b*t, pt.z - c*t);
    }

    public static double distancePanel(BlockPos p1, BlockPos p2, BlockPos p3, BlockPos pt){
        return distancePanel(transPosToVec(p1), transPosToVec(p2), transPosToVec(p3), transPosToVec(pt));
    }

    public static double distancePanel(Vector3d p1, Vector3d p2, Vector3d p3, Vector3d pt){
        double a = ( (p2.y-p1.y)*(p3.z-p1.z)-(p2.z-p1.z)*(p3.y-p1.y) );

        double b = ( (p2.z-p1.z)*(p3.x-p1.x)-(p2.x-p1.x)*(p3.z-p1.z) );

        double c = ( (p2.x-p1.x)*(p3.y-p1.y)-(p2.y-p1.y)*(p3.x-p1.x) );

        double d = ( 0-(a*p1.x+b*p1.y+c*p1.z) );

        return Math.abs(a*pt.x+b*pt.y+c*pt.z+d)/Math.sqrt(a*a+b*b+c*c);
    }

    public static Vector3d transPosToVec(BlockPos pos) {
        return new Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }

    public static Vector3d getPanelNormal(BlockPos p1, BlockPos p2, BlockPos p3)
    {
        return getPanelNormal(transPosToVec(p1), transPosToVec(p2), transPosToVec(p3));
    }

    public static BlockPos getPanelCenter(BlockPos p1, BlockPos p2, BlockPos p3)
    {
        return new BlockPos((p1.getX() + p2.getX() + p3.getX()) / 3d, (p1.getY() + p2.getY() + p3.getY()) / 3d, (p1.getZ() + p2.getZ() + p3.getZ()) / 3d);
    }

    public static Vector3d getPanelNormal(Vector3d p1, Vector3d p2, Vector3d p3)
    {
        double a = ((p2.y-p1.y)*(p3.z-p1.z)-(p2.z-p1.z)*(p3.y-p1.y));

        double b = ((p2.z-p1.z)*(p3.x-p1.x)-(p2.x-p1.x)*(p3.z-p1.z));

        double c = ((p2.x-p1.x)*(p3.y-p1.y)-(p2.y-p1.y)*(p3.x-p1.x));

        return new Vector3d(a,b,c);
    }

    public static class Plane{
        public final Vector3d p1;
        public final Vector3d p2;
        public final Vector3d p3;

        public Plane(BlockPos p1, BlockPos p2, BlockPos p3)
        {
            this.p1 = transPosToVec(p1);
            this.p2 = transPosToVec(p2);
            this.p3 = transPosToVec(p3);
        }
    }
}
