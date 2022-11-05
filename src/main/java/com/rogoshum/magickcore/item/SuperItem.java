package com.rogoshum.magickcore.item;

import com.rogoshum.magickcore.enums.EnumManaLimit;
import com.rogoshum.magickcore.enums.ApplyType;
import com.rogoshum.magickcore.magick.MagickElement;
import com.rogoshum.magickcore.magick.context.MagickContext;
import com.rogoshum.magickcore.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.init.ModEntities;
import com.rogoshum.magickcore.lib.LibElements;
import com.rogoshum.magickcore.lib.LibItem;
import com.rogoshum.magickcore.magick.context.child.PositionContext;
import com.rogoshum.magickcore.magick.context.child.SpawnContext;
import com.rogoshum.magickcore.magick.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.tool.ExtraDataHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class SuperItem extends BaseItem {
    public SuperItem() {
        super(BaseItem.properties().maxStackSize(1));
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity playerIn) {
        if(!worldIn.isRemote) {
            EntityStateData state = ExtraDataHelper.entityStateData(playerIn);
            int mana = (int) state.getManaValue();
            if(playerIn instanceof PlayerEntity && ((PlayerEntity) playerIn).isCreative())
                mana = EnumManaLimit.MAX_MANA.getValue();
            Vector3d position = playerIn.getPositionVec();
            PositionContext positionContext = PositionContext.create(playerIn.getPositionVec());
            SpawnContext spawnContext = new SpawnContext();
            MagickElement element = ExtraDataHelper.entityStateData(playerIn).getElement();
            MagickContext context = MagickContext.create(worldIn).caster(playerIn).applyType(ApplyType.SPAWN_ENTITY);
            context.addChild(spawnContext);
            context.tick(mana).element(element);
            switch (state.getElement().type()) {
                case LibElements.SOLAR: {
                    positionContext.pos = position;
                    context.addChild(positionContext);
                    spawnContext.entityType = ModEntities.radiance_wall.get();
                    break;
                }
                case LibElements.ARC: {
                    positionContext.pos = position.add(0, 1, 0);
                    context.addChild(positionContext);
                    spawnContext.entityType = ModEntities.chaos_reach.get();
                    break;
                }
                case LibElements.VOID: {
                    positionContext.pos = position.add(0, -4, 0);
                    context.addChild(positionContext);
                    spawnContext.entityType = ModEntities.mana_shield.get();
                    break;
                }
                case LibElements.STASIS: {
                    positionContext.pos = position.add(0, 2, 0);
                    context.addChild(positionContext);
                    spawnContext.entityType = ModEntities.silence_squall.get();
                    break;
                }
                case LibElements.WITHER: {
                    positionContext.pos = position.add(0, 1, 0);
                    context.addChild(positionContext);
                    spawnContext.entityType = ModEntities.thorns_caress.get();
                    break;
                }
                case LibElements.TAKEN: {
                    positionContext.pos = position;
                    context.addChild(positionContext);
                    spawnContext.entityType = ModEntities.ascendant_realm.get();
                    break;
                }
            }
            MagickReleaseHelper.releaseMagick(context);
        }
        return super.onItemUseFinish(stack, worldIn, playerIn);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        playerIn.setActiveHand(handIn);
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 30;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent(LibItem.SUPER_D));
    }
}
