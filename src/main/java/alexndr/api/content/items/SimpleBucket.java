/**
 * Revised SimpleBucket API class that uses Forge's fluid handler code.
 */
package alexndr.api.content.items;

import java.util.List;

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
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.ItemFluidContainer;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.items.ItemHandlerHelper;
import alexndr.api.config.types.ConfigItem;
import alexndr.api.helpers.game.TooltipHelper;
import alexndr.api.registry.ContentCategories;
import alexndr.api.registry.ContentRegistry;
import alexndr.api.registry.Plugin;

import com.google.common.collect.Lists;

/**
 * @author Sinhika
 *
 */
public class SimpleBucket extends ItemFluidContainer 
{
	protected Plugin plugin;
	protected ContentCategories.Item category = ContentCategories.Item.OTHER;
	protected ConfigItem entry;
	protected List<String> toolTipStrings = Lists.newArrayList();
	
    protected final ItemStack empty; // empty item to return and recognize when filling
    protected final SimpleBucketType bucketType;

	/**
	 * It's a bucket; it has a fixed volume.
	 * @param empty empty bucket item.
	 * @param type SimpleBucketType object describing this bucket.
	 */
	public SimpleBucket(Plugin plugin, ItemStack empty, SimpleBucketType type) 
	{
		super(Fluid.BUCKET_VOLUME);
		
		this.bucketType = type;
		this.plugin = plugin;
        this.setMaxStackSize(1);
		this.empty = ((empty == null) ? new ItemStack(this) : empty);
	}

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
		if(entry.getValueByName("CreativeTab") != null && entry.getValueByName("CreativeTab").isActive()) {
			this.setCreativeTab(entry.getCreativeTab());
		}
	}
	
	
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) 
	{
		return new SimpleBucketFluidHandler(stack, empty, capacity, bucketType);
	}

    protected FluidStack getFluid(ItemStack container)
    {
		SimpleBucketFluidHandler handler = 
				(SimpleBucketFluidHandler) container.getCapability(
						CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
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
        if (emptyBucket == null ||
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
        singleBucket.stackSize = 1;

        ItemStack filledBucket = FluidUtil.tryPickUpFluid(singleBucket, event.getEntityPlayer(), world, pos, target.sideHit);
        if (filledBucket != null)
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
        	if (fluid == FluidRegistry.LAVA 
        		|| fluid.getTemperature() >= SimpleBucketType.DESTROY_ON_LAVA_TEMP)
        	{
        		event.getEntityPlayer().playSound(
						SoundEvents.BLOCK_LAVA_EXTINGUISH, 0.5F,
						2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
        		// TODO see if this works...
                event.setResult(Event.Result.ALLOW);
                event.setFilledBucket(null);
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

//    @Override
//    public String getItemStackDisplayName(ItemStack stack)
//    {
//        FluidStack fluidStack = getFluid(stack);
//        if (fluidStack == null)
//        {
//            if(getEmpty() != null)
//            {
//                return getEmpty().getDisplayName();
//            }
//            return super.getItemStackDisplayName(stack);
//        }
//
//        String unloc = this.getUnlocalizedNameInefficiently(stack);
//
//        if (I18n.canTranslate(unloc + "." + fluidStack.getFluid().getName()))
//        {
//            return I18n.translateToLocal(unloc + "." + fluidStack.getFluid().getName());
//        }
//
//        return I18n.translateToLocalFormatted(unloc + ".name", fluidStack.getLocalizedName());
//    } // end getItemStackDisplayName()
//    
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemstack, World world, 
    											    EntityPlayer player, EnumHand hand)
    {
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
        // EVERYTHING BELOW IS PROBABLY UNNECESSARY, but at least handle 
        // vanilla liquids in case onFillBucket() is AWOL.
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
                    	--itemstack.stackSize;
                        if (itemstack.stackSize < 0)
                        {
                        	itemstack.stackSize = 0;
                        }
                        return ActionResult.newResult(EnumActionResult.FAIL, itemstack);
                    }
                    // no, we didn't. But do we do lava?
                	Item lava_bucket = bucketType.getBucketFromLiquid(FluidRegistry.LAVA);
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
            else if (FluidUtil.tryPlaceFluid(player, player.getEntityWorld(), 
            								 fluidStack, blockpos1))
            {
                // success!
                player.addStat(StatList.getObjectUseStats(this));

                if (player.capabilities.isCreativeMode)
                {
            		return ActionResult.newResult(EnumActionResult.SUCCESS, itemstack);
                }
                else {
                	itemstack.stackSize--;
                	ItemStack emptyStack = getEmpty() != null ? getEmpty().copy() : new ItemStack(this);

                	// check whether we replace the item or add the empty one to the inventory
                	if (itemstack.stackSize <= 0)
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

    protected ItemStack fillBucket(ItemStack emptyBuckets, EntityPlayer player, Item fullBucket)
    {
        if (player.capabilities.isCreativeMode)
        {
            return emptyBuckets;
        }
        else if (--emptyBuckets.stackSize <= 0)
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
