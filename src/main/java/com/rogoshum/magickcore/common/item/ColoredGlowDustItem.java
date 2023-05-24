package com.rogoshum.magickcore.common.item;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.entity.BlockLinkLightEntity;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.init.ModEntities;
import com.rogoshum.magickcore.common.init.ModSounds;
import com.rogoshum.magickcore.common.lib.LibElements;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ColoredGlowDustItem extends BaseItem implements DyeableLeatherItem {
    public ColoredGlowDustItem() {
        super(properties());
    }

    @Override
    public InteractionResult useOn(UseOnContext p_41427_) {
        BlockState state = p_41427_.getLevel().getBlockState(p_41427_.getClickedPos());
        int intensity = state.getLightEmission(p_41427_.getLevel(), p_41427_.getClickedPos());
        if(!state.isAir() && intensity > 0) {
            if(p_41427_.getLevel().isClientSide()) {
                for(int i = 0; i < 10; ++i) {
                    LitParticle litPar = new LitParticle(p_41427_.getLevel(), ModElements.ORIGIN.getRenderer().getParticleTexture()
                            , new Vec3(MagickCore.getNegativeToOne() * 0.5, MagickCore.getNegativeToOne() * 0.5, MagickCore.getNegativeToOne() * 0.5)
                            .add(Vec3.atCenterOf(p_41427_.getClickedPos()))
                            , 0.25f, 0.25f, 0.6f, 10, ModElements.ORIGIN.getRenderer());
                    litPar.setGlow();
                    litPar.setParticleGravity(0f);
                    litPar.setShakeLimit(5.0f);
                    MagickCore.addMagickParticle(litPar);
                }
            } else {
                BlockLinkLightEntity light = ModEntities.LIGHT.get().create(p_41427_.getLevel());
                light.setPos(Vec3.atCenterOf(p_41427_.getClickedPos()));
                light.setElement(LibElements.ORIGIN);
                light.setColor(Color.create(getColor(p_41427_.getItemInHand())));
                light.setIntensity(intensity);
                p_41427_.getLevel().addFreshEntity(light);
                light.playSound(ModSounds.glitter.get(), 1.0f, 1.0f);
            }
        }
        return super.useOn(p_41427_);
    }

    @Override
    public void appendHoverText(ItemStack p_41421_, @Nullable Level p_41422_, List<Component> p_41423_, TooltipFlag p_41424_) {
        super.appendHoverText(p_41421_, p_41422_, p_41423_, p_41424_);
        if(hasCustomColor(p_41421_)) {
            Color color = Color.create(getColor(p_41421_));
            p_41423_.add((new TextComponent("r: ").append(String.valueOf(String.format("%.1f", color.r())))
                    .append(" g: ").append(String.valueOf(String.format("%.1f", color.g())))
                    .append(" b: ").append(String.valueOf(String.format("%.1f", color.b())))).setStyle(Style.EMPTY.withColor(color.decimalColor())));
        }
    }
}
