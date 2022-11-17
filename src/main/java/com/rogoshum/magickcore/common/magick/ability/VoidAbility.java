package com.rogoshum.magickcore.common.magick.ability;

import com.rogoshum.magickcore.common.api.enums.ApplyType;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import com.rogoshum.magickcore.common.init.ModEntities;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.magick.context.child.PositionContext;
import com.rogoshum.magickcore.common.magick.context.child.SpawnContext;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import com.rogoshum.magickcore.common.init.ModBuff;
import com.rogoshum.magickcore.common.init.ModDamage;
import com.rogoshum.magickcore.common.lib.LibBuff;
import com.rogoshum.magickcore.common.lib.LibElements;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SkullItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.*;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fluids.IFluidBlock;

public class VoidAbility{
    public static boolean hitEntity(MagickContext context) {
        if(context.victim == null) return false;
        return ModBuff.applyBuff(context.victim, LibBuff.WEAKEN, context.tick, context.force, false);
    }

    public static boolean damageEntity(MagickContext context) {
        if(context.victim == null) return false;
        if(ModBuff.hasBuff(context.victim, LibBuff.WEAKEN))
            context.force *= 2;

        boolean flag;
        if(context.caster != null && context.projectile != null)
            flag = context.victim.attackEntityFrom(ModDamage.applyProjectileVoidDamage(context.caster, context.projectile), context.force);
        else if(context.caster != null)
            flag = context.victim.attackEntityFrom(ModDamage.applyEntityVoidDamage(context.caster), context.force);
        else if(context.projectile != null)
            flag = context.victim.attackEntityFrom(ModDamage.applyEntityVoidDamage(context.projectile), context.force);
        else
            flag = context.victim.attackEntityFrom(ModDamage.getVoidDamage(), context.force);
        if(flag)
            ModBuff.applyBuff(context.victim, LibBuff.FRAGILE, 10, 0, false);

        return flag;
    }

    public static boolean hitBlock(MagickContext context) {
        if(context.force < 1) return false;
        if(!context.world.isRemote && context.containChild(LibContext.POSITION)) {
            PositionContext positionContext = context.getChild(LibContext.POSITION);
            BlockPos pos = new BlockPos(positionContext.pos);
            World world = context.world;

            if (!world.isAreaLoaded(pos, 1)) return false;
            if(context.caster instanceof PlayerEntity) {
                if(!world.isBlockModifiable((PlayerEntity) context.caster, pos)) return false;
            } else if(!world.getGameRules().get(GameRules.MOB_GRIEFING).get()) return false;

            BlockState state = world.getBlockState(pos);
            int har = state.getHarvestLevel();
            if(state.getHarvestLevel() > context.force) return false;
            Block block = state.getBlock();
            if (!block.isAir(state, world, pos) && !(block instanceof IFluidBlock) && state.getBlockHardness(world, pos) != -1) {
                int exp = state.getExpDrop((IWorldReader) world, pos, (int) context.force, 1);
                if(context.caster instanceof PlayerEntity) {
                    /*
                    if (!state.canHarvestBlock(world, pos, (PlayerEntity) context.caster)) {
                        return false;
                    }

                     */
                    BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(world, pos, state, (PlayerEntity) context.caster);
                    event.setExpToDrop(exp);
                    MinecraftForge.EVENT_BUS.post(event);
                    if(event.isCanceled()) return false;
                    exp = event.getExpToDrop();
                }

                ItemStack result = ItemStack.EMPTY;
                TileEntity te = null;
                if (state.hasTileEntity())
                    te = world.getTileEntity(pos);

                try {
                    result = state.getPickBlock(null, world, pos, null);
                } catch (Exception ignored) {

                }

                if (result.isEmpty())
                    return false;
                if(te != null) {
                    storeTEInStack(result, te);
                } //else
                    //result.setCount((int) context.range);

                if (world instanceof ServerWorld) {
                    block.dropXpOnBlockBreak((ServerWorld) world, pos, exp);
                }
                world.setBlockState(pos, Blocks.AIR.getDefaultState());
                world.playEvent(2001, pos, Block.getStateId(state));
                ItemEntity entity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, result);
                world.addEntity(entity);
                return true;
            }
        }
        return false;
    }

    public static void storeTEInStack(ItemStack stack, TileEntity te) {
        CompoundNBT compoundnbt = te.write(new CompoundNBT());
        if (stack.getItem() instanceof SkullItem && compoundnbt.contains("SkullOwner")) {
            CompoundNBT compoundnbt2 = compoundnbt.getCompound("SkullOwner");
            stack.getOrCreateTag().put("SkullOwner", compoundnbt2);
        } else {
            stack.setTagInfo("BlockEntityTag", compoundnbt);
        }
    }

    public static boolean applyBuff(MagickContext context) {
        if(context.victim == null) return false;
        return ModBuff.applyBuff(context.victim, LibBuff.LIGHT, context.tick, context.force, true);
    }

    public static boolean applyDebuff(MagickContext context) {
        if(context.victim == null) return false;
        return ModBuff.applyBuff(context.victim, LibBuff.FRAGILE, context.tick, context.force, false);
    }

    public static boolean applyToolElement(MagickContext context) {
        int level = (int) context.force;
        if(!(context.caster instanceof LivingEntity)) return false;
        LivingEntity entity = (LivingEntity) context.caster;
        ItemStack stack = entity.getHeldItemMainhand();
        if(stack.hasTag() && NBTTagHelper.hasElementOnTool(stack, LibElements.VOID)) {
            CompoundNBT tag = NBTTagHelper.getStackTag(stack);
            tag.putInt("VOID_LEVEL", level);
            stack.setTag(tag);
        }
        return true;
    }

    public static boolean superEntity(MagickContext context) {
        if(context.caster == null) return false;
        PositionContext positionContext = PositionContext.create(context.caster.getPositionVec().add(0, -4, 0));
        context.addChild(positionContext);
        SpawnContext spawnContext = new SpawnContext();
        spawnContext.entityType = ModEntities.MANA_SHIELD.get();
        context.addChild(spawnContext);
        context.applyType(ApplyType.SPAWN_ENTITY);
        return MagickReleaseHelper.releaseMagick(context);
    }

    public static boolean diffusion(MagickContext context) {
        if(context.victim == null || context.caster == null) return false;
        if(context.containChild(LibContext.POSITION)) {
            PositionContext positionContext = context.getChild(LibContext.POSITION);
            Vector3d pos = positionContext.pos;
            context.victim.setPosition(pos.x, pos.y, pos.z);
        } else {
            Vector3d pos = context.caster.getLookVec().scale(context.range).add(context.victim.getPositionVec());
            context.victim.setPosition(pos.x, pos.y, pos.z);
        }
        return true;
    }

    public static boolean agglomerate(MagickContext context) {
        if(!(context.victim instanceof LivingEntity)) return false;
        return ModBuff.applyBuff(context.victim, LibBuff.INVISIBILITY, context.tick * 10, context.force, true) && ((LivingEntity) context.victim).addPotionEffect(new EffectInstance(Effects.INVISIBILITY, context.tick * 10));
    }
}