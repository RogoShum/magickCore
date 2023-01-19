package com.rogoshum.magickcore.common.event;

import com.rogoshum.magickcore.api.event.Event;
import com.rogoshum.magickcore.api.event.FabricEvent;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.world.InteractionResult;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class EventBus {
    private static final HashMap<Class<? extends Event>, HashMap<Method, Object>> listenerMap = new HashMap<>();

    public void register(Object obj) {
        final HashSet<Class<?>> classes = new HashSet<>();
        typesFor(obj.getClass(), classes);
        Arrays.stream(obj.getClass().getMethods()).
                filter(m->!Modifier.isStatic(m.getModifiers())).
                forEach(m -> classes.stream().
                        map(c->getDeclMethod(c, m)).
                        filter(rm -> rm.isPresent() && rm.get().isAnnotationPresent(SubscribeEvent.class)).
                        findFirst().
                        ifPresent(rm->registerListener(obj, m, rm.get())));
    }

    private void typesFor(final Class<?> clz, final Set<Class<?>> visited) {
        if (clz.getSuperclass() == null) return;
        typesFor(clz.getSuperclass(),visited);
        Arrays.stream(clz.getInterfaces()).forEach(i->typesFor(i, visited));
        visited.add(clz);
    }

    private Optional<Method> getDeclMethod(final Class<?> clz, final Method in) {
        try {
            return Optional.of(clz.getDeclaredMethod(in.getName(), in.getParameterTypes()));
        } catch (NoSuchMethodException nse) {
            return Optional.empty();
        }

    }

    private void registerListener(final Object target, final Method method, final Method real) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length != 1)
        {
            throw new IllegalArgumentException(
                    "Method " + method + " has @SubscribeEvent annotation. " +
                            "It has " + parameterTypes.length + " arguments, " +
                            "but event handler methods require a single argument only."
            );
        }

        Class<?> eventType = parameterTypes[0];

        if (!Event.class.isAssignableFrom(eventType))
        {
            throw new IllegalArgumentException(
                    "Method " + method + " has @SubscribeEvent annotation, " +
                            "but takes an argument that is not an Event subtype : " + eventType);
        }
        if(!listenerMap.containsKey(target.getClass()))
            listenerMap.put((Class<? extends Event>) eventType, new HashMap<>());
        HashMap<Method, Object> listeners = listenerMap.get(target.getClass());
        listeners.put(real, target);
    }

    public boolean post(Event event) {
        if(!listenerMap.containsKey(event.getClass())) return true;
        HashMap<Method, Object> listeners = listenerMap.get(event.getClass());
        for (Method me :listeners.keySet()) {
            try {
                me.invoke(listeners.get(me), event);
            } catch (Throwable throwable) {

            }
        }
        InteractionResult result = InteractionResult.SUCCESS;
        if(event instanceof FabricEvent) {
            result = ((FabricEvent) event).interact();
        }
        return (event.isCancelable() && event.isCanceled()) || result == InteractionResult.FAIL;
    }
}
