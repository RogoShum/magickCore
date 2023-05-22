package com.rogoshum.magickcore.common.magick.ability;

import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.api.enums.ParticleType;
import com.rogoshum.magickcore.api.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.api.magick.context.child.DimensionContext;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.api.magick.context.MagickContext;
import com.rogoshum.magickcore.common.init.ModEntities;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.api.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.api.magick.context.child.PositionContext;
import com.rogoshum.magickcore.api.magick.context.child.SpawnContext;
import com.rogoshum.magickcore.common.util.ItemStackUtil;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import com.rogoshum.magickcore.common.init.ModBuffs;
import com.rogoshum.magickcore.common.init.ModDamages;
import com.rogoshum.magickcore.common.lib.LibBuff;
import com.rogoshum.magickcore.common.lib.LibElements;
import com.rogoshum.magickcore.common.util.ParticleUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fluids.IFluidBlock;

import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;

public class VoidAbility{
    public static boolean radiance(MagickContext context) {
        if(context.caster!= null && context.victim instanceof ItemEntity item) {
            ParticleUtil.spawnBlastParticle(context.world, context.victim.position().add(0, context.victim.getBbHeight() * 0.5, 0), 2, ModElements.VOID, ParticleType.PARTICLE);
            item.setPos(context.caster.position().add(0, -1, 0));
            return true;
        }
        return false;
    }
    public static boolean hitEntity(MagickContext context) {
        if(context.victim == null) return false;
        return ModBuffs.applyBuff(context.victim, LibBuff.WEAKEN, context.tick(), context.force(), false);
    }

    public static boolean damageEntity(MagickContext context) {
        if(context.victim == null) return false;
        if(ModBuffs.hasBuff(context.victim, LibBuff.WEAKEN))
            context.force(context.force() * 1.2f);

        boolean flag;
        if(context.caster != null && context.projectile instanceof Projectile)
            flag = context.victim.hurt(ModDamages.applyProjectileVoidDamage(context.caster, context.projectile), context.force());
        else if(context.caster != null)
            flag = context.victim.hurt(ModDamages.applyEntityVoidDamage(context.caster), context.force());
        else if(context.projectile != null)
            flag = context.victim.hurt(ModDamages.applyEntityVoidDamage(context.projectile), context.force());
        else
            flag = context.victim.hurt(ModDamages.getVoidDamage(), context.force());
        if(flag)
            ModBuffs.applyBuff(context.victim, LibBuff.FRAGILE, 10, 0, false);

        return flag;
    }

    public static boolean hitBlock(MagickContext context) {
        return false;
    }

    public static boolean applyBuff(MagickContext context) {
        if(context.victim == null) return false;
        return ModBuffs.applyBuff(context.victim, LibBuff.LIGHT, context.tick() * 2, context.force(), true);
    }

    public static boolean applyDebuff(MagickContext context) {
        if(context.victim == null) return false;
        return ModBuffs.applyBuff(context.victim, LibBuff.FRAGILE, context.tick(), context.force(), false);
    }

    public static boolean applyToolElement(MagickContext context) {
        int level = (int) context.force();
        if(!(context.caster instanceof LivingEntity)) return false;
        LivingEntity entity = (LivingEntity) context.caster;
        ItemStack stack = entity.getMainHandItem();
        if(stack.hasTag() && NBTTagHelper.hasElementOnTool(stack, LibElements.VOID)) {
            CompoundTag tag = NBTTagHelper.getStackTag(stack);
            tag.putInt("VOID_LEVEL", level);
            stack.setTag(tag);
        }
        return true;
    }

    public static boolean superEntity(MagickContext context) {
        if(context.caster == null) return false;
        SpawnContext spawnContext = new SpawnContext();
        spawnContext.entityType = ModEntities.MANA_SHIELD.get();
        context.addChild(spawnContext);
        context.applyType(ApplyType.SPAWN_ENTITY);
        return MagickReleaseHelper.releaseMagick(context);
    }

    public static boolean diffusion(MagickContext context) {
        Entity target = context.victim;
        if(target == null)
            target = context.caster;
        if(target == null || !target.isAlive()) return false;

        Level level = context.world;
        Vec3 pos;
        ParticleUtil.spawnBlastParticle(context.world, target.position().add(0, target.getBbHeight() * 0.5, 0), 2, ModElements.VOID, ParticleType.PARTICLE);
        if(context.containChild(LibContext.POSITION)) {
            PositionContext positionContext = context.getChild(LibContext.POSITION);
            pos = positionContext.pos;
        } else if(context.projectile != null){
            pos = context.projectile.position();
        } else {
            pos = target.getLookAngle().scale(context.range() * 2).add(target.position());
        }
        if(!context.world.isClientSide() && context.containChild(DimensionContext.TYPE)) {
            DimensionContext dimensionContext = context.getChild(DimensionContext.TYPE);
            Level dimension = context.world.getServer().getLevel(dimensionContext.dimension);
            if(dimension != null && level != dimension) {
                level = dimension;
                target = target.changeDimension((ServerLevel) level);
                if(context.caster instanceof LivingEntity)
                    ExtraDataUtil.entityStateData(context.caster).setManaValue(0);
            }
        }
        if(target != null) {
            target.teleportTo(pos.x, pos.y, pos.z);
            target.playSound(SoundEvents.ENDERMAN_TELEPORT, 0.5f, 1.0f);
            ParticleUtil.spawnBlastParticle(level, target.position().add(0, target.getBbHeight() * 0.5, 0), 2, ModElements.VOID, ParticleType.PARTICLE);
        }
        return true;
    }

    public static boolean agglomerate(MagickContext context) {
        if(context.doBlock && context.containChild(LibContext.POSITION)) {
            PositionContext positionContext = context.getChild(LibContext.POSITION);
            BlockPos pos = new BlockPos(positionContext.pos);
            Level world = context.world;

            if (!world.isAreaLoaded(pos, 1)) return false;
            if(context.caster instanceof Player) {
                if(!world.mayInteract((Player) context.caster, pos)) return false;
            } else if(!world.getGameRules().getRule(GameRules.RULE_MOBGRIEFING).get()) return false;

            BlockState state = world.getBlockState(pos);
            //if(state.getHarvestLevel() > context.force) return false;
            Block block = state.getBlock();
            if (!state.isAir() && !(block instanceof IFluidBlock) && state.getDestroySpeed(world, pos) != -1) {
                int exp = state.getExpDrop((LevelReader) world, pos, (int) context.force(), 1);
                if(context.caster instanceof Player) {
                    /*
                    if (!state.canHarvestBlock(world, pos, (PlayerEntity) context.caster)) {
                        return false;
                    }

                     */
                    BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(world, pos, state, (Player) context.caster);
                    event.setExpToDrop(exp);
                    MinecraftForge.EVENT_BUS.post(event);
                    if(event.isCanceled()) return false;
                    exp = event.getExpToDrop();
                }

                ItemStack result = ItemStack.EMPTY;
                BlockEntity te = null;
                if (block instanceof EntityBlock)
                    te = world.getBlockEntity(pos);

                try {
                    result = state.getCloneItemStack(null, world, pos, null);
                } catch (Exception ignored) {

                }

                if (result.isEmpty())
                    return false;
                if(te != null) {
                    ItemStackUtil.storeTEInStack(result, te);
                } //else
                //result.setCount((int) context.range);

                if (world instanceof ServerLevel) {
                    block.popExperience((ServerLevel) world, pos, exp);
                }
                world.removeBlockEntity(pos);
                world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                world.levelEvent(2001, pos, Block.getId(state));
                ItemEntity entity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, result);
                world.addFreshEntity(entity);
                return true;
            }
            return false;
        }
        if(!(context.victim instanceof LivingEntity)) return false;
        return ModBuffs.applyBuff(context.victim, LibBuff.INVISIBILITY, context.tick() * 10, context.force(), true) && ((LivingEntity) context.victim).addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, context.tick() * 10));
    }
}
