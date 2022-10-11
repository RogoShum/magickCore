package com.rogoshum.magickcore.client;

import com.rogoshum.magickcore.MagickCore;
import net.minecraft.util.math.vector.Vector3d;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VertexShakerHelper {
    private static ConcurrentHashMap<String, VertexGroup> Groups = new ConcurrentHashMap<String, VertexGroup>();

    private static void newGroup(String s)
    {
        Groups.put(s, new VertexGroup());
    }

    public static VertexGroup getGroup(String s) {
        if(Groups.containsKey(s)) {
            Groups.get(s).reTick();
        }
        else {
            newGroup(s);
        }
        return Groups.get(s);
    }

    public static void tickGroup() {
        Iterator<String> iter = Groups.keySet().iterator();
        while (iter.hasNext()) {
            VertexGroup vertex = Groups.get(iter.next());
            vertex.updateVertex();
            vertex.tick();

            if(vertex.getTick() >= 100)
                iter.remove();
        }
    }

    public static class VertexGroup {
        private final Map<String, VertexShaker> vertexGroup = Collections.synchronizedMap(new HashMap<>());
        private int tick;

        private VertexGroup() {}

        public void putVertex(double x, double y , double z, float limit) {
            if(limit <= 0.0f)
                return;

            String key = Double.toString(x) + Double.toString(y) + Double.toString(z);
            if(!vertexGroup.containsKey(key))
                vertexGroup.put(key, new VertexShaker(new Vector3d(x, y, z), limit));
        }

        public VertexShaker getVertex(double x, double y , double z) {
            String key = Double.toString(x) + Double.toString(y) + Double.toString(z);
            if(vertexGroup.containsKey(key)) {
                return vertexGroup.get(key);
            }
            return new VertexShaker(new Vector3d(x, y, z), 0);
        }

        private void updateVertex() {
            Iterator<String> iter = vertexGroup.keySet().iterator();
            while (iter.hasNext()) {
                VertexShaker vertex = vertexGroup.get(iter.next());
                vertex.update();
            }
        }

        private void tick() { tick++; }

        private int getTick() { return tick; }

        private void reTick() { tick = 0; }
    }
}
