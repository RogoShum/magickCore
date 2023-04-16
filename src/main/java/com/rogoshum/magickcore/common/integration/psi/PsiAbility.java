package com.rogoshum.magickcore.common.integration.psi;

import com.mojang.datafixers.util.Either;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import com.rogoshum.magickcore.common.magick.context.child.PsiSpellContext;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.spell.*;
import vazkii.psi.common.core.handler.PlayerDataHandler;
import vazkii.psi.common.item.ItemCAD;
import vazkii.psi.common.item.ItemSpellBullet;

public class PsiAbility {
    public static ItemStack spellBullet(ItemStack stack) {
        if(stack.getItem() instanceof ItemSpellBullet && ISpellAcceptor.hasSpell(stack)) {
            ISpellAcceptor spellContainer = ISpellAcceptor.acceptor(stack);
            Spell spell = spellContainer.getSpell();
            SpellContext context = (new SpellContext()).setSpell(spell);
            ItemStack newBullet = new ItemStack(PsiLoader.BULLET.get());
            ExtraDataUtil.itemManaData(newBullet).spellContext().element(ModElements.PSI).applyType(ApplyType.CAST).addChild(PsiSpellContext.create(stack, context.cspell.metadata.getStat(EnumSpellStat.COST)));
            return newBullet;
        }
        return ItemStack.EMPTY;
    }

    public static boolean cast(MagickContext context) {
        if(context.caster instanceof Player player && context.containChild(PsiSpellContext.TYPE)) {
            PlayerDataHandler.PlayerData data = PlayerDataHandler.get(player);
            ItemStack cad = PsiAPI.getPlayerCAD(player);
            PsiSpellContext psiSpell = context.getChild(PsiSpellContext.TYPE);
            if(!ISpellAcceptor.hasSpell(psiSpell.itemStack)) return false;
            return ItemCAD.cast(context.world, player, data, psiSpell.itemStack, cad, 40, 25, 0.5F, (ctx) -> {
                ctx.castFrom = InteractionHand.OFF_HAND;
                ctx.customData.put("magick_context", context);
            }).isPresent();
        }
        return false;
    }
}
