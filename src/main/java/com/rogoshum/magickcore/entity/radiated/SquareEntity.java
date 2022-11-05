package com.rogoshum.magickcore.entity.radiated;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.entity.base.ManaRadiateEntity;
import com.rogoshum.magickcore.lib.LibShaders;
import com.rogoshum.magickcore.tool.ParticleHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class SquareEntity extends ManaRadiateEntity {
    public SquareEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    public void successFX() {
        applyParticle(20);
    }

    @Nonnull
    @Override
    public List<Entity> findEntity(@Nullable Predicate<Entity> predicate) {
        return this.world.getEntitiesInAABBexcluding(this, this.getBoundingBox().grow(spellContext().range), predicate);
    }

    @Override
    public ResourceLocation getEntityIcon() {
        return null;
    }

    @Override
    protected void applyParticle() {
        applyParticle(2);
    }

    protected void applyParticle(int particleAge) {
        float scale = 0.5f;
        double width = this.getBoundingBox().grow(spellContext().range).getXSize();
        List<Vector3d> list = ParticleHelper.drawRectangle(this.positionVec().add(0, this.getHeight() / 2, 0), scale, width, width, width);
        for(int i = 0; i < list.size(); ++i) {
            Vector3d pos = list.get(i);
            LitParticle par = new LitParticle(this.world, MagickCore.proxy.getElementRender(spellContext().element.type()).getParticleTexture()
                    , pos
                    , 0.1f, 0.1f, 1.0f, particleAge, MagickCore.proxy.getElementRender(spellContext().element.type()));
            par.setGlow();
            par.setParticleGravity(0);
            par.setLimitScale();
            par.addMotion(MagickCore.getNegativeToOne() * 0.2f, MagickCore.getNegativeToOne() * 0.2f, MagickCore.getNegativeToOne() * 0.2f);
            MagickCore.addMagickParticle(par);
        }
    }
}
