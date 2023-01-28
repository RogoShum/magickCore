/**
 * from forge
 */

package com.rogoshum.magickcore.api.event;

import com.rogoshum.magickcore.api.event.living.LivingEvent;
import net.minecraft.world.entity.player.Player;

public class PlayerEvent extends LivingEvent {
    private final Player entityPlayer;
    public PlayerEvent(Player player)
    {
        super(player);
        entityPlayer = player;
    }

    public Player getPlayer() { return entityPlayer; }
    public static class Clone extends PlayerEvent{
        private final Player original;
        private final boolean wasDeath;

        public Clone(Player _new, Player oldPlayer, boolean wasDeath)
        {
            super(_new);
            this.original = oldPlayer;
            this.wasDeath = wasDeath;
        }

        /**
         * The old EntityPlayer that this new entity is a clone of.
         */
        public Player getOriginal()
        {
            return original;
        }

        /**
         * True if this event was fired because the player died.
         * False if it was fired because the entity switched dimensions.
         */
        public boolean isWasDeath()
        {
            return wasDeath;
        }
    }

    public static class PlayerLoggedInEvent extends PlayerEvent {
        public PlayerLoggedInEvent(Player player)
        {
            super(player);
        }
    }
}
