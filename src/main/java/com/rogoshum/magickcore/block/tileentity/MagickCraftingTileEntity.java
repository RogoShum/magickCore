package com.rogoshum.magickcore.block.tileentity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.mana.IManaMaterial;
import com.rogoshum.magickcore.block.SpiritCrystalBlock;
import com.rogoshum.magickcore.client.element.ElementRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.entity.PlaceableItemEntity;
import com.rogoshum.magickcore.init.*;
import com.rogoshum.magickcore.item.placeable.SpiritCrystalItem;
import com.rogoshum.magickcore.lib.LibElements;
import com.rogoshum.magickcore.magick.Color;
import com.rogoshum.magickcore.magick.extradata.item.ItemManaData;
import com.rogoshum.magickcore.registry.MagickRegistry;
import com.rogoshum.magickcore.tool.ExtraDataHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class MagickCraftingTileEntity extends CanSeeTileEntity implements ITickableTileEntity {
    public static final int transNeed = 120;
    public int transTick;
    private PlaceableItemEntity corePlaceable;

    public MagickCraftingTileEntity() {
        super(ModTileEntities.magick_crafting_tileentity.get());
    }

    @Override
    public void tick() {
        ticksExisted++;
        if(ticksExisted > 10 && ticksExisted % 10 == 0) {
            if(SpiritCrystalItem.validCrafting(world, pos, true) == null)
                this.world.setBlockState(pos, Blocks.AIR.getDefaultState());
        }

        if(world.isRemote) {
            float scale = 0.1f;
            Vector3d vec = Vector3d.copyCentered(this.getPos().add(1, 0, 1));
            addParticle(scale, vec);

            vec = Vector3d.copyCentered(this.getPos().add(-1, 0, 1));
            addParticle(scale, vec);

            vec = Vector3d.copyCentered(this.getPos().add(1, 0, -1));
            addParticle(scale, vec);

            vec = Vector3d.copyCentered(this.getPos().add(-1, 0, -1));
            addParticle(scale, vec);

            vec = Vector3d.copyCentered(this.getPos());
            LitParticle par = new LitParticle(this.world, ModElements.ORIGIN.getRenderer().getParticleTexture()
                    , new Vector3d(MathHelper.sin(MagickCore.getNegativeToOne() * 1.5f) + vec.x
                    , vec.y - 0.3
                    , MathHelper.sin(MagickCore.getNegativeToOne() * 1.5f) + vec.z)
                    , scale * 0.2f, scale * 2f, 0.5f, Math.max((int) (30 * MagickCore.rand.nextFloat()), 15), ModElements.ORIGIN.getRenderer());
            par.setGlow();
            par.setParticleGravity(-0.1f);
            par.setColor(Color.BLUE_COLOR);
            MagickCore.addMagickParticle(par);

            return;
        }
        List<ItemEntity> list = world.getEntitiesWithinAABB(ItemEntity.class, new AxisAlignedBB(pos).grow(0.5), null);
        for (ItemEntity item : list) {
            ItemStack stack = ModRecipes.findExplosionOutput(item.getItem());
            if(!stack.isEmpty() && !stack.equals(item.getItem(), false)) {
                transTick+=2;
                addParticle(0.3f, item.getPositionVec().add(0, item.getHeight(), 0));
                if(transTick >= transNeed) {
                    item.setItem(stack);
                    transTick=0;
                }
                break;
            }
        }

        if(transTick > 0)
            transTick--;

        if(corePlaceable != null && !corePlaceable.isAlive()) {
            corePlaceable = null;
            return;
        }
        List<PlaceableItemEntity> placeableItemEntities = this.world.getEntitiesWithinAABB(ModEntities.placeable_entity.get(), new AxisAlignedBB(pos.add(0, 1, 0)).grow(1, 1, 1), (entity) -> entity != corePlaceable);
        if(placeableItemEntities.size() < 1) return;
        if(corePlaceable == null)
            corePlaceable = placeableItemEntities.get(0);
        else {
            for (PlaceableItemEntity placeableItem : placeableItemEntities){
                if(placeableItem.getOrigin() != corePlaceable)
                    placeableItem.remove();
            }
        }
    }

    private void addParticle(float scale, Vector3d vec) {
        LitParticle par = new LitParticle(this.world, ModElements.ORIGIN.getRenderer().getParticleTexture()
                , new Vector3d(MagickCore.getNegativeToOne() * scale * 0.5 + vec.x
                , MagickCore.getNegativeToOne() * scale * 0.5 + vec.y
                , MagickCore.getNegativeToOne() * scale * 0.5 + vec.z)
                , scale, scale, 0.5f, 15, ModElements.ORIGIN.getRenderer());
        par.setGlow();
        par.setParticleGravity(0f);
        par.setColor(Color.BLUE_COLOR);
        par.setShakeLimit(15f);
        MagickCore.addMagickParticle(par);
    }
}
