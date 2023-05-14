package com.rogoshum.magickcore.api.extradata;

import com.rogoshum.magickcore.api.entity.IEntityData;
import com.rogoshum.magickcore.api.extradata.entity.*;
import com.rogoshum.magickcore.api.extradata.item.ItemDimensionData;
import com.rogoshum.magickcore.api.extradata.item.ItemManaData;
import com.rogoshum.magickcore.common.lib.LibEntityData;
import net.minecraft.world.entity.Entity;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;


public class ExtraDataUtil {
    public static IEntityData entityData(Entity entity) {
        return (IEntityData) entity;
    }

    public static void itemManaData(ItemStack item, Consumer<ItemManaData> consumer) {
        consumer.accept(new ItemManaData(item));
    }

    public static ItemManaData itemManaData(ItemStack item, int depth) {
        return new ItemManaData(item, true, depth);
    }

    public static ItemManaData itemManaData(ItemStack item) {
        return new ItemManaData(item);
    }

    public static ItemDimensionData itemDimensionData(ItemStack item) {
        return new ItemDimensionData(item);
    }

    public static void entityStateData(Entity entity, Consumer<EntityStateData> consumer) {
        ((IEntityData)entity).execute(LibEntityData.ENTITY_STATE, consumer);
    }

    public static EntityStateData entityStateData(Entity entity) {
        return ((IEntityData)entity).get(LibEntityData.ENTITY_STATE);
    }

    public static PlayerTradeUnlock playerTradeData(Player player) {
        return ((IEntityData)player).get(LibEntityData.PLAYER_TRADE);
    }

    public static void takenEntityData(Entity entity, Consumer<TakenEntityData> consumer) {
        ((IEntityData)entity).execute(LibEntityData.TAKEN_ENTITY, consumer);
    }

    public static TakenEntityData takenEntityData(Entity entity) {
        return ((IEntityData)entity).get(LibEntityData.TAKEN_ENTITY);
    }

    public static LeechEntityData leechEntityData(Entity entity) {
        return ((IEntityData)entity).get(LibEntityData.LEECH_ENTITY);
    }

    public static void elementToolData(Entity entity, Consumer<ElementToolData> consumer) {
        ((IEntityData)entity).execute(LibEntityData.ELEMENT_TOOL, consumer);
    }

    public static ElementToolData elementToolData(Entity entity) {
        return ((IEntityData)entity).get(LibEntityData.ELEMENT_TOOL);
    }
}