package com.rogoshum.magickcore.client.render;

import com.google.common.collect.Queues;
import com.mojang.math.Vector3f;
import com.rogoshum.magickcore.api.render.RenderHelper;
import net.minecraft.world.phys.Vec2;

import java.util.Queue;

public class Polyhedron {
    public static final Vector3f[] vdata = {
            new Vector3f(-0.5f, -0.5f, -0.5f),
            new Vector3f(0.5f, -0.5f, -0.5f),
            new Vector3f(-0.5f,  0.5f, -0.5f),
            new Vector3f(0.5f,  0.5f, -0.5f),
            new Vector3f(-0.5f, -0.5f,  0.5f),
            new Vector3f(0.5f, -0.5f,  0.5f),
            new Vector3f(-0.5f,  0.5f,  0.5f),
            new Vector3f(0.5f,  0.5f,  0.5f)
    };

    public static final int[][] tindices = {
            {0, 2, 3, 1},
            {0, 4, 6, 2},
            {0, 1, 5, 4},
            {4, 5, 7, 6},
            {1, 3, 7, 5},
            {2, 6, 7, 3}};

    public static final Vec2[] UV = {
            new Vec2(0.5f, 0),
            new Vec2(0, 0),
            new Vec2(0.5f, 0.5f),
            new Vec2(0, 1),
            new Vec2(0.5f, 0),
            new Vec2(0.5f, 0.5f),
            new Vec2(1, 1),
            new Vec2(0.5f, 1),
    };

    public final Queue<RenderHelper.VertexAttribute> VERTEX = Queues.newArrayDeque();
    private final RenderHelper.RenderContext render;
    private final RenderHelper.VertexContext vertex;

    public Polyhedron(RenderHelper.RenderContext render, RenderHelper.VertexContext vertex) {
        this.render = render;
        this.vertex = vertex;
        for (int i = 0; i < 6; i++) {
            addVertex(vdata[tindices[i][0]].x(), vdata[tindices[i][0]].y(), vdata[tindices[i][0]].z(), UV[tindices[i][0]]);
            addVertex(vdata[tindices[i][1]].x(), vdata[tindices[i][1]].y(), vdata[tindices[i][1]].z(), UV[tindices[i][1]]);
            addVertex(vdata[tindices[i][2]].x(), vdata[tindices[i][2]].y(), vdata[tindices[i][2]].z(), UV[tindices[i][2]]);
            addVertex(vdata[tindices[i][3]].x(), vdata[tindices[i][3]].y(), vdata[tindices[i][3]].z(), UV[tindices[i][3]]);
        }
    }

    public static Queue<RenderHelper.VertexAttribute> drawPolyhedron(RenderHelper.RenderContext render, RenderHelper.VertexContext vertex, int depth) {
        return new Polyhedron(render, vertex, depth).VERTEX;
    }

    private Polyhedron(RenderHelper.RenderContext render, RenderHelper.VertexContext vertex, int depth) {
        this.render = render;
        this.vertex = vertex;
        for (int i = 0; i < 6; i++) {
            vdata[tindices[i][0]].normalize();
            vdata[tindices[i][1]].normalize();
            vdata[tindices[i][2]].normalize();
            vdata[tindices[i][3]].normalize();
            subdivide(vdata[tindices[i][0]], vdata[tindices[i][1]], vdata[tindices[i][2]], vdata[tindices[i][3]]
                    , UV[tindices[i][0]], UV[tindices[i][1]], UV[tindices[i][2]], UV[tindices[i][3]], depth);
        }
    }

    public void addVertex(float x, float y, float z, Vec2 uv) {
        x *= 0.5f;
        y *= 0.5f;
        z *= 0.5f;
        if(!vertex.shake)
            VERTEX.add(RenderHelper.VertexAttribute.create().pos(x, y, z)
                    .lightmap(render.packedLightIn).color(render.color).uv(uv).alpha(1).normalAsPos());
        else
            VERTEX.add(RenderHelper.calculateVertex(x, y, z, uv.x, uv.y, render, vertex.hitReaction, vertex.limit));
    }

    public void subdivide(Vector3f v1, Vector3f v2, Vector3f v3, Vector3f v4
            , Vec2 uv1, Vec2 uv2, Vec2 uv3, Vec2 uv4, float depth) {
        if (depth == 0) {
            addVertex(v1.x(), v1.y(), v1.z(), uv1);
            addVertex(v2.x(), v2.y(), v2.z(), uv2);
            addVertex(v3.x(), v3.y(), v3.z(), uv3);
            addVertex(v4.x(), v4.y(), v4.z(), uv4);
            return;
        }

        Vector3f v12 = new Vector3f();
        Vector3f v23 = new Vector3f();
        Vector3f v34 = new Vector3f();
        Vector3f v41 = new Vector3f();
        Vector3f vC = new Vector3f();

        Vec2 uv12 = new Vec2((uv1.x+uv2.x) * 0.5f, (uv1.y+uv2.y) * 0.5f);
        Vec2 uv23 = new Vec2((uv2.x+uv3.x) * 0.5f, (uv2.y+uv3.y) * 0.5f);
        Vec2 uv34 = new Vec2((uv3.x+uv4.x) * 0.5f, (uv3.y+uv4.y) * 0.5f);
        Vec2 uv41 = new Vec2((uv4.x+uv1.x) * 0.5f, (uv4.y+uv1.y) * 0.5f);
        Vec2 uvC = new Vec2((uv1.x+uv2.x+uv3.x+uv4.x) * 0.25f, (uv1.y+uv2.y+uv3.y+uv4.y) * 0.25f);

        v12.set(v1.x() + v2.x(), v1.y() + v2.y(), v1.z() + v2.z());
        v23.set(v2.x() + v3.x(), v2.y() + v3.y(), v2.z() + v3.z());
        v34.set(v3.x() + v4.x(), v3.y() + v4.y(), v3.z() + v4.z());
        v41.set(v4.x() + v1.x(), v4.y() + v1.y(), v4.z() + v1.z());
        vC.set(v12.x()+v23.x()+v34.x()+ v41.x(), v12.y()+v23.y()+v34.y()+ v41.y(), v12.z()+v23.z()+v34.z()+ v41.z());

        v12.normalize();
        v23.normalize();
        v34.normalize();
        v41.normalize();
        vC.normalize();

        subdivide(v1, v12, vC, v41, uv1, uv12, uvC, uv41, depth - 1);
        subdivide(v2, v23, vC, v12, uv2, uv23, uvC, uv12, depth - 1);
        subdivide(v3, v34, vC, v23, uv3, uv34, uvC, uv23, depth - 1);
        subdivide(v4, v41, vC, v34, uv4, uv41, uvC, uv34, depth - 1);
    }
}
