package com.rogoshum.magickcore.common.item.material;

import com.rogoshum.magickcore.api.mana.ISpellContext;
import com.rogoshum.magickcore.api.mana.IManaMaterial;
import com.rogoshum.magickcore.common.item.BaseItem;
import com.rogoshum.magickcore.common.lib.LibEntityData;
import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.lib.LibItem;
import com.rogoshum.magickcore.common.registry.MagickRegistry;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ElementItem extends BaseItem implements IManaMaterial {
    private final String element;
    public ElementItem(String element) {
        super(properties().stacksTo(16));
        this.element = element;
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if(!worldIn.isClientSide) {
            ExtraDataUtil.entityData(playerIn).<EntityStateData>execute(LibEntityData.ENTITY_STATE, state -> {
                if(!state.getElement().type().equals(element)) {
                    state.setElement(MagickRegistry.getElement(element));
                    playerIn.getItemInHand(handIn).shrink(1);
                    playerIn.level.playSound(null, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, playerIn.getSoundSource(), 1.5f, 1.0f);
                }
            });
        }
        return super.use(worldIn, playerIn, handIn);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent(LibItem.CONTEXT_MATERIAL));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return false;
    }

    @Override
    public boolean disappearAfterRead() {
        return true;
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
