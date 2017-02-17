/**
 * Revised SimpleBucket API class that uses Forge's fluid handler code.
 */
package alexndr.api.content.items;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import alexndr.api.config.types.ConfigItem;
import alexndr.api.helpers.game.IConfigureItemHelper;
import alexndr.api.helpers.game.TooltipHelper;
import alexndr.api.registry.ContentCategories;
import alexndr.api.registry.ContentRegistry;
import alexndr.api.registry.Plugin;
import mcjty.lib.compat.CompatItem;
import mcjty.lib.tools.ItemStackTools;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.items.ItemHandlerHelper;

/**
 * @author Sinhika
 *
 */
public class SimpleBucket extends CompatItem 
	implements IConfigureItemHelper<SimpleBucket, ConfigItem> 
{
	protected Plugin plugin;
	protected ContentCategories.Item category = ContentCategories.Item.OTHER;
	protected ConfigItem entry;
	protected List<String> toolTipStrings = Lists.newArrayList();
    protected final int capacity;
	
    protected final ItemStack empty; // empty item to return and recognize when filling
    protected final SimpleBucketType bucketType;

	/**
	 * It's a bucket; it has a fixed volume.
	 * @param empty empty bucket item; null if *this* is the empty bucket item.
	 * @param type SimpleBucketType object describing this bucket.
	 */
	public SimpleBucket(Plugin plugin, ItemStack empty, SimpleBucketType type) 
	{
		this.capacity = Fluid.BUCKET_VOLUME;
		
		this.bucketType = type;
		this.plugin = plugin;
        this.setMaxStackSize(1);
        
        // YES, we really mean to check against null here!
        if (empty == null) {
        	this.empty = new ItemStack(this);
        }
        else {
        	this.empty = empty;
        	setContainerItem(empty.getItem());
        }
	} // end ctor

	@Override
	public SimpleBucket setUnlocalizedName(String itemName) 
	{
		super.setUnlocalizedName(itemName);
        setRegistryName(this.plugin.getModId(), itemName);
        GameRegistry.register(this);
		ContentRegistry.registerItem(this.plugin, this, itemName, this.category);
		return this;
	}
	
	/**
	 * Returns the ConfigItem used by this bucket.
	 * @return ConfigItem
	 */
	public ConfigItem getConfigEntry() {
		return this.entry;
	}
	
	/**
	 * Sets the ConfigItem to be used for this bucket.
	 * @param entry ConfigItem
	 * @return SimpleBucket
	 */
	public SimpleBucket setConfigEntry(ConfigItem entry) {
		this.entry = entry;
		this.setMaxStackSize(entry.getStackSize());
		this.setAdditionalProperties();
		return this;
	}
	
	/**
	 * Adds a tooltip to the bucket. Must be unlocalised, so needs to be present in a localization file.
	 * @param toolTip Name of the localisation entry for the tooltip, as a String. Normal format is modId.theitem.info
	 * @return SimpleBucket
	 */
	public SimpleBucket addToolTip(String toolTip) {
		TooltipHelper.addTooltipToItem(this, toolTip);
		return this;
	}
	
	public void setAdditionalProperties() {
//		if(entry.getValueByName("CreativeTab") != null && entry.getValueByName("CreativeTab").isActive()) {
//			this.setCreativeTab(entry.getCreativeTab());
//		}
	}
	
	
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) 
	{
		return new SimpleBucketFluidHandler(stack, empty, capacity, bucketType);
	}

    protected FluidStack getFluid(ItemStack container)
    {
		SimpleBucketFluidHandler handler = 
				(SimpleBucketFluidHandler) FluidUtil.getFluidHandler(container);
		return handler != null ? handler.getFluid() : null;
    }

    public ItemStack getEmpty()
    {
        return empty;
    }

    @SubscribeEvent
    public void onFillBucket(FillBucketEvent event)
    {
        if (event.getResult() != Event.Result.DEFAULT)
        {
            // event was already handled
            return;
        }

        // not for us to handle, because this isn't an empty bucket.
        ItemStack emptyBucket = event.getEmptyBucket();
        if (ItemStackTools.isEmpty(emptyBucket) ||
            !emptyBucket.isItemEqual(getEmpty()))
        {
            return;
        }

        // needs to target a block
        RayTraceResult target = event.getTarget();
        if (target == null || target.typeOfHit != RayTraceResult.Type.BLOCK)
        {
            return;
        }

        World world = event.getWorld();
        BlockPos pos = target.getBlockPos();

        ItemStack singleBucket = emptyBucket.copy();
        ItemStackTools.setStackSize(singleBucket, 1);
        // note difference here from 1.11 version...
        ItemStack fRes = FluidUtil.tryPickUpFluid(singleBucket, event.getEntityPlayer(), 
        												  world, pos, target.sideHit);
        ItemStack filledBucket = fRes;
        if (ItemStackTools.isValid(filledBucket))
        {
            event.setResult(Event.Result.ALLOW);
            event.setFilledBucket(filledBucket);
        }
        // did we try to dip a meltable/flammable bucket in something hot?
        else if (bucketType.getDestroyOnLava())
        {
        	IFluidHandler targetFluidHandler = FluidUtil.getFluidHandler(world, pos, 
        			target.sideHit);
        	Fluid fluid = targetFluidHandler.getTankProperties()[0].getContents().getFluid();
        	
        	// did we??
        	if (fluid.getTemperature() >= SimpleBucketType.DESTROY_ON_LAVA_TEMP)
        	{
        		event.getEntityPlayer().playSound(
						SoundEvents.BLOCK_LAVA_EXTINGUISH, 0.5F,
						2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
        		// see if this works...
                event.setResult(Event.Result.ALLOW);
                event.setFilledBucket(ItemStackTools.getEmptyStack());
        	}
        	else {
                // cancel event, otherwise the vanilla minecraft ItemBucket would
                // convert it into a water/lava bucket depending on the blocks material
                event.setCanceled(true);
        	}
        } // end else-if DestroyOnLava
        else
        {
            // cancel event, otherwise the vanilla minecraft ItemBucket would
            // convert it into a water/lava bucket depending on the blocks material
            event.setCanceled(true);
        }
    } // end onFillBucket()

    @Override
    protected ActionResult<ItemStack> clOnItemRightClick(World world, EntityPlayer player, 
                    EnumHand hand) 
    {
    	ItemStack itemstack = player.getHeldItem(hand);
        FluidStack fluidStack = getFluid(itemstack);
        boolean flag = fluidStack == null;
        RayTraceResult mop = this.rayTrace(world, player, flag);
        
        ActionResult<ItemStack> ret = ForgeEventFactory.onBucketUse(player, world, 
        															itemstack, mop);
        // if FillBucketEvent is handled, we're done.
        if (ret != null) return ret;
        
        if (mop == null)
        {
            return ActionResult.newResult(EnumActionResult.PASS, itemstack);
        }
        else if(mop == null || mop.typeOfHit != RayTraceResult.Type.BLOCK)
        {
            return ActionResult.newResult(EnumActionResult.PASS, itemstack);
        }

        BlockPos clickPos = mop.getBlockPos();
        // can we place liquid there?
        if (!world.isBlockModifiable(player, clickPos))
        {
            // couldn't place liquid there2
            return ActionResult.newResult(EnumActionResult.FAIL, itemstack);
        }
        // for some reason, onFillBucket did not cover this...
        // probably because puddle of lava or water.
        else if (flag) // empty bucket
        {
            // the block adjacent to the side we clicked on
            BlockPos targetPos = clickPos.offset(mop.sideHit);

            // can the player change this location?
            if (!player.canPlayerEdit(targetPos, mop.sideHit, itemstack))
            {
                return ActionResult.newResult(EnumActionResult.FAIL, itemstack);
            }
            else 
            {
                IBlockState iblockstate = world.getBlockState(clickPos);
                Material material = iblockstate.getMaterial();
                
                if (material == Material.WATER 
                	&& ((Integer)iblockstate.getValue(BlockLiquid.LEVEL)).intValue() == 0)
                {
                    world.setBlockState(clickPos, Blocks.AIR.getDefaultState(), 11);
                    player.addStat(StatList.getObjectUseStats(this));
                    player.playSound(SoundEvents.ITEM_BUCKET_FILL, 1.0F, 1.0F);
                    Item water_bucket = bucketType.getBucketFromLiquid(FluidRegistry.WATER);
                    if (water_bucket == null) 
                    {
                        return ActionResult.newResult(EnumActionResult.FAIL, itemstack);
                    }
                    return ActionResult.newResult(EnumActionResult.SUCCESS, 
                    		this.fillBucket(itemstack, player, water_bucket));
                }
                else if (material == Material.LAVA 
                   && ((Integer)iblockstate.getValue(BlockLiquid.LEVEL)).intValue() == 0)
                {
                    // did we try to dip a meltable/flammable bucket in something hot?
                    if (bucketType.getDestroyOnLava())
                    {
                    	player.playSound(SoundEvents.BLOCK_LAVA_EXTINGUISH, 0.5F,
                    			2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
                    	// destroy it
                    	ItemStackTools.incStackSize(itemstack, -1);
                        return ActionResult.newResult(EnumActionResult.FAIL, itemstack);
                    }
                    // no, we didn't. But do we do lava?
                	Item lava_bucket = bucketType.getBucketFromLiquid(FluidRegistry.LAVA);
                	// YES, this is supposed to be compared with null!
                    if (lava_bucket == null) 
                    {
                        return ActionResult.newResult(EnumActionResult.FAIL, itemstack);
                    }
                    player.playSound(SoundEvents.ITEM_BUCKET_FILL_LAVA, 1.0F, 1.0F);
                    world.setBlockState(clickPos, Blocks.AIR.getDefaultState(), 11);
                    player.addStat(StatList.getObjectUseStats(this));
                    return ActionResult.newResult(EnumActionResult.SUCCESS, 
                    		this.fillBucket(itemstack, player, lava_bucket));
                }
                else
                {
                    return ActionResult.newResult(EnumActionResult.FAIL, itemstack);
                }
            } // end-if canPlayerEdit
        } // end-if flag
        // full bucket of stuff.
        else {
            boolean flag1 = world.getBlockState(clickPos).getBlock().isReplaceable(world, clickPos);
            BlockPos blockpos1 = flag1 && mop.sideHit == EnumFacing.UP 
            					? clickPos : clickPos.offset(mop.sideHit);

            if (!player.canPlayerEdit(blockpos1, mop.sideHit, itemstack))
            {
                return ActionResult.newResult(EnumActionResult.FAIL, itemstack);
            }
        	// try placing liquid
           else if (tryPlaceContainedLiquid(player, player.getEntityWorld(), blockpos1, 
        		   							fluidStack))
           {
                 // success!
                player.addStat(StatList.getObjectUseStats(this));

                if (player.capabilities.isCreativeMode)
                {
            		return ActionResult.newResult(EnumActionResult.SUCCESS, itemstack);
                }
                else {
                	ItemStackTools.incStackSize(itemstack, -1);
                	ItemStack emptyStack = getEmpty() != null 
                			? getEmpty().copy() : new ItemStack(this);

                	// check whether we replace the item or add the empty one to the inventory
                	if (ItemStackTools.isEmpty(itemstack))
                	{
                		return ActionResult.newResult(EnumActionResult.SUCCESS, emptyStack);
                	}
                	else
                	{
                		// add empty bucket to player inventory
                		ItemHandlerHelper.giveItemToPlayer(player, emptyStack);
                		return ActionResult.newResult(EnumActionResult.SUCCESS, itemstack);
                	}
                } // else !creativeMode
            } // end-if tryPlaceFluid
        } // end-else !flag
        
        // we shouldn't get here, but this shuts compiler up.
        return ActionResult.newResult(EnumActionResult.FAIL, itemstack);
    } // end onItemRightClick()

    /**
     * Couldn't use FluidUtil.tryPlaceLiquid(), because it places the wrong block
     * for water & lava.
     * 
     * @param player
     * @param world
     * @param posIn
     * @param fluidStack fluid being placed
     * @return true if successful, false if failed.
     */
    public boolean tryPlaceContainedLiquid(@Nullable EntityPlayer player, World world, 
    										BlockPos posIn,  FluidStack fluidStack)
    {
        Block fluidBlock;

    	if (this == getEmpty().getItem()) {
    		return false;
    	}
    	else
    	{
            IBlockState iblockstate = world.getBlockState(posIn);
            Material material = iblockstate.getMaterial();
            boolean flag = !material.isSolid();
            boolean flag1 = iblockstate.getBlock().isReplaceable(world, posIn);

            if (!world.isAirBlock(posIn) && !flag && !flag1)
            {
                return false;
            }
            else
            {
                if (world.provider.doesWaterVaporize() 
                	&& fluidStack.getFluid() == FluidRegistry.WATER)
                	// Blocks.FLOWING_WATER)
                {
                    int l = posIn.getX();
                    int i = posIn.getY();
                    int j = posIn.getZ();
                    world.playSound(player, posIn, SoundEvents.BLOCK_FIRE_EXTINGUISH, 
                    				SoundCategory.BLOCKS, 0.5F, 2.6F + 
                    				(world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);

                    for (int k = 0; k < 8; ++k)
                    {
                    	world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, 
                    			(double)l + Math.random(), (double)i + Math.random(), 
                    			(double)j + Math.random(), 0.0D, 0.0D, 0.0D, new int[0]);
                    }
                }
                else
                {
                    if (!world.isRemote && (flag || flag1) && !material.isLiquid())
                    {
                    	world.destroyBlock(posIn, true);
                    }
                   
                    SoundEvent soundevent = fluidStack.getFluid() == FluidRegistry.LAVA  
                    		? SoundEvents.ITEM_BUCKET_EMPTY_LAVA 
                    		: SoundEvents.ITEM_BUCKET_EMPTY;
                    world.playSound(player, posIn, soundevent, SoundCategory.BLOCKS, 
                    				1.0F, 1.0F);
                    
                    if (fluidStack.getFluid() == FluidRegistry.LAVA) {
                    	fluidBlock = Blocks.FLOWING_LAVA;
                    }
                    else if (fluidStack.getFluid() == FluidRegistry.WATER) {
                    	fluidBlock = Blocks.FLOWING_WATER;
                    }
                    else {
                    	fluidBlock = fluidStack.getFluid().getBlock();
                    }
                    world.setBlockState(posIn, fluidBlock.getDefaultState(), 11);
                }

                return true;
            	
            } // end-else    		
    	} // end-else
    } // end tryPlaceContainedLiquid()
    
    protected ItemStack fillBucket(ItemStack emptyBuckets, EntityPlayer player, Item fullBucket)
    {
        if (player.capabilities.isCreativeMode)
        {
            return emptyBuckets;
        }
        else if (ItemStackTools.isEmpty(ItemStackTools.incStackSize(emptyBuckets, -1)))
        {
            return new ItemStack(fullBucket);
        }
        else
        {
            if (!player.inventory.addItemStackToInventory(new ItemStack(fullBucket)))
            {
                player.dropItem(new ItemStack(fullBucket), false);
            }

            return emptyBuckets;
        }
    } // end fillBucket()


} // end class
