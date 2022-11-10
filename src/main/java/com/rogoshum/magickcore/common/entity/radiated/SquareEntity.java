package com.rogoshum.magickcore.common.entity.radiated;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.entity.base.ManaRadiateEntity;
import com.rogoshum.magickcore.common.magick.ManaFactor;
import com.rogoshum.magickcore.common.util.ParticleUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class SquareEntity extends ManaRadiateEntity {
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/cube.png");
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
        return ICON;
    }

    @Override
    protected void applyParticle() {
        applyParticle(2);
    }

    @Override
    public ManaFactor getManaFactor() {
        return ManaFactor.RADIATE_DEFAULT;
    }

    protected void applyParticle(int particleAge) {
        float scale = 0.5f;
        double width = this.getBoundingBox().grow(spellContext().range).getXSize();
        List<Vector3d> list = ParticleUtil.drawRectangle(this.positionVec().add(0, this.getHeight() / 2, 0), scale, width, width, width);
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
