package com.rogoshum.magickcore.common.integration.psi;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.common.buff.ManaBuff;
import com.rogoshum.magickcore.common.entity.projectile.ManaElementOrbEntity;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.init.ModEntities;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.integration.AdditionLoader;
import com.rogoshum.magickcore.common.lib.LibBuff;
import com.rogoshum.magickcore.common.lib.LibElements;
import com.rogoshum.magickcore.common.lib.LibRegistry;
import com.rogoshum.magickcore.common.magick.MagickElement;
import com.rogoshum.magickcore.common.registry.MagickRegistry;
import com.rogoshum.magickcore.common.registry.elementmap.ElementFunctions;
import com.rogoshum.magickcore.common.tileentity.MagickCraftingTileEntity;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegistryObject;

import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.spell.PreSpellCastEvent;
import vazkii.psi.common.core.handler.PlayerDataHandler;

import java.util.HashMap;

public class PsiLoader extends AdditionLoader {
    public static final RegistryObject<Item> BULLET = ModItems.ITEMS.register("spell_bullet", ItemSpellBullet::new);

    @Override
    public void onLoad(IEventBus eventBus) {
        PsiAPI.registerSpellPieceAndTexture(MagickCore.fromId("operator_mana_in"), PieceOperatorManaIn.class);
        PsiAPI.registerSpellPieceAndTexture(MagickCore.fromId("operator_mana_target"), PieceOperatorManaTarget.class);
        PsiAPI.registerSpellPieceAndTexture(MagickCore.fromId("operator_mana_direction"), PieceOperatorManaDirection.class);
        PsiAPI.registerSpellPieceAndTexture(MagickCore.fromId("operator_mana_position"), PieceOperatorManaPosition.class);
        PsiAPI.registerSpellPieceAndTexture(MagickCore.fromId("trick_mana_out"), PieceTrickManaOut.class);

        MagickCraftingTileEntity.addAdditionTransform(PsiAbility::spellBullet);

        HashMap<String, ElementFunctions> registry = MagickRegistry.<ElementFunctions>getRegistry(LibRegistry.ELEMENT_FUNCTION).registry();
        ElementFunctions functions = ElementFunctions.create();
        functions.add(ApplyType.CAST, PsiAbility::cast);
        registry.put(LibElements.PSI, functions);
        HashMap<String, MagickElement> elements = MagickRegistry.<MagickElement>getRegistry(LibRegistry.ELEMENT).registry();
        elements.put(LibElements.PSI, ModElements.PSI);
        ModElements.elements.add(LibElements.PSI);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onLivingDamageEvent(LivingDamageEvent event) {
        if(!event.getEntity().level.isClientSide() && event.getSource() == PlayerDataHandler.damageSourceOverload) {
            ManaElementOrbEntity orb = new ManaElementOrbEntity(ModEntities.ELEMENT_ORB.get(), event.getEntityLiving().level);
            Vec3 vec = event.getEntityLiving().position();
            orb.setPos(vec.x + MagickCore.getNegativeToOne() * 2, vec.y + event.getEntityLiving().getBbHeight() * .5 + MagickCore.getNegativeToOne() * 2, vec.z + MagickCore.getNegativeToOne() * 2);
            orb.spellContext().element(ModElements.PSI);
            orb.setOrbType(true);
            orb.spellContext().tick(200);
            orb.setCaster(event.getEntityLiving());
            event.getEntityLiving().level.addFreshEntity(orb);
        }
    }

    @SubscribeEvent
    public void onPreSpellCastEvent(PreSpellCastEvent event) {
        if(event.getCost() > 0) {
            float scale = 1.0f;
            for (ItemStack stack : event.getPlayer().getAllSlots()) {
                if(NBTTagHelper.hasElementOnTool(stack, LibElements.PSI)) {
                    NBTTagHelper.consumeElementOnTool(stack, LibElements.PSI, 2);
                    scale-=0.15f;
                }
            }
            event.setCost((int) (event.getCost()*scale));
        }
    }
}
