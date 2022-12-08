package com.rogoshum.magickcore.common.item;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.mana.IManaMaterial;
import com.rogoshum.magickcore.client.item.ManaEnergyRenderer;
import com.rogoshum.magickcore.common.entity.pointed.ContextCreatorEntity;
import com.rogoshum.magickcore.common.event.AdvancementsEvent;
import com.rogoshum.magickcore.common.init.ManaMaterials;
import com.rogoshum.magickcore.common.init.ModEntities;
import com.rogoshum.magickcore.common.lib.LibAdvancements;
import com.rogoshum.magickcore.common.lib.LibItem;
import com.rogoshum.magickcore.common.lib.LibMaterial;
import com.rogoshum.magickcore.common.magick.materials.Material;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ContextCoreItem extends BaseItem{
    public ContextCoreItem() {
        super(properties().maxStackSize(8).setISTER(() -> ManaEnergyRenderer::new));
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if(entity.ticksExisted > 20) {
            boolean upGround = true;
            for (int i = 0; i < 2; ++i) {
                if(!entity.world.isAirBlock(entity.getPosition().add(0, -i, 0)))
                    upGround = false;
            }
            double speed = 0.043;
            if(!upGround)
                entity.addVelocity(0, speed, 0);
            else {
                entity.remove();
                if(!entity.world.isRemote) {
                    Entity createEntity = NBTTagHelper.createEntityByItem(stack, entity.world);
                    ContextCreatorEntity contextCreator = ModEntities.CONTEXT_CREATOR.get().create(entity.world);
                    if(createEntity instanceof ContextCreatorEntity)
                        contextCreator = (ContextCreatorEntity) createEntity;
                    contextCreator.setPosition(entity.getPosX(), entity.getPosY() - 0.5, entity.getPosZ());
                    entity.world.addEntity(contextCreator);
                    entity.playSound(SoundEvents.BLOCK_BEACON_ACTIVATE, 0.5f, 2.0f);
                }
            }
        }

        return false;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        Material material = ManaMaterials.getMaterial(LibMaterial.ORIGIN);
        if(worldIn != null) {
            Entity entity = NBTTagHelper.createEntityByItem(stack, worldIn);
            if(entity instanceof ContextCreatorEntity) {
                ContextCreatorEntity contextCreator = (ContextCreatorEntity) entity;
                material = contextCreator.getMaterial();
            }
        }
        tooltip.add((new TranslationTextComponent(LibItem.MATERIAL)).appendString(" ").append(new TranslationTextComponent(MagickCore.MOD_ID + ".material." + material.getName())));
        tooltip.add((new TranslationTextComponent(LibItem.FORCE)).appendString(" ").append(new StringTextComponent(String.valueOf(material.getForce()))));
        tooltip.add((new TranslationTextComponent(LibItem.RANGE)).appendString(" ").append(new StringTextComponent(String.valueOf(material.getRange()))));
        tooltip.add((new TranslationTextComponent(LibItem.TICK)).appendString(" ").append(new StringTextComponent(String.valueOf(material.getTick()))));
    }

    @Override
    public void inventoryTick(ItemStack p_77663_1_, World p_77663_2_, Entity p_77663_3_, int p_77663_4_, boolean p_77663_5_) {
        super.inventoryTick(p_77663_1_, p_77663_2_, p_77663_3_, p_77663_4_, p_77663_5_);
        if(p_77663_3_ instanceof ServerPlayerEntity) {
            AdvancementsEvent.STRING_TRIGGER.trigger((ServerPlayerEntity) p_77663_3_, LibAdvancements.CONTEXT_CORE);
        }
    }
}
