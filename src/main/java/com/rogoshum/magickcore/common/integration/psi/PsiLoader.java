package com.rogoshum.magickcore.common.integration.psi;

import com.google.common.collect.Queues;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.common.entity.projectile.ManaElementOrbEntity;
import com.rogoshum.magickcore.common.event.AdvancementsEvent;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.init.ModEntities;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.integration.AdditionLoader;
import com.rogoshum.magickcore.common.integration.botania.BotaniaAbility;
import com.rogoshum.magickcore.common.lib.LibElements;
import com.rogoshum.magickcore.common.lib.LibRegistry;
import com.rogoshum.magickcore.api.magick.MagickElement;
import com.rogoshum.magickcore.api.registry.MagickRegistry;
import com.rogoshum.magickcore.api.registry.elementmap.ElementFunctions;
import com.rogoshum.magickcore.common.tileentity.MagickCraftingTileEntity;
import com.rogoshum.magickcore.common.tileentity.RadianceCrystalTileEntity;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegistryObject;

import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.*;
import vazkii.psi.api.spell.PreSpellCastEvent;
import vazkii.psi.common.core.handler.PlayerDataHandler;
import vazkii.psi.common.item.component.ItemCADComponent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PsiLoader extends AdditionLoader {
    public static final RegistryObject<Item> BULLET = ModItems.ITEMS.register("spell_bullet", ItemSpellBullet::new);
    public static final ConcurrentHashMap<Player, ItemStack> CAD = new ConcurrentHashMap<>();
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

            orb.setPos(vec.x + event.getEntityLiving().getLookAngle().x * 3, vec.y + event.getEntityLiving().getEyeHeight() + event.getEntityLiving().getLookAngle().y * 2, vec.z + event.getEntityLiving().getLookAngle().z * 3);
            orb.spellContext().element(ModElements.PSI);
            orb.setOrbType(true);
            orb.spellContext().tick(200);
            orb.setCaster(event.getEntityLiving());
            event.getEntityLiving().level.addFreshEntity(orb);
            if(event.getEntityLiving() instanceof ServerPlayer) {
                AdvancementsEvent.STRING_TRIGGER.trigger((ServerPlayer) event.getEntityLiving(), "element_energy_psi");
            }
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

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        CAD.remove(event.getPlayer());
    }

    @SubscribeEvent
    public void onPsiRegen(RegenPsiEvent event) {
        if(RadianceCrystalTileEntity.RADIANCE_PLAYER.containsKey(event.getPlayer())) {
            HashSet<RadianceCrystalTileEntity> list = RadianceCrystalTileEntity.RADIANCE_PLAYER.get(event.getPlayer());
            list.stream().filter(crystal -> crystal.getElement()==ModElements.PSI && crystal.getApplyType() == ApplyType.RADIANCE).forEach(crystal -> {
                event.addRegen(10);
                event.getCad().getOrCreateTag().putUUID("owner_uuid", event.getPlayer().getUUID());
            });
        }
    }

    @SubscribeEvent
    public void onCADStatEvent(CADStatEvent event) {
        for(Player player : RadianceCrystalTileEntity.RADIANCE_PLAYER.keySet()) {
            if(event.getCad().getOrCreateTag().contains("owner_uuid") && event.getCad().getTag().getUUID("owner_uuid").equals(player.getUUID())) {
                HashSet<RadianceCrystalTileEntity> list = RadianceCrystalTileEntity.RADIANCE_PLAYER.get(player);
                list.stream().filter(crystal -> crystal.getElement()==ModElements.PSI && crystal.getApplyType() == ApplyType.RADIANCE).forEach(crystal -> {
                    ItemStack cad = event.getCad();
                    ICAD cadItem = (ICAD)cad.getItem();
                    ItemStack component = cadItem.getComponentInSlot(cad, event.getStatProvider());
                    if (component.getItem() instanceof ItemCADComponent) {
                        int value = ((ItemCADComponent)component.getItem()).getCADStatValue(component, event.getStat())*3;
                        if(event.getStat() == EnumCADStat.EFFICIENCY && cadItem.getComponentInSlot(cad, EnumCADComponent.ASSEMBLY).getItem() == vazkii.psi.common.item.base.ModItems.cadAssemblyIvory)
                            value*=3;
                        if(event.getStat() == EnumCADStat.POTENCY && cadItem.getComponentInSlot(cad, EnumCADComponent.ASSEMBLY).getItem() == vazkii.psi.common.item.base.ModItems.cadAssemblyEbony)
                            value*=3;
                        if(event.getStatValue() < value)
                            event.setStatValue(value);
                    }
                });
            }
        }
    }
}
