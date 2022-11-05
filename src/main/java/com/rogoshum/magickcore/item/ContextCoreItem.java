package com.rogoshum.magickcore.item;

import com.rogoshum.magickcore.client.item.ManaEnergyRenderer;
import com.rogoshum.magickcore.entity.pointed.ContextCreatorEntity;
import com.rogoshum.magickcore.entity.pointed.ManaCapacityEntity;
import com.rogoshum.magickcore.init.ManaMaterials;
import com.rogoshum.magickcore.init.ModEntities;
import com.rogoshum.magickcore.lib.LibMaterial;
import com.rogoshum.magickcore.tool.NBTTagHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;

public class ContextCoreItem extends BaseItem{
    public ContextCoreItem() {
        super(properties().maxStackSize(8).setISTER(() -> ManaEnergyRenderer::new));
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if(entity.ticksExisted > 20) {
            boolean upGround = true;
            for (int i = 0; i < 5; ++i) {
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
                    ContextCreatorEntity contextCreator = ModEntities.context_creator.get().create(entity.world);
                    if(createEntity instanceof ContextCreatorEntity)
                        contextCreator = (ContextCreatorEntity) createEntity;
                    contextCreator.setPosition(entity.getPosX(), entity.getPosY() - 3, entity.getPosZ());
                    entity.world.addEntity(contextCreator);
                }
            }
        }

        return false;
    }
}
