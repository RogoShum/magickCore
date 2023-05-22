package com.rogoshum.magickcore.common.magick.ability;

import com.rogoshum.magickcore.api.entity.IManaEntity;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.api.enums.ParticleType;
import com.rogoshum.magickcore.common.entity.projectile.PhantomEntity;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.init.ModEntities;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.api.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.init.ModBuffs;
import com.rogoshum.magickcore.common.init.ModDamages;
import com.rogoshum.magickcore.common.lib.LibBuff;
import com.rogoshum.magickcore.api.magick.context.MagickContext;
import com.rogoshum.magickcore.api.magick.context.child.PositionContext;
import com.rogoshum.magickcore.api.magick.context.child.SpawnContext;
import com.rogoshum.magickcore.common.util.ParticleUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.util.FakePlayerFactory;

public class StasisAbility{
    public static boolean radiance(MagickContext context) {
        if(!context.world.isClientSide() && context.victim instanceof ItemEntity item && item.tickCount > 10) {
            if(item.getItem().getItem() instanceof BlockItem) {
                ParticleUtil.spawnBlastParticle(context.world, context.victim.position(), 2, ModElements.STASIS, ParticleType.PARTICLE);
                BlockPlaceContext place = new BlockPlaceContext(context.world, FakePlayerFactory.getMinecraft((ServerLevel) context.world), InteractionHand.MAIN_HAND, item.getItem(), new BlockHitResult(Vec3.ZERO, Direction.UP, item.getOnPos().above(), false));
                item.getItem().useOn(place);
                return true;
            }
        }
        return false;
    }
    public static boolean hitEntity(MagickContext context) {
        if(context.victim == null) return false;
        return ModBuffs.applyBuff(context.victim, LibBuff.SLOW, context.tick(), context.force(), false);
    }

    public static boolean damageEntity(MagickContext context) {
        if(context.victim == null) return false;
        if(ModBuffs.hasBuff(context.victim, LibBuff.SLOW))
            context.force(context.force() * 1.25f);

        boolean flag = false;
        if(context.caster != null && context.projectile instanceof Projectile)
            flag = context.victim.hurt(ModDamages.applyProjectileStasisDamage(context.caster, context.projectile), context.force());
        else if(context.caster != null)
            flag = context.victim.hurt(ModDamages.applyEntityStasisDamage(context.caster), context.force());
        else if(context.projectile != null)
            flag = context.victim.hurt(ModDamages.applyEntityStasisDamage(context.projectile), context.force());
        else
            flag = context.victim.hurt(ModDamages.getStasisDamage(), context.force());
        if(flag)
            ModBuffs.applyBuff(context.victim, LibBuff.FREEZE, context.tick() / 8, 0, false);

        return flag;
    }

    public static boolean hitBlock(MagickContext context) {
        if(!context.world.isClientSide && context.containChild(LibContext.POSITION)) {
            PositionContext positionContext = context.getChild(LibContext.POSITION);

            BlockPos pos = new BlockPos(positionContext.pos);
            if (context.world.getBlockState(pos).getBlock().equals(Blocks.WATER)) {
                context.world.setBlockAndUpdate(pos, Blocks.ICE.defaultBlockState());
                return true;
            }

            if (context.world.isEmptyBlock(pos.offset(0, 1, 0)) && Blocks.SNOW.defaultBlockState().canSurvive(context.world, pos.offset(0, 1, 0))) {
                context.world.setBlock(pos.offset(0, 1, 0), Blocks.SNOW.defaultBlockState(), 2);
                return true;
            }
        }
        return false;
    }

    public static boolean applyBuff(MagickContext context) {
        if(context.victim == null) return false;
        return ModBuffs.applyBuff(context.victim, LibBuff.STASIS, context.tick() * 2, context.force(), true);
    }

    public static boolean applyDebuff(MagickContext context) {
        if(context.victim == null) return false;
        if(context.force() >= 7)
            return ModBuffs.applyBuff(context.victim, LibBuff.FREEZE, context.tick(), context.force(), false);
        return ModBuffs.applyBuff(context.victim, LibBuff.SLOW, context.tick(), context.force(), false);
    }

    public static boolean superEntity(MagickContext context) {
        if(context.caster == null) return false;
        SpawnContext spawnContext = new SpawnContext();
        spawnContext.entityType = ModEntities.SILENCE_SQUALL.get();
        context.addChild(spawnContext);
        context.applyType(ApplyType.SPAWN_ENTITY);
        return MagickReleaseHelper.releaseMagick(context);
    }

    public static boolean agglomerate(MagickContext context) {
        if(!(context.victim instanceof LivingEntity) && !context.world.isClientSide) {
            Vec3 pos = Vec3.ZERO;
            if(context.victim != null)
                pos = context.victim.position();
            if(context.containChild(LibContext.POSITION))
                pos = context.<PositionContext>getChild(LibContext.POSITION).pos;

            if(pos.y > 192) {
                ((ServerLevel)context.world).setWeatherParameters(0, 6000, true, false);
            }
        }

        if(context.victim == null || context.victim instanceof IManaEntity || context.world.isClientSide) return false;
        PhantomEntity phantom = new PhantomEntity(ModEntities.PHANTOM.get(), context.world);
        phantom.setEntity(context.victim);
        phantom.setPos(context.victim.getX(), context.victim.getY(), context.victim.getZ());
        phantom.spellContext().tick(context.tick() * 2);
        context.world.addFreshEntity(phantom);
        return true;
    }

    public static boolean diffusion(MagickContext context) {
        if(!(context.victim instanceof LivingEntity)) return false;
        return ModBuffs.applyBuff(context.victim, LibBuff.PURE, context.tick() * 2, context.force(), true);
    }
}
