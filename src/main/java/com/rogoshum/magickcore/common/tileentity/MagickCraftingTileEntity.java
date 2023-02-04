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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class MagickCraftingTileEntity extends BlockEntity implements TickableBlockEntity {
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
            getSide(this.getBlockPos().offset(1, 0, 1));
            getSide(this.getBlockPos().offset(-1, 0, 1));
            getSide(this.getBlockPos().offset(1, 0, -1));
            getSide(this.getBlockPos().offset(-1, 0, -1));
            if(craftingSides.size() < 4) {
                this.level.setBlockAndUpdate(worldPosition, Blocks.AIR.defaultBlockState());
                return;
            }
        }

        if(craftingSides.size() >= 4) {
            craftingSides.removeIf(placeableItemEntity -> !placeableItemEntity.isAlive());
            if(craftingSides.size() < 4) {
                this.level.setBlockAndUpdate(worldPosition, Blocks.AIR.defaultBlockState());
                return;
            }
        }

        if(level.isClientSide) {
            float scale = 0.1f;
            Vec3 vec = Vec3.atCenterOf(this.getBlockPos().offset(1, 0, 1));
            addParticle(scale, vec);

            vec = Vec3.atCenterOf(this.getBlockPos().offset(-1, 0, 1));
            addParticle(scale, vec);

            vec = Vec3.atCenterOf(this.getBlockPos().offset(1, 0, -1));
            addParticle(scale, vec);

            vec = Vec3.atCenterOf(this.getBlockPos().offset(-1, 0, -1));
            addParticle(scale, vec);

            vec = Vec3.atCenterOf(this.getBlockPos());
            LitParticle par = new LitParticle(this.level, ModElements.ORIGIN.getRenderer().getParticleTexture()
                    , new Vec3(Mth.sin(MagickCore.getNegativeToOne() * 1.5f) + vec.x
                    , vec.y - 0.3
                    , Mth.sin(MagickCore.getNegativeToOne() * 1.5f) + vec.z)
                    , scale * 0.2f, scale * 2f, 0.5f, Math.max((int) (30 * MagickCore.rand.nextFloat()), 15), ModElements.ORIGIN.getRenderer());
            par.setGlow();
            par.setParticleGravity(-0.1f);
            par.setColor(Color.BLUE_COLOR);
            MagickCore.addMagickParticle(par);

        }
        List<ItemEntity> list = level.getEntitiesOfClass(ItemEntity.class, new AABB(worldPosition).inflate(0.5), null);
        for (ItemEntity item : list) {
            Container inventory = new SimpleContainer(item.getItem());
            Optional<MagickWorkbenchRecipe> optional = level.getRecipeManager().getRecipeFor(ModRecipes.MAGICK_WORKBENCH, inventory, level);
            ItemStack stack = ItemStack.EMPTY;
            if(optional.isPresent())
                stack = optional.get().assemble(inventory);
            if(stack.isEmpty() || ItemStack.matches(stack, item.getItem())) {
                if(PotionTypeItem.canTransform(item.getItem())) {
                    stack = PotionTypeItem.transformToType(item.getItem());
                }
            }
            if(!stack.isEmpty() && !ItemStack.matches(stack, item.getItem())) {
                transTick+=2;
                if(!level.isClientSide) {
                    float scale = 0.3f;
                    Vec3 vec = item.position().add(0, item.getBbHeight(), 0);
                    ParticleBuilder builder = ParticleBuilder.create(level, ParticleType.PARTICLE, new Vec3(MagickCore.getNegativeToOne() * scale * 0.5 + vec.x
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
                    this.level.playSound((Player) null, this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), ModSounds.soft_buildup.get(), SoundSource.BLOCKS, 0.5F, 2.0F);
                }
                break;
            }
        }

        if(transTick > 0)
            transTick--;

        if(craftingSides.size() < 4) return;

        List<PlaceableItemEntity> placeableItemEntities = this.level.getEntities(ModEntities.PLACEABLE_ENTITY.get(), new AABB(worldPosition), (entity -> {
            double x = Math.abs(entity.getX() - (this.getBlockPos().getX() + 0.5));
            double z = Math.abs(entity.getZ() - (this.getBlockPos().getZ() + 0.5));
            double y = Math.abs(entity.getY() - this.getBlockPos().getY());
            return x < 0.5 && z < 0.5 && y >= 0 && y <= 1;
        }));
        placeableItemEntities.forEach(entity -> {
            if(!craftingMatrix.push(entity))
                entity.remove();
        });
        craftingMatrix.update();
    }

    private void getSide(BlockPos pos) {
        List<PlaceableItemEntity> placeableItemEntities = this.level.getEntities(ModEntities.PLACEABLE_ENTITY.get(), new AABB(pos), Entity::isAlive);
        if(placeableItemEntities.size() < 1) return;
        craftingSides.add(placeableItemEntities.get(0));
    }

    private void addParticle(float scale, Vec3 vec) {
        LitParticle par = new LitParticle(this.level, ModElements.ORIGIN.getRenderer().getParticleTexture()
                , new Vec3(MagickCore.getNegativeToOne() * scale * 0.5 + vec.x
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
        private final HashMap<Vec3i, PlaceableItemEntity> entityHashMap = new HashMap<>();

        private static CraftingMatrix make(MagickCraftingTileEntity workbench) {
            CraftingMatrix matrix = new CraftingMatrix();
            matrix.workbench = workbench;
            return matrix;
        }

        public Vec3 getCenterPos() {
            return Vec3.atCenterOf(workbench.getBlockPos()).subtract(0, 0.5, 0);
        }

        private boolean push(PlaceableItemEntity entity) {
            Vec3 dir = Vec3.ZERO;
            if(entity.getDirection().getAxis().isHorizontal()) {
                dir = Vec3.atLowerCornerOf(entity.getDirection().getNormal()).scale(0.5).subtract(0, entity.getBbHeight() * 0.5, 0);
            }
            Vec3 offset = entity.position().subtract(getCenterPos()).scale(3.333).add(dir);
            Vec3i vec = new Vec3i(Math.round(offset.x), Math.min(Math.round(offset.y), 2), Math.round(offset.z));
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

        public HashMap<Vec3i, PlaceableItemEntity> getMatrix() {
            return entityHashMap;
        }

        private void update() {
            for (Iterator<Map.Entry<Vec3i, PlaceableItemEntity>> it = entityHashMap.entrySet().iterator(); it.hasNext();){
                Map.Entry<Vec3i, PlaceableItemEntity> item = it.next();
                Vec3i key = item.getKey();
                PlaceableItemEntity val = item.getValue();
                if(!val.isAlive())
                    it.remove();
                else {
                    Vec3 vec = getCenterPos().add(Vec3.atLowerCornerOf(key).scale(0.333));
                    val.setPos(vec.x, vec.y, vec.z);
                }
            }
        }
    }
}
