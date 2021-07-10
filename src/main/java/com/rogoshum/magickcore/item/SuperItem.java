package com.rogoshum.magickcore.item;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.EnumManaType;
import com.rogoshum.magickcore.api.EnumTargetType;
import com.rogoshum.magickcore.api.IManaItem;
import com.rogoshum.magickcore.capability.IEntityState;
import com.rogoshum.magickcore.capability.IManaItemData;
import com.rogoshum.magickcore.entity.ManaRuneEntity;
import com.rogoshum.magickcore.entity.superentity.*;
import com.rogoshum.magickcore.helper.MagickReleaseHelper;
import com.rogoshum.magickcore.helper.RoguelikeHelper;
import com.rogoshum.magickcore.init.ModElements;
import com.rogoshum.magickcore.init.ModEntites;
import com.rogoshum.magickcore.lib.LibElements;
import com.rogoshum.magickcore.lib.LibItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class SuperItem extends BaseItem {
    public SuperItem() {
        super(BaseItem.properties.maxStackSize(1));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if(!worldIn.isRemote) {
            List<Entity> list = playerIn.world.getEntitiesWithinAABB(ManaRuneEntity.class, playerIn.getBoundingBox().grow(16));

            if(list.size() >= 3) {
                Vector3d position = list.get(0).getPositionVec().scale(1d / 3d).add(list.get(1).getPositionVec().scale(1d / 3d)).add(list.get(2).getPositionVec().scale(1d / 3d));
                list.get(0).remove();
                list.get(1).remove();
                list.get(2).remove();
                IEntityState state = playerIn.getCapability(MagickCore.entityState).orElse(null);
                if (state.getElement().getType() == LibElements.SOLAR) {
                    MagickReleaseHelper.releasePointEntity(ModEntites.radiance_wall, playerIn, position, ModElements.getElement(LibElements.SOLAR), null, 0, (int) state.getManaValue()
                            , 0, EnumTargetType.NONE, EnumManaType.NONE);
                } else if (state.getElement().getType() == LibElements.ARC) {
                    MagickReleaseHelper.releasePointEntity(ModEntites.chaos_reach, playerIn, position.add(0, 1, 0), ModElements.getElement(LibElements.ARC), null, 0, (int) state.getManaValue()
                            , 0, EnumTargetType.NONE, EnumManaType.NONE);
                } else if (state.getElement().getType() == LibElements.VOID) {
                    DawnWardEntity orb = new DawnWardEntity(ModEntites.mana_shield, worldIn);
                    MagickReleaseHelper.releasePointEntity(ModEntites.mana_shield, playerIn, position.add(0, -orb.getHeight() / 2, 0), ModElements.getElement(LibElements.VOID), null, 0, (int) state.getManaValue()
                            , 0, EnumTargetType.NONE, EnumManaType.NONE);
                } else if (state.getElement().getType() == LibElements.STASIS) {
                    MagickReleaseHelper.releasePointEntity(ModEntites.silence_squall, playerIn, position.add(0, 2, 0), ModElements.getElement(LibElements.STASIS), null, 0, (int) state.getManaValue()
                            , 0, EnumTargetType.NONE, EnumManaType.NONE);
                } else if (state.getElement().getType() == LibElements.WITHER) {
                    MagickReleaseHelper.releasePointEntity(ModEntites.thorns_caress, playerIn, position.add(0, 1, 0), ModElements.getElement(LibElements.WITHER), null, 0, (int) state.getManaValue()
                            , 0, EnumTargetType.NONE, EnumManaType.NONE);
                } else if (state.getElement().getType() == LibElements.TAKEN) {
                    MagickReleaseHelper.releasePointEntity(ModEntites.ascendant_realm, playerIn, position.add(0, 0, 0), ModElements.getElement(LibElements.TAKEN), null, 0, (int) state.getManaValue()
                            , 0, EnumTargetType.NONE, EnumManaType.NONE);
                }
            }
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent(LibItem.SUPER_D));
    }
}
