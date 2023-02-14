package com.rogoshum.magickcore.common.magick.ability;

import com.google.common.collect.Interner;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.api.enums.ParticleType;
import com.rogoshum.magickcore.common.init.ModBuffs;
import com.rogoshum.magickcore.common.init.ModDamages;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.init.ModEntities;
import com.rogoshum.magickcore.common.lib.LibBuff;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.lib.LibElements;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import com.rogoshum.magickcore.common.magick.context.child.ExtraApplyTypeContext;
import com.rogoshum.magickcore.common.magick.context.child.ItemContext;
import com.rogoshum.magickcore.common.magick.context.child.PositionContext;
import com.rogoshum.magickcore.common.magick.context.child.SpawnContext;
import com.rogoshum.magickcore.common.util.ItemStackUtil;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import com.rogoshum.magickcore.common.util.ParticleUtil;
import com.rogoshum.magickcore.mixin.MixinLevelChunk;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.tags.Tag;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;

public class WitherAbility{
    public static boolean hitEntity(MagickContext context) {
        if(context.victim == null) return false;
        if(context.victim instanceof ItemEntity) {
            ItemEntity itemEntity = ((ItemEntity) context.victim);
            Interner<TagKey<?>> itagcollection = ObfuscationReflectionHelper.getPrivateValue(TagKey.class, null, "VALUES");
            AtomicReference<Tag<Item>> iTag = new AtomicReference<>();
            AtomicBoolean ores = new AtomicBoolean(false);
            /*
            itagcollection..forEach((key, itemITag) -> {
                if((key.getPath().contains("ores/") || key.getPath().contains("ingots/")) && itemEntity.getItem().getItem().is(itemITag)) {
                    ResourceLocation res = new ResourceLocation(key.getNamespace(), key.getPath().replace("ingots", "dusts"));
                    if(key.getPath().contains("ores")) {
                        ores.set(true);
                        res = new ResourceLocation(key.getNamespace(), key.getPath().replace("ores", "dusts"));
                    }
                    if(itagcollection.getAllTags().containsKey(res))
                        iTag.set(itagcollection.getAllTags().get(res));
                    else if(key.getPath().contains("ores/")){
                        res = new ResourceLocation(key.getNamespace(), key.getPath().replace("ores", "gems"));
                        if(itagcollection.getAllTags().containsKey(res))
                            iTag.set(itagcollection.getAllTags().get(res));
                    }
                }
            });

             */
            if(iTag.get() == null)
                return false;
            Tag<Item> itemITag = iTag.get();
            Item item = itemEntity.getItem().getItem();
            for (Item tagItem : itemITag.getValues()) {
                if(tagItem.getRegistryName().getNamespace().equals(itemEntity.getItem().getItem().getRegistryName().getNamespace()))
                    item = tagItem;
                else if(!itemITag.getValues().contains(item))
                    item = tagItem;
            }
            if(item == itemEntity.getItem().getItem()) return false;
            CompoundTag nbt = itemEntity.getItem().save(new CompoundTag());
            ResourceLocation resourcelocation = ForgeRegistries.ITEMS.getKey(item);
            nbt.putString("id", resourcelocation.toString());
            ItemStack dustItem = ItemStack.of(nbt);
            if(dustItem.isEmpty()) return false;
            itemEntity.setItem(dustItem);
            if(ores.get()) {
                ItemStack dustCopy = itemEntity.getItem().copy();
                dustItem = ItemStackUtil.mergeStacks(dustItem, dustCopy, 64);
                if(!dustCopy.isEmpty()) {
                    ItemEntity entity = new ItemEntity(itemEntity.level, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), dustCopy);
                    if(!entity.level.isClientSide)
                        entity.level.addFreshEntity(entity);
                }
            }
            if(context.force >= 7) {
                ItemStack dustCopy = itemEntity.getItem().copy();
                dustItem = ItemStackUtil.mergeStacks(dustItem, dustCopy, 64);
                if(!dustCopy.isEmpty()) {
                    ItemEntity entity = new ItemEntity(itemEntity.level, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), dustCopy);
                    if(!entity.level.isClientSide)
                        entity.level.addFreshEntity(entity);
                }
            }
            itemEntity.setItem(dustItem);
            ParticleUtil.spawnBlastParticle(context.world, itemEntity.position().add(0, itemEntity.getBbHeight(), 0), 2, ModElements.WITHER, ParticleType.PARTICLE);
            return true;
        } else
            return ModBuffs.applyBuff(context.victim, LibBuff.WITHER, context.tick, context.force, false);
    }

    public static boolean damageEntity(MagickContext context) {
        if(context.victim == null) return false;
        if(ModBuffs.hasBuff(context.victim, LibBuff.WITHER))
            context.force *= 1.25;

        boolean flag = false;
        if(context.caster != null && context.projectile instanceof Projectile)
            flag = context.victim.hurt(ModDamages.applyProjectileWitherDamage(context.caster, context.projectile), context.force);
        else if(context.caster != null)
            flag = context.victim.hurt(ModDamages.applyEntityWitherDamage(context.caster), context.force);
        else if(context.projectile != null)
            flag = context.victim.hurt(ModDamages.applyEntityWitherDamage(context.projectile), context.force);
        else
            flag = context.victim.hurt(ModDamages.getWitherDamage(), context.force);

        return flag;
    }

    public static boolean hitBlock(MagickContext context) {
        return false;
    }

    public static boolean growBlock(MagickContext context, Block block, BlockPos pos, BlockState state) {
        if (state.getBlock() instanceof BonemealableBlock) {
            BonemealableBlock igrowable = (BonemealableBlock)state.getBlock();
            for (int i = 0; i < context.force; i++) {
                if(igrowable.isValidBonemealTarget((ServerLevel)context.world, pos, state, false))
                    igrowable.performBonemeal((ServerLevel)context.world, context.world.random, pos, context.world.getBlockState(pos));
                spawnLoot(context, block,  pos, context.world.getBlockState(pos), CropBlock.AGE);
            }
            return true;
        } else {
            boolean hasAge = false;
            IntegerProperty integerProperty = null;
            int force = (int) context.force;
            if(containProperty(context.world, pos, BlockStateProperties.AGE_1, force)) {
                hasAge = true;
                integerProperty = BlockStateProperties.AGE_1;
            } else if(containProperty(context.world, pos, BlockStateProperties.AGE_2, force)) {
                hasAge = true;
                integerProperty = BlockStateProperties.AGE_2;
            } else if(containProperty(context.world, pos, BlockStateProperties.AGE_3, force)) {
                hasAge = true;
                integerProperty = BlockStateProperties.AGE_3;
            } else if(containProperty(context.world, pos, BlockStateProperties.AGE_5, force)) {
                hasAge = true;
                integerProperty = BlockStateProperties.AGE_5;
            } else if(containProperty(context.world, pos, BlockStateProperties.AGE_7, force)) {
                hasAge = true;
                integerProperty = BlockStateProperties.AGE_7;
            } else if(containProperty(context.world, pos, BlockStateProperties.AGE_15, force)) {
                hasAge = true;
                integerProperty = BlockStateProperties.AGE_15;
            } else if(containProperty(context.world, pos, BlockStateProperties.AGE_25, force)) {
                hasAge = true;
                integerProperty = BlockStateProperties.AGE_25;
            }
            if(hasAge)
                spawnLoot(context, block, pos, state, integerProperty);
            return hasAge;
        }
    }

    public static boolean containProperty(Level world, BlockPos pos, IntegerProperty integerProperty, int force) {
        BlockState state = world.getBlockState(pos);
        if (state.getProperties().stream().anyMatch(p -> p.equals(integerProperty))) {
            int age = (force + state.getValue(integerProperty));
            Collection<Integer> integers = integerProperty.getPossibleValues();
            if(integers.size() < 1) return false;
            if (!integers.contains(age)) {
                age = (int) integers.toArray()[integers.size() - 1];
            }
            world.setBlockAndUpdate(pos, state.setValue(integerProperty, age));
            return true;
        }
        return false;
    }

    public static void spawnLoot(MagickContext context, Block block, BlockPos pos, BlockState state, IntegerProperty property) {
        if (state.getProperties().stream().anyMatch(p -> p.equals(property))) {
            Collection<Integer> integers = property.getPossibleValues();
            if(state.getValue(property) != (int) integers.toArray()[integers.size() - 1])
                return;
        }
         else
             return;
        LootContext.Builder lootContext = new LootContext.Builder((ServerLevel) context.world)
                .withParameter(LootContextParams.ORIGIN, new Vec3(pos.getX(), pos.getY(), pos.getZ()))
                .withParameter(LootContextParams.BLOCK_STATE, state)
                .withOptionalParameter(LootContextParams.THIS_ENTITY, context.caster);
        lootContext.withParameter(LootContextParams.TOOL, ItemStack.EMPTY);
        List<ItemStack> drops = state.getDrops(lootContext);

        context.world.setBlockAndUpdate(pos, state.setValue(property, 0));

        for (ItemStack stack : drops) {
            ItemEntity entity = new ItemEntity(context.world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
            entity.setPickUpDelay(20);
            context.world.addFreshEntity(entity);
        }
    }

    public static boolean applyBuff(MagickContext context) {
        if(context.victim == null) return false;
        return ModBuffs.applyBuff(context.victim, LibBuff.DECAY, context.tick * 2, context.force, true);
    }

    public static boolean applyDebuff(MagickContext context) {
        if(context.victim == null) return false;
        return ModBuffs.applyBuff(context.victim, LibBuff.CRIPPLE, context.tick, context.force, false);
    }

    public static boolean applyToolElement(MagickContext context) {
        if(!context.containChild(LibContext.ITEM) || context.world.isClientSide()) return false;
        ItemContext itemStack = context.getChild(LibContext.ITEM);
        if(!context.valid()) return false;
        if(itemStack.itemStack.getDamageValue() > 0) {
            NBTTagHelper.consumeElementOnTool(itemStack.itemStack, LibElements.WITHER);
            int maxDamage = 3;
            int damage;
            if(itemStack.itemStack.getDamageValue() <= maxDamage)
                damage = MagickCore.rand.nextInt(itemStack.itemStack.getDamageValue());
            else {
                damage = itemStack.itemStack.getDamageValue() - MagickCore.rand.nextInt(maxDamage);
            }
            itemStack.itemStack.setDamageValue(damage);
        }
        return true;
    }

    public static boolean superEntity(MagickContext context) {
        if(context.caster == null) return false;
        SpawnContext spawnContext = new SpawnContext();
        spawnContext.entityType = ModEntities.THORNS_CARESS.get();
        context.addChild(spawnContext);
        context.applyType(ApplyType.SPAWN_ENTITY);
        return MagickReleaseHelper.releaseMagick(context);
    }

    public static boolean diffusion(MagickContext context) {
        if(context.doBlock) {
            if(context.world instanceof ServerLevel && context.containChild(LibContext.POSITION)) {
                PositionContext positionContext = context.getChild(LibContext.POSITION);
                BlockPos pos = new BlockPos(positionContext.pos);
                BlockEntity tile = context.world.getBlockEntity(pos);
                if(context.world.getBlockState(pos).getBlock() instanceof EntityBlock) {
                    float force = context.force*20;
                    for (int i = 0; i < force; ++i) {
                        ((MixinLevelChunk)context.world.getChunkAt(pos)).invokerUpdateBlockEntityTicker(tile);
                    }
                }
            }
        }
        if(!(context.victim instanceof LivingEntity)) return false;
        float health = Math.min(((LivingEntity) context.victim).getHealth() - 0.1f, context.force * 0.5f);

        ((LivingEntity) context.victim).setHealth(((LivingEntity) context.victim).getHealth() - health);
        ((LivingEntity)context.victim).setAbsorptionAmount(((LivingEntity) context.victim).getAbsorptionAmount() + health);

        return true;
    }

    public static boolean agglomerate(MagickContext context) {
        BlockPos pos = null;

        if(context.containChild(LibContext.POSITION)) {
            PositionContext positionContext = context.getChild(LibContext.POSITION);
            pos = new BlockPos(positionContext.pos);
        }

        if(pos != null && context.doBlock) {
            if(context.world instanceof ServerLevel) {
                BlockState blockstate = context.world.getBlockState(pos);
                if(growBlock(context, blockstate.getBlock(), pos, blockstate)) {
                    context.world.levelEvent(2005, pos, 0);
                    return true;
                }
                blockstate = context.world.getBlockState(pos.above());
                if(growBlock(context, blockstate.getBlock(), pos.above(), blockstate)) {
                    context.world.levelEvent(2005, pos, 0);
                    return true;
                }
            }
        } else if (!context.world.isClientSide && context.victim instanceof AgeableMob) {
            AgeableMob ageable = (AgeableMob) context.victim;
            int i = ageable.getAge();
            if (i < 0) {
                i += context.force * 400;
                ageable.setAge(i);
            }
            return true;
        }
        if(context.world.isClientSide && context.victim instanceof AgeableMob) {
            Vec3 vector3d = context.victim.position().add(0, context.victim.getBbHeight() * 0.5, 0);
            for(int i = 0; i < 15; ++i) {
                double d2 = MagickCore.rand.nextGaussian() * 0.5D;
                double d3 = MagickCore.rand.nextGaussian() * 0.5D;
                double d4 = MagickCore.rand.nextGaussian() * 0.5D;
                double d6 = (double)vector3d.x() + d2 * context.victim.getBbWidth();
                double d7 = (double)vector3d.y() + d3 * context.victim.getBbHeight();
                double d8 = (double)vector3d.z() + d4 * context.victim.getBbWidth();
                context.world.addParticle(ParticleTypes.HAPPY_VILLAGER, d6, d7, d8, d2, d3, d4);
            }
        }
        return false;
    }
}
