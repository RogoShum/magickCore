package com.rogoshum.magickcore.common.item.material;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.enums.ParticleType;
import com.rogoshum.magickcore.api.mana.ISpellContext;
import com.rogoshum.magickcore.api.mana.IManaMaterial;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.entity.BlockLinkLightEntity;
import com.rogoshum.magickcore.common.event.AdvancementsEvent;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.init.ModEntities;
import com.rogoshum.magickcore.common.init.ModSounds;
import com.rogoshum.magickcore.common.item.BaseItem;
import com.rogoshum.magickcore.api.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.item.ElementContainerItem;
import com.rogoshum.magickcore.common.lib.LibAdvancements;
import com.rogoshum.magickcore.common.lib.LibElements;
import com.rogoshum.magickcore.common.lib.LibItem;
import com.rogoshum.magickcore.api.magick.MagickElement;
import com.rogoshum.magickcore.api.registry.MagickRegistry;
import com.rogoshum.magickcore.api.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.common.util.ParticleUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class ElementItem extends BaseItem implements IManaMaterial {
    private final String element;
    public ElementItem(String element) {
        super(properties().stacksTo(64));
        this.element = element;
    }

    public String getElementType() {
        return element;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity playerIn) {
        if(!worldIn.isClientSide) {
            EntityStateData state = ExtraDataUtil.entityStateData(playerIn);
            if(!state.getElement().type().equals(element)) {
                MagickElement element1 = MagickRegistry.getElement(element);
                state.setElement(element1);
                stack.shrink(1);
                ParticleUtil.spawnBlastParticle(worldIn, playerIn.position().add(0, playerIn.getBbHeight() * 0.5, 0), 3, element1, ParticleType.PARTICLE);
                playerIn.level.playSound(null, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, playerIn.getSoundSource(), 1.5f, 1.0f);

                if(playerIn instanceof ServerPlayer) {
                    AdvancementsEvent.STRING_TRIGGER.trigger((ServerPlayer) playerIn, LibAdvancements.UNDERSTAND_SPELL);
                }
            }
        }
        return super.finishUsingItem(stack, worldIn, playerIn);
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        EntityStateData state = ExtraDataUtil.entityStateData(playerIn);
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        if(state.getElement().type().equals(element))
            return InteractionResultHolder.pass(itemstack);
        playerIn.startUsingItem(handIn);
        return InteractionResultHolder.consume(itemstack);
    }

    @Override
    public InteractionResult useOn(UseOnContext p_41427_) {
        BlockState state = p_41427_.getLevel().getBlockState(p_41427_.getClickedPos());
        int intensity = state.getLightEmission(p_41427_.getLevel(), p_41427_.getClickedPos());
        if(!state.isAir() && intensity > 0) {
            if(p_41427_.getLevel().isClientSide()) {
                MagickElement element1 = MagickRegistry.getElement(getElementType());
                for(int i = 0; i < 10; ++i) {
                    LitParticle litPar = new LitParticle(p_41427_.getLevel(), element1.getRenderer().getParticleTexture()
                            , new Vec3(MagickCore.getNegativeToOne() * 0.5, MagickCore.getNegativeToOne() * 0.5, MagickCore.getNegativeToOne() * 0.5)
                            .add(Vec3.atCenterOf(p_41427_.getClickedPos()))
                            , 0.25f, 0.25f, 0.6f, 10, element1.getRenderer());
                    litPar.setGlow();
                    litPar.setParticleGravity(0f);
                    litPar.setShakeLimit(5.0f);
                    MagickCore.addMagickParticle(litPar);
                }
            } else {
                BlockLinkLightEntity light = ModEntities.LIGHT.get().create(p_41427_.getLevel());
                light.setPos(Vec3.atCenterOf(p_41427_.getClickedPos()));
                light.setElement(getElementType());
                light.setIntensity(intensity);
                p_41427_.getLevel().addFreshEntity(light);
                light.playSound(ModSounds.glitter.get(), 1.0f, 1.0f);
            }
        }
        return super.useOn(p_41427_);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack p_41454_) {
        return 10;
    }

    @Override
    public boolean singleMaterial() {
        return true;
    }

    @Override
    public boolean elementMaterial() {
        return true;
    }

    @Override
    public Component getName(ItemStack p_41458_) {
        return ElementContainerItem.withElementColor(super.getName(p_41458_), element);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(new TranslatableComponent(LibItem.CONTEXT_MATERIAL));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return false;
    }

    @Override
    public boolean disappearAfterRead() {
        return false;
    }

    @Override
    public int getManaNeed(ItemStack stack) {
        return 50;
    }

    @Override
    public boolean upgradeManaItem(ItemStack stack, ISpellContext data) {
        if(!data.spellContext().element().type().equals(element)){
            data.spellContext().element(MagickRegistry.getElement(element));
            return true;
        }
        return false;
    }
}
