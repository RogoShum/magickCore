package com.rogoshum.magickcore.block.tileentity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.IManaMaterial;
import com.rogoshum.magickcore.api.IManaTransable;
import com.rogoshum.magickcore.capability.IEntityState;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.init.ModItems;
import com.rogoshum.magickcore.init.ModTileEntities;
import com.rogoshum.magickcore.lib.LibElements;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.UUID;

public class MagickExtractorTileEntity extends CanSeeTileEntity implements ITickableTileEntity{
    public MagickExtractorTileEntity() {
        super(ModTileEntities.magick_container_tileentity.get());
    }

    @Override
    public void tick() {
    }
}
