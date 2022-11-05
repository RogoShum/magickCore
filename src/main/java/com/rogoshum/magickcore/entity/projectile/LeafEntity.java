package com.rogoshum.magickcore.entity.projectile;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.entity.easyrender.LeafRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.ManaStarRenderer;
import com.rogoshum.magickcore.entity.base.ManaProjectileEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class LeafEntity extends ManaProjectileEntity {
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/leaf_1.png");
    private static final DataParameter<Integer> NUMBER = EntityDataManager.createKey(LeafEntity.class, DataSerializers.VARINT);
    public LeafEntity(EntityType<? extends ThrowableEntity> type, World worldIn) {
        super(type, worldIn);
        this.dataManager.register(NUMBER, rand.nextInt(3));
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        MagickCore.proxy.addRenderer(new LeafRenderer(this));
    }

    public void setNumber(int number) {
        this.getDataManager().set(NUMBER, number);
    }

    public int getNumber() {
        return this.getDataManager().get(NUMBER);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public float getSourceLight() {
        return 3;
    }

    @Override
    protected void applyParticle() {

    }

    @Override
    public ResourceLocation getEntityIcon() {
        return ICON;
    }
}
