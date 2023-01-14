package com.rogoshum.magickcore.client.entity.easyrender;

import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.entity.projectile.ManaElementOrbEntity;

import java.util.HashMap;
import java.util.function.Consumer;

public class ElementOrbRenderer extends EasyRenderer<ManaElementOrbEntity> {
    public ElementOrbRenderer(ManaElementOrbEntity entity) {
        super(entity);
    }

    @Override
    protected void updateSpellContext() {
        super.updateSpellContext();
        String[] strings = new String[1];
        String mana = "§7Mana: " + entity.manaCapacity().getMana();
        if(contextLength < mana.length())
            contextLength = mana.length();
        strings[0] = mana;
        if(debugSpellContext != null) {
            strings = new String[debugSpellContext.length+2];
            System.arraycopy(debugSpellContext, 0, strings, 0, debugSpellContext.length);
            strings[debugSpellContext.length] = "";
            strings[debugSpellContext.length+1] = mana;
        }
        debugSpellContext = strings;
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        return null;
    }
}
