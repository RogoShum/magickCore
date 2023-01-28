/**
 * from forge
 */
package com.rogoshum.magickcore.api.event;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class ItemAttributeModifierEvent extends Event {
    private final ItemStack stack;
    private final EquipmentSlot slotType;
    private final Multimap<Attribute, AttributeModifier> originalModifiers;
    @Nullable
    private Multimap<Attribute, AttributeModifier> modifiableModifiers;

    public ItemAttributeModifierEvent(ItemStack stack, EquipmentSlot slotType, Multimap<Attribute, AttributeModifier> modifiers)
    {
        this.stack = stack;
        this.slotType = slotType;
        this.originalModifiers = modifiers;
    }

    private Multimap<Attribute, AttributeModifier> getModifiableMap()
    {
        if (this.modifiableModifiers == null)
        {
            this.modifiableModifiers = HashMultimap.create(this.originalModifiers);
        }
        return this.modifiableModifiers;
    }

    /**
     * Adds a new attribute modifier to the given stack.
     * Modifier must have a consistent UUID for consistency between equipping and unequipping items.
     * Modifier name should clearly identify the mod that added the modifier.
     * @param attribute  Attribute
     * @param modifier   Modifier instance.
     * @return  True if the attribute was added, false if it was already present
     */
    public boolean addModifier(Attribute attribute, AttributeModifier modifier)
    {
        return getModifiableMap().put(attribute, modifier);
    }

    /** Gets the slot containing this stack */
    public EquipmentSlot getSlotType()
    {
        return this.slotType;
    }

    /** Gets the item stack instance */
    public ItemStack getItemStack()
    {
        return this.stack;
    }
}
