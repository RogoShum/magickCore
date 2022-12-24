package com.rogoshum.magickcore.common.util;

import net.minecraft.util.text.TranslationTextComponent;

public class ToolTipHelper {
    public final static String GREY = "§7";
    public final static String DEEP_GREY = "§8";
    public final static String PINK = "§d";
    public final static String PURPLE = "§5";
    public final static String BLUE = "§9";
    public final StringBuilder builder = new StringBuilder();
    public int tab = 0;

    public String getString() {
        return builder.toString();
    }

    public void nextLine(String s, Object object) {
        nextLine();
        prefix();
        builder.append(s).append(": ").append(object);
    }

    public void nextLine(String s) {
        nextLine();
        prefix();
        builder.append(s);
    }

    public void nextTrans(String s, Object object) {
        nextLine();
        prefix();
        String post = object.toString();
        if(!post.isEmpty())
            builder.append(new TranslationTextComponent(s).getString()).append(": ").append(object);
        else
            builder.append(new TranslationTextComponent(s).getString());
    }

    public void nextTrans(String s, Object object, String prefix, String valuePrefix) {
        nextLine();
        prefix();
        String post = object.toString();
        if(!post.isEmpty())
            builder.append(prefix).append(new TranslationTextComponent(s).getString()).append(": ").append(valuePrefix).append(post);
        else
            builder.append(prefix).append(new TranslationTextComponent(s).getString());
    }

    public void nextTrans(String s, String prefix) {
        nextLine();
        prefix();
        builder.append(prefix).append(new TranslationTextComponent(s).getString());
    }

    public void nextTrans(String s) {
        nextLine();
        prefix();
        builder.append(new TranslationTextComponent(s).getString());
    }

    public void push() {
        tab++;
    }

    public void  pop() {
        tab--;
    }

    public void nextLine() {
        //if(builder.length() > 1)
            builder.append("\n");
    }

    public void prefix() {
        builder.append(GREY);
        for(int i = 0; i < tab; i++) {
            builder.append("-");
        }
    }
}
