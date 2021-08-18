package com.rogoshum.magickcore.item;

import com.rogoshum.magickcore.magick.lifestate.repeater.LifeRepeater;

import java.util.concurrent.Callable;

public class LifeRepeaterItem extends BaseItem {
    private final Callable<? extends LifeRepeater> repeater;
    public LifeRepeaterItem(Callable<? extends LifeRepeater> repeater) {
        super(properties.maxStackSize(16));
        this.repeater = repeater;
    }

    public LifeRepeater getRepeater(){
        LifeRepeater repeater = null;
        try{
            repeater = this.repeater.call();
        }
        catch(Exception e){

        }
        return repeater;
    }
}
