package com.rogoshum.magickcore.magick.element;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.IManaAbility;
import com.rogoshum.magickcore.api.IManaElement;
import com.rogoshum.magickcore.api.event.ElementEvent;
import com.rogoshum.magickcore.client.element.ElementRenderer;
import com.rogoshum.magickcore.enums.EnumManaType;
import com.rogoshum.magickcore.tool.MagickReleaseHelper;
import com.rogoshum.magickcore.lib.LibElements;
import com.rogoshum.magickcore.magick.ElementFunction;
import net.minecraft.util.DamageSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;

public abstract class MagickElement implements IManaElement {
    private String type = LibElements.ORIGIN;
    private ElementAbility ability;

    public MagickElement(String type, ElementAbility ability)
    {
        this.type = type;
        this.ability = ability;
        registryFunction();
    }

    private void registryFunction() {
        ElementFunction function = ElementFunction.create().add(EnumManaType.ATTACK, this.getAbility()::damageEntity)
                .add(EnumManaType.BUFF, this.getAbility()::applyBuff)
                .add(EnumManaType.DEBUFF, this.getAbility()::applyDebuff)
                .add(EnumManaType.HIT, this.getAbility()::hitEntity);
        ElementEvent.ElementFunctionRegistryEvent event = new ElementEvent.ElementFunctionRegistryEvent(function);
        MinecraftForge.EVENT_BUS.post(event);
        MagickReleaseHelper.registryElementFunction(this, event.getFunction());
    }

    public String getType()
    {
        return this.type;
    }

    @OnlyIn(Dist.CLIENT)
    public ElementRenderer getRenderer()
    {
        return MagickCore.proxy.getElementRender(this.type);
    }

    public ElementAbility getAbility()
    {
        return this.ability;
    }

    public static abstract class ElementAbility implements IManaAbility
    {
        private DamageSource damage;

        ElementAbility(DamageSource damage)
        {
            this.damage = damage;
        }

        public DamageSource getDamageSource() { return this.damage; }
    }
}
