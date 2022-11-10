package com.rogoshum.magickcore.common.tileentity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.entity.PlaceableItemEntity;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.init.ModEntities;
import com.rogoshum.magickcore.common.init.ModRecipes;
import com.rogoshum.magickcore.common.init.ModTileEntities;
import com.rogoshum.magickcore.common.item.placeable.PlaceableEntityItem;
import com.rogoshum.magickcore.common.item.placeable.SpiritCrystalItem;
import com.rogoshum.magickcore.common.magick.Color;
import net.minecraft.block.Blocks;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

import java.util.ArrayList;
import java.util.List;

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

        }
        List<ItemEntity> list = world.getEntitiesWithinAABB(ItemEntity.class, new AxisAlignedBB(pos).grow(0.5), null);
        for (ItemEntity item : list) {
            ItemStack stack = ModRecipes.findExplosionOutput(item.getItem()).copy();
            if(!stack.isEmpty() && !stack.equals(item.getItem(), false)) {
                transTick+=2;
                if(world.isRemote)
                    addParticle(0.3f, item.getPositionVec().add(0, item.getHeight(), 0));
                if(transTick >= transNeed) {
                    stack.setCount(item.getItem().getCount());
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
        List<PlaceableItemEntity> placeableItemEntities = this.world.getEntitiesWithinAABB(ModEntities.PLACEABLE_ENTITY.get(), new AxisAlignedBB(pos), (entity) -> entity != corePlaceable);
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
