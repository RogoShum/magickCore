package com.rogoshum.magickcore.registry;

import com.rogoshum.magickcore.api.IRegistry;
import com.rogoshum.magickcore.init.ModElements;
import com.rogoshum.magickcore.lib.LibRegistry;
import com.rogoshum.magickcore.magick.condition.Condition;
import com.rogoshum.magickcore.magick.context.child.ChildContext;
import com.rogoshum.magickcore.registry.elementmap.ElementFunctions;
import com.rogoshum.magickcore.magick.MagickElement;
import com.rogoshum.magickcore.registry.elementmap.EntityRenderers;
import com.rogoshum.magickcore.registry.elementmap.RenderFunctions;
import com.rogoshum.magickcore.magick.extradata.EntityExtraData;
import com.rogoshum.magickcore.magick.extradata.ItemExtraData;

import java.util.HashMap;
import java.util.concurrent.Callable;

public class MagickRegistry {
    private static final HashMap<String, IRegistry<?>> registries = new HashMap<>();

    public static void register(String type, IRegistry<?> registry) {
        if(!registries.containsKey(type)) {
            registries.put(type, registry);
        }
        else try {
            throw new Exception("Containing same type in the registries = [" + type +"]");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> IRegistry<T> getRegistry(String type) {
        if(registries.containsKey(type)) {
            return registries.get(type) != null ? (IRegistry<T>) registries.get(type) : null;
        }
        return null;
    }

    public static <T> T getObjectByID(String type, String id) {
        if(registries.containsKey(type)) {
            return registries.get(type).get(id) != null ? (T) registries.get(type).get(id) : null;
        }
        return null;
    }

    public static MagickElement getElement(String type) {
        Object value = registries.get(LibRegistry.ELEMENT).get(type);
        if(value instanceof MagickElement)
            return (MagickElement) value;
        return ModElements.ORIGIN;
    }

    public static ElementFunctions getElementFunctions(String type) {
        Object value = registries.get(LibRegistry.ELEMENT_FUNCTION).get(type);
        if(value instanceof ElementFunctions)
            return (ElementFunctions) value;
        return ElementFunctions.create();
    }

    public static RenderFunctions getRenderFunctions(String type) {
        Object value = registries.get(LibRegistry.RENDER_FUNCTION).get(type);
        if(value instanceof RenderFunctions)
            return (RenderFunctions) value;
        return RenderFunctions.create();
    }

    public static EntityRenderers getEntityRenderers(String type) {
        Object value = registries.get(LibRegistry.ENTITY_RENDERER).get(type);
        if(value instanceof EntityRenderers)
            return (EntityRenderers) value;
        return EntityRenderers.create();
    }

    public static ChildContext getChildContext(String id) {
        Object value = registries.get(LibRegistry.CHILD_CONTEXT).get(id);
        try{
            if(value instanceof Callable)
                return ((Callable<ChildContext>) value).call();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Condition getCondition(String id) {
        Object value = registries.get(LibRegistry.CONDITION).get(id);
        try{
            if(value instanceof Callable)
                return ((Callable<Condition>) value).call();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public static ObjectRegistry<Callable<EntityExtraData>> getEntityExtraData() {
        return (ObjectRegistry<Callable<EntityExtraData>>) registries.get(LibRegistry.ENTITY_DATA);
    }

    @SuppressWarnings("unchecked")
    public static ObjectRegistry<Callable<ItemExtraData>> getItemExtraData() {
        return (ObjectRegistry<Callable<ItemExtraData>>) registries.get(LibRegistry.ITEM_DATA);
    }
}
