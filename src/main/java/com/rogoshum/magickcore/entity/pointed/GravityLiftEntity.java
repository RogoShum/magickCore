package com.rogoshum.magickcore.entity.pointed;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.entity.base.ManaPointEntity;
import com.rogoshum.magickcore.init.ModElements;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class GravityLiftEntity extends ManaPointEntity {
    public GravityLiftEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Nonnull
    @Override
    public List<Entity> findEntity(@Nullable Predicate<Entity> predicate) {
        return this.world.getEntitiesInAABBexcluding(this, this.getBoundingBox().grow(0, spellContext().range * 2, 0), predicate);
    }

    @Override
    public ResourceLocation getEntityIcon() {
        return null;
    }

    @Override
    protected void applyParticle() {
        double x = getPositionVec().x;
        double y = getPositionVec().y;
        double z = getPositionVec().z;
        for (int i = 0; i < 5; ++i) {
            LitParticle par = new LitParticle(this.world, ModElements.ORIGIN.getRenderer().getParticleSprite()
                    , new Vector3d(MagickCore.getNegativeToOne() * this.getWidth() + x
                    , MagickCore.getNegativeToOne() * this.getWidth() + y
                    , MagickCore.getNegativeToOne() * this.getWidth() + z)
                    , getWidth() * rand.nextFloat(), getWidth() * rand.nextFloat(), 0.1f, (int) (2 * getHeight()), MagickCore.proxy.getElementRender(spellContext().element.type()));
            par.setParticleGravity(-2f);
            par.setLimitScale();
            par.setGlow();
            MagickCore.addMagickParticle(par);
        }
    }

    @Override
    public void reSize() {
        float height = getType().getHeight() + spellContext().range * 2;
        if(getHeight() != height)
            this.setHeight(height);
    }
}
