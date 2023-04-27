package com.rogoshum.magickcore.common.integration.botania;

import com.rogoshum.magickcore.api.entity.IManaEntity;
import com.rogoshum.magickcore.api.enums.ParticleType;
import com.rogoshum.magickcore.api.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.api.extradata.entity.LeechEntityData;
import com.rogoshum.magickcore.common.init.ModBuffs;
import com.rogoshum.magickcore.common.init.ModDamages;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.lib.LibBuff;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.lib.LibElements;
import com.rogoshum.magickcore.api.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.api.magick.context.MagickContext;
import com.rogoshum.magickcore.api.magick.context.child.DirectionContext;
import com.rogoshum.magickcore.api.magick.context.child.PositionContext;
import com.rogoshum.magickcore.common.util.ParticleBuilder;
import com.rogoshum.magickcore.common.util.ParticleUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import vazkii.botania.api.internal.IManaBurst;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.api.item.ISparkEntity;
import vazkii.botania.api.mana.*;
import vazkii.botania.api.recipe.IManaInfusionRecipe;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.block.tile.mana.IThrottledPacket;
import vazkii.botania.common.block.tile.mana.TilePool;
import vazkii.botania.common.entity.EntityManaBurst;
import vazkii.botania.common.entity.ModEntities;
import vazkii.botania.common.handler.ModSounds;
import vazkii.botania.xplat.BotaniaConfig;
import vazkii.botania.xplat.IXplatAbstractions;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class BotaniaAbility {
    public static boolean radiance(MagickContext context) {
        if(context.victim == null) return false;
        if(!context.world.isDay()) return false;
        BlockPos pos = context.victim.getOnPos().above();
        if(context.victim instanceof ISparkEntity)
            pos = pos.below(2);
        BlockState state = context.world.getBlockState(pos);
        BlockEntity tile = context.world.getBlockEntity(pos);
        var receiver = IXplatAbstractions.INSTANCE.findManaReceiver(context.world, pos, state, tile, Direction.DOWN);
        if(receiver != null && !receiver.isFull()) {
            ParticleUtil.spawnBlastParticle(context.world, Vec3.atCenterOf(pos), 2, ModElements.BOTANIA, ParticleType.PARTICLE);
            receiver.receiveMana(Math.max(1, (int) (context.force*10)));
            return true;
        }
        return false;
    }

    public static boolean damageEntity(MagickContext context) {
        if(context.victim == null || context.victim instanceof ItemEntity) return false;

        if(ModBuffs.hasBuff(context.victim, LibBuff.THORNS))
            context.force *= 1.25f;
        if(context.caster != null && context.projectile instanceof Projectile)
            return context.victim.hurt(ModDamages.applyProjectileBotaniaDamage(context.caster, context.projectile), context.force);
        else if(context.caster != null)
            return context.victim.hurt(ModDamages.applyEntityBotaniaDamage(context.caster), context.force);
        else if(context.projectile != null)
            return context.victim.hurt(ModDamages.applyEntityBotaniaDamage(context.projectile), context.force);
        else
            return context.victim.hurt(ModDamages.getBotaniaDamage(), context.force);
    }

    public static boolean superEntity(MagickContext context) {
        if(!(context.caster instanceof LivingEntity)) return false;
        List<LivingEntity> livings = context.world.getEntitiesOfClass(LivingEntity.class, context.caster.getBoundingBox().inflate(32), Entity::isAlive);
        context.world.playSound(null, context.caster, ModSounds.manaBlaster, SoundSource.PLAYERS, 2.0f, 0.0f);
        for(LivingEntity living : livings) {
            if(living == context.caster || MagickReleaseHelper.sameLikeOwner(context.caster, living)) continue;
            LeechEntityData data = ExtraDataUtil.leechEntityData(living);
            if(!context.world.isClientSide()) {
                data.setCount(context.tick/80);
                data.setForce(1.5f);
                data.setOwner((LivingEntity) context.caster);
                ModBuffs.applyBuff(living, LibBuff.THORNS, context.tick, context.force, false);

                ParticleBuilder.create(context.world, ParticleType.PARTICLE, new Vec3(living.position().x
                        , living.position().y+living.getBbHeight()*0.5
                        , living.position().z), 1.2f, 1.2f, 1.0f, 100, LibElements.BOTANIA);
            }
        }
        return true;
    }

    public static boolean hitBlock(MagickContext context) {
        if(!context.world.isClientSide && context.containChild(PositionContext.TYPE) && context.containChild(DirectionContext.TYPE)) {
            PositionContext positionContext = context.getChild(LibContext.POSITION);
            Vec3 dir = context.getChild(DirectionContext.TYPE).direction;
            BlockPos pos = new BlockPos(positionContext.pos);
            BlockPos side = new BlockPos(pos.relative(Direction.getNearest(dir.x, dir.y, dir.z).getOpposite()));
            DyeColor color = DyeColor.byId(context.world.random.nextInt(16));
            BlockState flower = ModBlocks.getFlower(color).defaultBlockState();
            if (context.world.isEmptyBlock(side) && flower.canSurvive(context.world, side)) {
                if (MagickReleaseHelper.consumePlayerMana((LivingEntity) context.caster, 100)) {
                    if (BotaniaConfig.common().blockBreakParticles()) {
                        context.world.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, side, Block.getId(flower));
                    }
                    context.world.setBlockAndUpdate(side, flower);
                    return true;
                }
            }

            BlockEntity tile = context.world.getBlockEntity(pos);
            BlockState state = context.world.getBlockState(pos);

            var receiver = IXplatAbstractions.INSTANCE.findManaReceiver(context.world, pos, state, tile, Direction.getNearest(dir.x, dir.y, dir.z).getOpposite());
            EntityManaBurst manaBurst = new EntityManaBurst(ModEntities.MANA_BURST, context.world);
            manaBurst.setMana((int) (context.force * context.range * 10));
            manaBurst.setStartingMana(manaBurst.getMana());
            boolean dead = false;
            if (receiver != null && receiver.canReceiveManaFromBursts() && onReceiverImpact(receiver, manaBurst)) {
                dead = true;
                ParticleUtil.spawnBlastParticle(context.world, positionContext.pos.add(0, 0.5, 0), 3, ModElements.BOTANIA, ParticleType.MIST);
                if (tile instanceof IThrottledPacket throttledPacket) {
                    throttledPacket.markDispatchable();
                } else if (tile != null) {
                    VanillaPacketDispatcher.dispatchTEToNearbyPlayers(tile);
                }
            }

            var trigger = IXplatAbstractions.INSTANCE.findManaTrigger(context.world, pos, state, tile);
            if (trigger != null) {
                trigger.onBurstCollision(manaBurst);
            }
            if(dead && context.projectile instanceof IManaEntity)
                context.projectile.remove(Entity.RemovalReason.DISCARDED);
            return true;
        }
        return false;
    }

    private static boolean onReceiverImpact(IManaReceiver receiver, IManaBurst manaBurst) {
        if (manaBurst.hasWarped()) {
            return false;
        }

        int mana = manaBurst.getMana();

        if (receiver instanceof IManaCollector collector) {
            mana *= collector.getManaYieldMultiplier(manaBurst);
        }

        if (mana > 0) {
            receiver.receiveMana(mana);
            return true;
        }

        return false;
    }

    public static final ItemStack SAMPLE = new ItemStack(Items.CAKE);

    public static boolean applyBuff(MagickContext context) {
        if(context.victim == null) return false;
        return ModBuffs.applyBuff(context.victim, LibBuff.BOTAN, context.tick, context.force, true);
    }

    public static boolean applyDebuff(MagickContext context) {
        if(!(context.victim instanceof LivingEntity)) return false;
        LeechEntityData data = ExtraDataUtil.leechEntityData(context.victim);
        if(data != null) {
            data.setCount(context.tick/20);
            data.setForce(context.force);
            data.setOwner((LivingEntity) context.caster);
            ModBuffs.applyBuff(context.victim, LibBuff.THORNS, context.tick, context.force, false);
        }
        return data != null;
    }

    public static boolean diffusion(MagickContext context) {
        if(!(context.victim instanceof Player)) return false;
        boolean success = ManaItemHandler.instance().requestManaExact(SAMPLE, (Player) context.victim, (int) (1000*context.force), true);
        if(success) {
            MagickReleaseHelper.addPlayerMana((LivingEntity) context.victim, (int) (100*context.force));
            ParticleUtil.spawnBlastParticle(context.world, context.victim.position().add(0, context.victim.getBbHeight() * 0.5, 0), 4, ModElements.BOTANIA, ParticleType.MIST);
        }
        return success;
    }

    public static boolean agglomerate(MagickContext context) {
        if(context.caster instanceof LivingEntity && context.victim instanceof ItemEntity itemEntity) {
            ItemStack stack = itemEntity.getItem();
            IManaInfusionRecipe recipe = getMatchingRecipe(stack, context.world);

            if (recipe != null) {
                int mana = stack.getCount() * 80;
                if (MagickReleaseHelper.consumePlayerMana((LivingEntity) context.caster, mana)) {
                    ParticleUtil.spawnBlastParticle(context.world, context.victim.position().add(0, context.victim.getBbHeight() * 0.5, 0), 4, ModElements.BOTANIA, ParticleType.PARTICLE);
                    ItemStack result = recipe.getResultItem().copy();
                    result.setCount(stack.getCount());
                    itemEntity.setItem(result);
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public static IManaInfusionRecipe getMatchingRecipe(@Nonnull ItemStack stack, Level level) {
        List<IManaInfusionRecipe> matchingNonCatRecipes = new ArrayList<>();
        List<IManaInfusionRecipe> matchingCatRecipes = new ArrayList<>();

        for (IManaInfusionRecipe recipe : TilePool.manaInfusionRecipes(level)) {
            if (recipe.matches(stack)) {
                if (recipe.getRecipeCatalyst() == null) {
                    matchingNonCatRecipes.add(recipe);
                } else if (recipe.getRecipeCatalyst().test(ModBlocks.alchemyCatalyst.defaultBlockState())) {
                    matchingCatRecipes.add(recipe);
                }
            }
        }

        // Recipes with matching catalyst take priority above recipes with no catalyst specified
        return !matchingCatRecipes.isEmpty() ? matchingCatRecipes.get(0) : !matchingNonCatRecipes.isEmpty() ? matchingNonCatRecipes.get(0) : null;
    }
}
