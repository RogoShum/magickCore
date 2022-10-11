package com.rogoshum.magickcore.entity.radiated;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.entity.base.ManaRadiateEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class SphereEntity extends ManaRadiateEntity {
    public final Predicate<Entity> inSphere = (entity ->
            this.getDistanceSq(entity.getPositionVec().add(0, entity.getHeight() / 2, 0))
                    <= spellContext().range * spellContext().range);
    public SphereEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    protected void applyParticle() {
        applyParticle(2);
    }

    @Override
    public void successFX() {
        applyParticle(20);
    }

    @Nonnull
    @Override
    public List<Entity> findEntity(@Nullable Predicate<Entity> predicate) {
        return this.world.getEntitiesInAABBexcluding(this, this.getBoundingBox().grow(spellContext().range),
                predicate != null ? predicate.and(inSphere)
                        : inSphere);
    }

    @Override
    public ResourceLocation getEntityIcon() {
        return null;
    }

    protected void applyParticle(int particleAge) {
        float radius = spellContext().range;
        float rho, drho, theta, dtheta;
        float x, y, z;
        int stacks = Math.max((int) (2 * spellContext().range), 8);
        drho = (float) (2.0f * Math.PI / stacks);
        dtheta = (float) (2.0f * Math.PI / stacks);
        for (int i = 0; i < stacks; i++) {
            rho = i * drho;
            for (int j = 0; j < stacks; j++) {
                theta = j * dtheta;
                x = (float) (-Math.sin(theta) * Math.sin(rho));
                y = (float) (Math.cos(theta) * Math.sin(rho));
                z = (float) Math.cos(rho);

                Vector3d pos = new Vector3d(x * radius, y * radius, z * radius);
                LitParticle par = new LitParticle(this.world, MagickCore.proxy.getElementRender(spellContext().element.type()).getParticleTexture()
                        , pos.add(this.getPositionVec())
                        , 0.1f, 0.1f, 1.0f, particleAge, MagickCore.proxy.getElementRender(spellContext().element.type()));
                par.setGlow();
                par.setParticleGravity(0);
                par.setLimitScale();
                par.addMotion(x * 0.2, y * 0.2, z * 0.2);
                MagickCore.addMagickParticle(par);
            }
        }
    }
}
