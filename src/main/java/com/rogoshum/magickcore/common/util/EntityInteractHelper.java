package com.rogoshum.magickcore.common.util;

import com.rogoshum.magickcore.common.item.placeable.EntityItem;
import com.rogoshum.magickcore.common.item.placeable.PlaceableEntityItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class EntityInteractHelper {
    public static ActionResultType placeBlock(PlayerEntity player, Hand hand, ItemStack stack, Entity entity) {
        if(stack.getItem() instanceof BlockItem) {
            Vector3d vector3d = player.getLookVec();
            BlockItemUseContext blockItemUseContext = new BlockItemUseContext(player, hand, stack,
                    new BlockRayTraceResult(
                            entity.getPositionVec().add(0, entity.getHeight() * 0.5f, 0),
                            Direction.getFacingFromVector(vector3d.x, vector3d.y, vector3d.z).getOpposite(),
                            new BlockPos(entity.getPositionVec()),
                            false
                    ));
            ObfuscationReflectionHelper.setPrivateValue(BlockItemUseContext.class, blockItemUseContext, false, "field_196013_a");
            return ((BlockItem) stack.getItem()).tryPlace(blockItemUseContext);
        } else if(stack.getItem() instanceof EntityItem) {
            Vector3d vector3d = player.getLookVec();
            BlockItemUseContext blockItemUseContext = new BlockItemUseContext(player, hand, stack,
                    new BlockRayTraceResult(
                            entity.getPositionVec().add(0, entity.getHeight() * 0.5f, 0),
                            Direction.getFacingFromVector(vector3d.x, vector3d.y, vector3d.z).getOpposite(),
                            new BlockPos(entity.getPositionVec()),
                            false
                    ));
            ObfuscationReflectionHelper.setPrivateValue(BlockItemUseContext.class, blockItemUseContext, false, "field_196013_a");
            return ((EntityItem) stack.getItem()).tryPlace(blockItemUseContext);
        }
        return ActionResultType.PASS;
    }
}
