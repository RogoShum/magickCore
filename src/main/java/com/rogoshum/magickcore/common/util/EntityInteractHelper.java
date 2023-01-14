package com.rogoshum.magickcore.common.util;

import com.rogoshum.magickcore.common.item.placeable.EntityItem;
import com.rogoshum.magickcore.common.item.placeable.PlaceableEntityItem;
import net.minecraft.entity.Entity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vec3;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class EntityInteractHelper {
    public static ActionResultType placeBlock(Player player, Hand hand, ItemStack stack, Entity entity) {
        if(stack.getItem() instanceof BlockItem) {
            Vec3 vector3d = player.getLookAngle();
            BlockItemUseContext blockItemUseContext = new BlockItemUseContext(player, hand, stack,
                    new BlockRayTraceResult(
                            entity.position().add(0, entity.getBbHeight() * 0.5f, 0),
                            Direction.getNearest(vector3d.x, vector3d.y, vector3d.z).getOpposite(),
                            new BlockPos(entity.position()),
                            false
                    ));
            ObfuscationReflectionHelper.setPrivateValue(BlockItemUseContext.class, blockItemUseContext, false, "replaceClicked");
            return ((BlockItem) stack.getItem()).place(blockItemUseContext);
        } else if(stack.getItem() instanceof EntityItem) {
            Vec3 vector3d = player.getLookAngle();
            BlockItemUseContext blockItemUseContext = new BlockItemUseContext(player, hand, stack,
                    new BlockRayTraceResult(
                            entity.position().add(0, entity.getBbHeight() * 0.5f, 0),
                            Direction.getNearest(vector3d.x, vector3d.y, vector3d.z).getOpposite(),
                            new BlockPos(entity.position()),
                            false
                    ));
            ObfuscationReflectionHelper.setPrivateValue(BlockItemUseContext.class, blockItemUseContext, false, "replaceClicked");
            return ((EntityItem) stack.getItem()).tryPlace(blockItemUseContext);
        }
        return ActionResultType.PASS;
    }
}
