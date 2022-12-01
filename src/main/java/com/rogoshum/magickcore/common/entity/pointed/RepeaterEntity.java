package com.rogoshum.magickcore.common.entity.pointed;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.entity.base.ManaPointEntity;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.magick.ManaFactor;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import com.rogoshum.magickcore.common.magick.context.child.PositionContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class RepeaterEntity extends ManaPointEntity {
    private static final List<Entity> EMPTY = Collections.emptyList();
    public byte cool_down = 0;
    public RepeaterEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    public void releaseMagick() {
        if(!spellContext().valid()) return;

        if(cool_down >= 0)
            cool_down -= this.spellContext().force * 3 + 1;

        if(cool_down < 0) {
            MagickContext context = MagickContext.create(this.world, spellContext().postContext)
                    .<MagickContext>replenishChild(PositionContext.create(this.getPositionVec()))
                    .caster(getOwner()).projectile(this).noCost();
            if(spellContext().containChild(LibContext.DIRECTION)) {
                context.replenishChild(spellContext().getChild(LibContext.DIRECTION));
            }
            MagickReleaseHelper.releaseMagick(beforeCast(context));
            cool_down = 20;
        }
    }

    @Override
    public void beforeJoinWorld(MagickContext context) {
        //if(context.containChild(LibContext.DIRECTION))
            //spellContext().replenishChild(context.getChild(LibContext.DIRECTION));
    }

    @Override
    public void reSize() {
    }

    @Nonnull
    @Override
    public List<Entity> findEntity(@Nullable Predicate<Entity> predicate) {
        return EMPTY;
    }

    @Override
    public ResourceLocation getEntityIcon() {
        return null;
    }

    @Override
    public ManaFactor getManaFactor() {
        return ManaFactor.DEFAULT;
    }

    @Override
    protected void applyParticle() {
        LitParticle litPar = new LitParticle(this.world, this.spellContext().element.getRenderer().getMistTexture()
                , new Vector3d(this.getPosX()
                , this.getPosY() + this.getHeight() * 0.5
                , this.getPosZ())
                , MagickCore.getRandFloat() * 0.25f, MagickCore.getRandFloat() * 0.25f
                , 0.5f * MagickCore.getRandFloat()
                , this.spellContext().element.getRenderer().getParticleRenderTick(), this.spellContext().element.getRenderer());
        litPar.setGlow();
        litPar.setParticleGravity(0f);
        litPar.setShakeLimit(15.0f);
        litPar.setCanCollide(false);
        litPar.addMotion(MagickCore.getNegativeToOne() * 0.1, MagickCore.getNegativeToOne() * 0.1, MagickCore.getNegativeToOne() * 0.1);
        MagickCore.addMagickParticle(litPar);

        float height = Float.parseFloat(String.format("%.1f", Math.max(0.8f, this.rand.nextFloat()))) * 0.3f;
        float width = Float.parseFloat(String.format("%.1f", Math.max(0.8f, this.rand.nextFloat()))) * 0.3f;
        LitParticle par = new LitParticle(this.world, ModElements.ORIGIN.getRenderer().getParticleTexture()
                , new Vector3d(this.getPosX()
                , this.getPosY() + this.getHeight() * 0.5
                , this.getPosZ())
                , width, height, 0.5f, 15, MagickCore.proxy.getElementRender(spellContext().element.type()));
        par.setGlow();
        par.setParticleGravity(0f);
        par.setLimitScale();
        par.setShakeLimit(15f);
        MagickCore.addMagickParticle(par);
    }
}
