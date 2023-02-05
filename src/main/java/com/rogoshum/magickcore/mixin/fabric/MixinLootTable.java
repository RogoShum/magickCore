package com.rogoshum.magickcore.mixin.fabric;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.IIDLootTable;
import com.rogoshum.magickcore.common.entity.projectile.ManaElementOrbEntity;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.init.ModEntities;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.lib.LibElements;
import com.rogoshum.magickcore.common.util.LootUtil;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.core.util.NameUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.List;

@Mixin(LootTable.class)
public class MixinLootTable implements IIDLootTable {

    @Inject(method = "getRandomItems(Lnet/minecraft/world/level/storage/loot/LootContext;)Ljava/util/List;", at = @At("RETURN"))
    public void onGetItem(LootContext lootContext, CallbackInfoReturnable<List<ItemStack>> cir) {
        List<ItemStack> stacks = cir.getReturnValue();
        Entity entity = lootContext.getParamOrNull(LootContextParams.THIS_ENTITY);
        if(entity instanceof LivingEntity)
            LootUtil.modifyLivingLoot((LivingEntity) entity, stacks);

        if (getID() != null && getID().toString().contains("chests")) {
            while (MagickCore.rand.nextBoolean()) {
                int lucky = 3;
                while (MagickCore.rand.nextBoolean())
                    lucky++;
                stacks.add(LootUtil.createRandomItemByLucky(lucky));
            }
        }
    }

    ResourceLocation lootTableID;

    @Override
    public void setID(ResourceLocation id) {
        lootTableID = id;
    }

    @Override
    public ResourceLocation getID() {
        return lootTableID;
    }
}
