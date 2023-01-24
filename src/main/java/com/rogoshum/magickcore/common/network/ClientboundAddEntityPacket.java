package com.rogoshum.magickcore.common.network;

import com.rogoshum.magickcore.api.entity.IEntityAdditionalSpawnData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.BeeAggressiveSoundInstance;
import net.minecraft.client.resources.sounds.BeeFlyingSoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.util.Mth;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.UUID;

public class ClientboundAddEntityPacket implements Packet<ClientGamePacketListener> {
    private static final Logger LOGGER = LogManager.getLogger();
    private int id;
    private UUID uuid;
    private int type;
    private double x;
    private double y;
    private double z;
    private int xd;
    private int yd;
    private int zd;
    private byte yRot;
    private byte xRot;
    private byte yHeadRot;
    private Entity entity;
    private FriendlyByteBuf friendlyByteBuf;

    public ClientboundAddEntityPacket() {
    }

    public ClientboundAddEntityPacket(Entity livingEntity) {
        this.id = livingEntity.getId();
        this.uuid = livingEntity.getUUID();
        this.type = Registry.ENTITY_TYPE.getId(livingEntity.getType());
        this.x = livingEntity.getX();
        this.y = livingEntity.getY();
        this.z = livingEntity.getZ();
        this.yRot = (byte)((int)(livingEntity.yRot * 256.0F / 360.0F));
        this.xRot = (byte)((int)(livingEntity.xRot * 256.0F / 360.0F));
        this.yHeadRot = livingEntity instanceof LivingEntity ? (byte)((int)(((LivingEntity)livingEntity).yHeadRot * 256.0F / 360.0F)) : 0;
        double d = 3.9D;
        Vec3 vec3 = livingEntity.getDeltaMovement();
        double e = Mth.clamp(vec3.x, -3.9D, 3.9D);
        double f = Mth.clamp(vec3.y, -3.9D, 3.9D);
        double g = Mth.clamp(vec3.z, -3.9D, 3.9D);
        this.xd = (int)(e * 8000.0D);
        this.yd = (int)(f * 8000.0D);
        this.zd = (int)(g * 8000.0D);
        this.entity = livingEntity;
    }

    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.id = friendlyByteBuf.readVarInt();
        this.uuid = friendlyByteBuf.readUUID();
        this.type = friendlyByteBuf.readVarInt();
        this.x = friendlyByteBuf.readDouble();
        this.y = friendlyByteBuf.readDouble();
        this.z = friendlyByteBuf.readDouble();
        this.yRot = friendlyByteBuf.readByte();
        this.xRot = friendlyByteBuf.readByte();
        this.yHeadRot = friendlyByteBuf.readByte();
        this.xd = friendlyByteBuf.readShort();
        this.yd = friendlyByteBuf.readShort();
        this.zd = friendlyByteBuf.readShort();
    }

    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeVarInt(this.id);
        friendlyByteBuf.writeUUID(this.uuid);
        friendlyByteBuf.writeVarInt(this.type);
        friendlyByteBuf.writeDouble(this.x);
        friendlyByteBuf.writeDouble(this.y);
        friendlyByteBuf.writeDouble(this.z);
        friendlyByteBuf.writeByte(this.yRot);
        friendlyByteBuf.writeByte(this.xRot);
        friendlyByteBuf.writeByte(this.yHeadRot);
        friendlyByteBuf.writeShort(this.xd);
        friendlyByteBuf.writeShort(this.yd);
        friendlyByteBuf.writeShort(this.zd);
        if (entity instanceof IEntityAdditionalSpawnData)
        {
            ((IEntityAdditionalSpawnData)entity).writeSpawnData(friendlyByteBuf);
        }
    }

    public void handle(ClientGamePacketListener clientGamePacketListener) {
        PacketUtils.ensureRunningOnSameThread(this, clientGamePacketListener, Minecraft.getInstance());
        double d = this.getX();
        double e = this.getY();
        double f = this.getZ();
        float g = (float)(this.getyRot() * 360) / 256.0F;
        float h = (float)(this.getxRot() * 360) / 256.0F;
        Entity livingEntity = (Entity) EntityType.create(this.getType(), Minecraft.getInstance().level);
        if (livingEntity != null) {
            livingEntity.setPacketCoordinates(d, e, f);
            if(livingEntity instanceof LivingEntity) {
                ((LivingEntity)livingEntity).yBodyRot = (float)(this.getyHeadRot() * 360) / 256.0F;
                ((LivingEntity)livingEntity).yHeadRot = (float)(this.getyHeadRot() * 360) / 256.0F;
            }
            if (livingEntity instanceof EnderDragon) {
                EnderDragonPart[] enderDragonParts = ((EnderDragon)livingEntity).getSubEntities();

                for(int i = 0; i < enderDragonParts.length; ++i) {
                    enderDragonParts[i].setId(i + this.getId());
                }
            }

            livingEntity.setId(this.getId());
            livingEntity.setUUID(this.getUUID());
            livingEntity.absMoveTo(d, e, f, g, h);
            livingEntity.setDeltaMovement((double)((float)this.getXd() / 8000.0F), (double)((float)this.getYd() / 8000.0F), (double)((float)this.getZd() / 8000.0F));
            if (livingEntity instanceof IEntityAdditionalSpawnData)
            {
                ((IEntityAdditionalSpawnData) livingEntity).readSpawnData(friendlyByteBuf);
            }
            Minecraft.getInstance().level.putNonPlayer(this.getId(), livingEntity);
            if (livingEntity instanceof Bee) {
                boolean bl = ((Bee)livingEntity).isAngry();
                Object beeSoundInstance2;
                if (bl) {
                    beeSoundInstance2 = new BeeAggressiveSoundInstance((Bee)livingEntity);
                } else {
                    beeSoundInstance2 = new BeeFlyingSoundInstance((Bee)livingEntity);
                }

                Minecraft.getInstance().getSoundManager().queueTickingSound((TickableSoundInstance)beeSoundInstance2);
            }
        } else {
            LOGGER.warn((String)"Skipping Entity with id {}", (Object)this.getType());
        }

    }

    @Environment(EnvType.CLIENT)
    public int getId() {
        return this.id;
    }

    @Environment(EnvType.CLIENT)
    public UUID getUUID() {
        return this.uuid;
    }

    @Environment(EnvType.CLIENT)
    public int getType() {
        return this.type;
    }

    @Environment(EnvType.CLIENT)
    public double getX() {
        return this.x;
    }

    @Environment(EnvType.CLIENT)
    public double getY() {
        return this.y;
    }

    @Environment(EnvType.CLIENT)
    public double getZ() {
        return this.z;
    }

    @Environment(EnvType.CLIENT)
    public int getXd() {
        return this.xd;
    }

    @Environment(EnvType.CLIENT)
    public int getYd() {
        return this.yd;
    }

    @Environment(EnvType.CLIENT)
    public int getZd() {
        return this.zd;
    }

    @Environment(EnvType.CLIENT)
    public byte getyRot() {
        return this.yRot;
    }

    @Environment(EnvType.CLIENT)
    public byte getxRot() {
        return this.xRot;
    }

    @Environment(EnvType.CLIENT)
    public byte getyHeadRot() {
        return this.yHeadRot;
    }
}
