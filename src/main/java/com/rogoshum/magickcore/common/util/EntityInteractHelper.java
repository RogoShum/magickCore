package com.rogoshum.magickcore.common.util;

import com.rogoshum.magickcore.common.item.placeable.EntityItem;
import com.rogoshum.magickcore.common.item.placeable.PlaceableEntityItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

public class EntityInteractHelper {
    public static InteractionResult placeBlock(Player player, InteractionHand hand, ItemStack stack, Entity entity) {
        if(stack.getItem() instanceof BlockItem) {
            Vec3 vector3d = player.getLookAngle();
            BlockPlaceContext blockItemUseContext = new BlockPlaceContext(player, hand, stack,
                    new BlockHitResult(
                            entity.position().add(0, entity.getBbHeight() * 0.5f, 0),
                            Direction.getNearest(vector3d.x, vector3d.y, vector3d.z).getOpposite(),
                            new BlockPos(entity.position()),
                            false
                    ));
            ObfuscationReflectionHelper.setPrivateValue(BlockPlaceContext.class, blockItemUseContext, false, "replaceClicked");
            return ((BlockItem) stack.getItem()).place(blockItemUseContext);
        } else if(stack.getItem() instanceof EntityItem) {
            Vec3 vector3d = player.getLookAngle();
            BlockPlaceContext blockItemUseContext = new BlockPlaceContext(player, hand, stack,
                    new BlockHitResult(
                            entity.position().add(0, entity.getBbHeight() * 0.5f, 0),
                            Direction.getNearest(vector3d.x, vector3d.y, vector3d.z).getOpposite(),
                            new BlockPos(entity.position()),
                            false
                    ));
            ObfuscationReflectionHelper.setPrivateValue(BlockPlaceContext.class, blockItemUseContext, false, "replaceClicked");
            return ((EntityItem) stack.getItem()).tryPlace(blockItemUseContext);
        }
        return InteractionResult.PASS;
    }
}
