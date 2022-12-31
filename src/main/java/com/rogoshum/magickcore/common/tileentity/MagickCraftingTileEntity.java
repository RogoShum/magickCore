package com.rogoshum.magickcore.common.tileentity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.enums.ParticleType;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.entity.PlaceableItemEntity;
import com.rogoshum.magickcore.common.init.*;
import com.rogoshum.magickcore.common.item.material.PotionTypeItem;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.common.recipe.MagickWorkbenchRecipe;
import com.rogoshum.magickcore.common.util.ParticleBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;

import java.util.*;

public class MagickCraftingTileEntity extends TileEntity implements ITickableTileEntity {
    public int ticksExisted;
    public static final int transNeed = 120;
    public int transTick;
    private final HashSet<PlaceableItemEntity> craftingSides = new HashSet<>();
    private final CraftingMatrix craftingMatrix = CraftingMatrix.make(this);

    public MagickCraftingTileEntity() {
        super(ModTileEntities.MAGICK_CRAFTING_TILE_ENTITY.get());
    }

    public CraftingMatrix getCraftingMatrix() {
        return craftingMatrix;
    }

    @Override
    public void tick() {
        ticksExisted++;
        if(ticksExisted > 10 && ticksExisted % 10 == 0 && craftingSides.size() < 4) {
            craftingSides.clear();
            getSide(this.getPos().add(1, 0, 1));
            getSide(this.getPos().add(-1, 0, 1));
            getSide(this.getPos().add(1, 0, -1));
            getSide(this.getPos().add(-1, 0, -1));
            if(craftingSides.size() < 4) {
                this.world.setBlockState(pos, Blocks.AIR.getDefaultState());
                return;
            }
        }

        if(craftingSides.size() >= 4) {
            craftingSides.removeIf(placeableItemEntity -> !placeableItemEntity.isAlive());
            if(craftingSides.size() < 4) {
                this.world.setBlockState(pos, Blocks.AIR.getDefaultState());
                return;
            }
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
            IInventory inventory = new Inventory(item.getItem());
            Optional<MagickWorkbenchRecipe> optional = world.getRecipeManager().getRecipe(MagickWorkbenchRecipe.MAGICK_WORKBENCH, inventory, world);
            ItemStack stack = ItemStack.EMPTY;
            if(optional.isPresent())
                stack = optional.get().getCraftingResult(inventory);
            if(stack.isEmpty() || stack.equals(item.getItem(), false)) {
                if(PotionTypeItem.canTransform(item.getItem())) {
                    stack = PotionTypeItem.transformToType(item.getItem());
                }
            }
            if(!stack.isEmpty() && !stack.equals(item.getItem(), false)) {
                transTick+=2;
                if(!world.isRemote) {
                    float scale = 0.3f;
                    Vector3d vec = item.getPositionVec().add(0, item.getHeight(), 0);
                    ParticleBuilder builder = ParticleBuilder.create(world, ParticleType.PARTICLE, new Vector3d(MagickCore.getNegativeToOne() * scale * 0.5 + vec.x
                                    , MagickCore.getNegativeToOne() * scale * 0.5 + vec.y
                                    , MagickCore.getNegativeToOne() * scale * 0.5 + vec.z)
                            , scale, scale, 0.5f, 15, "origin");
                    builder.color(Color.BLUE_COLOR);
                    builder.glow();
                    builder.grav(0);
                    builder.shake(15f);
                    builder.send();
                }

                if(transTick >= transNeed) {
                    stack.setCount(item.getItem().getCount());
                    item.setItem(stack);
                    transTick=0;
                    this.world.playSound((PlayerEntity)null, this.pos.getX(), this.pos.getY(), this.pos.getZ(), ModSounds.soft_buildup.get(), SoundCategory.BLOCKS, 0.5F, 2.0F);
                }
                break;
            }
        }

        if(transTick > 0)
            transTick--;

        if(craftingSides.size() < 4) return;

        List<PlaceableItemEntity> placeableItemEntities = this.world.getEntitiesWithinAABB(ModEntities.PLACEABLE_ENTITY.get(), new AxisAlignedBB(pos), (entity -> {
            double x = Math.abs(entity.getPosX() - (this.getPos().getX() + 0.5));
            double z = Math.abs(entity.getPosZ() - (this.getPos().getZ() + 0.5));
            double y = Math.abs(entity.getPosY() - this.getPos().getY());
            return x < 0.5 && z < 0.5 && y >= 0 && y <= 1;
        }));
        placeableItemEntities.forEach(entity -> {
            if(!craftingMatrix.push(entity))
                entity.remove();
        });
        craftingMatrix.update();
    }

    private void getSide(BlockPos pos) {
        List<PlaceableItemEntity> placeableItemEntities = this.world.getEntitiesWithinAABB(ModEntities.PLACEABLE_ENTITY.get(), new AxisAlignedBB(pos), Entity::isAlive);
        if(placeableItemEntities.size() < 1) return;
        craftingSides.add(placeableItemEntities.get(0));
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

    public static class CraftingMatrix {
        private MagickCraftingTileEntity workbench;
        private final HashMap<Vector3i, PlaceableItemEntity> entityHashMap = new HashMap<>();

        private static CraftingMatrix make(MagickCraftingTileEntity workbench) {
            CraftingMatrix matrix = new CraftingMatrix();
            matrix.workbench = workbench;
            return matrix;
        }

        public Vector3d getCenterPos() {
            return Vector3d.copyCentered(workbench.getPos()).subtract(0, 0.5, 0);
        }

        private boolean push(PlaceableItemEntity entity) {
            Vector3d dir = Vector3d.ZERO;
            if(entity.getDirection().getAxis().isHorizontal()) {
                dir = Vector3d.copy(entity.getDirection().getDirectionVec()).scale(0.5).subtract(0, entity.getHeight() * 0.5, 0);
            }
            Vector3d offset = entity.getPositionVec().subtract(getCenterPos()).scale(3.333).add(dir);
            Vector3i vec = new Vector3i(Math.round(offset.x), Math.min(Math.round(offset.y), 2), Math.round(offset.z));
            if(Math.abs(vec.getX()) > 1 || Math.abs(vec.getZ()) > 1 || vec.getY() < 0) return false;
            if(entityHashMap.containsKey(vec) && !entityHashMap.containsValue(entity))
                return false;
            else if(!entityHashMap.containsKey(vec) && !entityHashMap.containsValue(entity)) {
                entityHashMap.put(vec, entity);
                entity.setDirection(Direction.UP);
                return true;
            }
            return true;
        }

        public HashMap<Vector3i, PlaceableItemEntity> getMatrix() {
            return entityHashMap;
        }

        private void update() {
            for (Iterator<Map.Entry<Vector3i, PlaceableItemEntity>> it = entityHashMap.entrySet().iterator(); it.hasNext();){
                Map.Entry<Vector3i, PlaceableItemEntity> item = it.next();
                Vector3i key = item.getKey();
                PlaceableItemEntity val = item.getValue();
                if(!val.isAlive())
                    it.remove();
                else {
                    Vector3d vec = getCenterPos().add(Vector3d.copy(key).scale(0.333));
                    val.setPosition(vec.x, vec.y, vec.z);
                }
            }
        }
    }
}
