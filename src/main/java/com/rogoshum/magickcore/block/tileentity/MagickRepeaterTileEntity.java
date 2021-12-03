package com.rogoshum.magickcore.block.tileentity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.block.ILifeStateTile;
import com.rogoshum.magickcore.entity.LifeStateEntity;
import com.rogoshum.magickcore.init.ModTileEntities;
import com.rogoshum.magickcore.magick.lifestate.repeater.LifeRepeater;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Optional;

public class MagickRepeaterTileEntity extends CanSeeTileEntity implements ILifeStateTile {
    private LifeRepeater lifeRepeater;
    private Vector3d vec = Vector3d.ZERO;
    private Vector3f rotation = new Vector3f(0, 0, 0);
    private final InterfaceDirection[] interfaceDirection = new InterfaceDirection[]{
            InterfaceDirection.create(Direction.UP), InterfaceDirection.create(Direction.DOWN),
            InterfaceDirection.create(Direction.EAST), InterfaceDirection.create(Direction.WEST),
            InterfaceDirection.create(Direction.SOUTH), InterfaceDirection.create(Direction.NORTH)
    };

    private TouchMode touchMode = TouchMode.DEFAULT;

    public MagickRepeaterTileEntity() {
        super(ModTileEntities.magick_repeater_tileentity.get());
    }

    public TouchMode getTouchMode(){
        return touchMode;
    }

    public void changeTouchMode(){
        if(touchMode == TouchMode.DEFAULT)
            touchMode = TouchMode.INPUT;
        else if(touchMode == TouchMode.INPUT)
            touchMode = TouchMode.OUTPUT;
        else
            touchMode = TouchMode.DEFAULT;

        updateInfo();
    }

    public boolean isPortTurnOn(Direction direction) {
        Optional<InterfaceDirection> directionOptional = Arrays.stream(interfaceDirection)
                .filter(port -> port.getDirection() == direction)
                .findAny();

        return directionOptional.isPresent() && directionOptional.get().isTurning();
    }

    public void setPortTurningState(Direction direction, boolean turnOn) {
        Arrays.stream(interfaceDirection)
                .filter(port -> port.getDirection() == direction)
                .forEach(port -> port.setTurn(turnOn));
        updateInfo();
    }

    public InterfaceDirection[] getPort() {
        return this.interfaceDirection;
    }

    @Override
    public void touch(LifeStateEntity entity) {
        if (lifeRepeater != null)
            lifeRepeater.touch(this, entity);
        else
            entity.split(this);
        //entity.updateCarrierNbt();
    }

    public void setLifeRepeater(LifeRepeater repeater) {
        dropItem();
        this.lifeRepeater = repeater;
        updateInfo();
    }

    public LifeRepeater getLifeRepeater() {
        return this.lifeRepeater;
    }

    public void changeDirection(Entity entity) {
        Vector3d pos = Vector3d.copyCentered(this.pos);
        changeDirection(entity.getPositionVec().add(0, entity.getEyeHeight(), 0).subtract(pos));
    }

    public void changeDirection(Vector3d vector3d) {
        this.vec = vector3d.normalize().scale(0.01);
        setYawPatch();
        updateInfo();
    }

    public Vector3d getDirection() {
        return vec;
    }

    private void setYawPatch() {
        double x = vec.x;
        double y = vec.y;
        double z = vec.z;

        float yaw = (float) (Math.atan2(z, x) * 180 / Math.PI);
        if (yaw < 0)
            yaw += 360;

        double tmp = Math.sqrt(x * x + z * z);
        float pitch = (float) (Math.atan2(-y, tmp) * 180 / Math.PI);
        if (pitch < 0)
            pitch += 360;

        this.rotation = new Vector3f(pitch - 90, yaw - 90, 0);
    }

    public Vector3f getRotation() {
        return this.rotation;
    }

    protected void updateInfo() {
        if (!world.isRemote)
            world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(pos, 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        handleUpdateTag(world.getBlockState(pkt.getPos()), pkt.getNbtCompound());
    }

    protected void dropItem() {
        if (this.lifeRepeater != null) {
            if (this.lifeRepeater.dropItem() != null) {
                ItemEntity item = new ItemEntity(world, this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5);
                item.setItem(this.lifeRepeater.dropItem());
                world.addEntity(item);
            }
        }
    }

    @Override
    public void remove() {
        dropItem();
        super.remove();
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT compoundNBT = super.getUpdateTag();
        storageTag(compoundNBT);
        return compoundNBT;
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        extractTag(tag);
    }

    @Override
    public void read(BlockState state, CompoundNBT compound) {
        extractTag(compound);
        super.read(state, compound);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        storageTag(compound);
        return super.write(compound);
    }

    public void extractTag(CompoundNBT compound) {
        String repeater = compound.getString("Repeater");
        if (compound.contains("Repeater") && repeater != "") {
            try {
                Class clazz = Class.forName(repeater);
                this.lifeRepeater = (LifeRepeater) clazz.newInstance();
            } catch (Exception e) {
                MagickCore.LOGGER.debug("RepeaterTile catch wrong clazz name :" + repeater);
                e.printStackTrace();
            }
        }
        this.vec = new Vector3d(compound.getDouble("VecX"), compound.getDouble("VecY"), compound.getDouble("VecZ"));
        setYawPatch();

        for(InterfaceDirection direction : interfaceDirection){
            direction.setTurn(compound.getBoolean(direction.direction.getName2()));
        }

        this.touchMode = TouchMode.byMode(compound.getInt("Mode"));
    }

    public void storageTag(CompoundNBT compound) {
        if (this.lifeRepeater != null)
            compound.putString("Repeater", this.lifeRepeater.getClass().getName());
        compound.putDouble("VecX", this.vec.x);
        compound.putDouble("VecY", this.vec.y);
        compound.putDouble("VecZ", this.vec.z);

        for(InterfaceDirection direction : interfaceDirection){
            compound.putBoolean(direction.direction.getName2(), direction.isTurning());
        }

        compound.putInt("Mode", this.touchMode.mode);
    }

    @Override
    public BlockPos pos() {
        return getPos();
    }

    @Override
    public World world() {
        return getWorld();
    }

    @Override
    public boolean removed() {
        return isRemoved();
    }

    public static class InterfaceDirection {
        private final Direction direction;
        private boolean turn;

        private InterfaceDirection(Direction direction, boolean turnOn) {
            this.direction = direction;
            this.turn = turnOn;
        }

        public static InterfaceDirection create(Direction direction) {
            return new InterfaceDirection(direction, false);
        }

        public boolean isTurning() {
            return turn;
        }

        public void setTurn(boolean turn) {
            this.turn = turn;
        }

        public Direction getDirection() {
            return direction;
        }
    }

    public enum TouchMode {
        DEFAULT(0),
        INPUT(1),
        OUTPUT(2);

        private final int mode;

        TouchMode(int mode) {
            this.mode = mode;
        }

        public static TouchMode byMode(int i){
            for(TouchMode type : TouchMode.values())
            {
                if(type.mode == i)
                    return type;
            }

            return DEFAULT;
        }

        public int getMode() {
            return mode;
        }
    }
}
