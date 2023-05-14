package com.rogoshum.magickcore.common.item.material;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.enums.ParticleType;
import com.rogoshum.magickcore.api.mana.ISpellContext;
import com.rogoshum.magickcore.api.mana.IManaMaterial;
import com.rogoshum.magickcore.common.event.AdvancementsEvent;
import com.rogoshum.magickcore.common.item.BaseItem;
import com.rogoshum.magickcore.api.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.lib.LibAdvancements;
import com.rogoshum.magickcore.common.lib.LibItem;
import com.rogoshum.magickcore.api.magick.MagickElement;
import com.rogoshum.magickcore.api.registry.MagickRegistry;
import com.rogoshum.magickcore.api.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.util.ParticleUtil;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.level.Level;

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
        if(!data.spellContext().element.type().equals(element)){
            data.spellContext().element(MagickRegistry.getElement(element));
            return true;
        }
        return false;
    }
}
