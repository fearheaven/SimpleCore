/**
 * Revised SimpleBucket API class that uses Forge's fluid handler code.
 */
package alexndr.api.content.items;

import java.util.List;

import com.google.common.collect.Lists;

import alexndr.api.config.types.ConfigItem;
import alexndr.api.helpers.game.IConfigureItemHelper;
import alexndr.api.helpers.game.TooltipHelper;
import alexndr.api.registry.ContentCategories;
import alexndr.api.registry.ContentRegistry;
import alexndr.api.registry.Plugin;
import mcjty.lib.compat.CompatItem;
import mcjty.lib.tools.FluidTools;
import mcjty.lib.tools.ItemStackTools;
import net.minecraft.block.BlockDispenser;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
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
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.fluids.DispenseFluidContainer;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.ItemFluidContainer;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author Sinhika
 *
 */
@SuppressWarnings("deprecation")
public class SimpleBucket extends CompatItem 
	implements IFluidContainerItem, IConfigureItemHelper<SimpleBucket, ConfigItem> 
{
	protected Plugin plugin;
	protected ContentCategories.Item category = ContentCategories.Item.OTHER;
	protected ConfigItem entry;
	protected ItemStack emptyBucketStack; // empty itemstack to return and recognize when filling
	protected List<String> toolTipStrings = Lists.newArrayList();

	protected final int capacity;
    protected final SimpleBucketType bucketType;
    protected final boolean nbtSensitive;

	/**
	 * It's a bucket; it has a fixed volume.
	 * @param type SimpleBucketType object describing this bucket.
	 */
	public SimpleBucket(Plugin plugin, SimpleBucketType type) 
	{
		this.capacity = Fluid.BUCKET_VOLUME;
		this.bucketType = type;
		this.plugin = plugin;
        this.setMaxStackSize(1);
        this.emptyBucketStack = ItemStackTools.getEmptyStack();
        this.nbtSensitive = false;
        
        // allows to work with dispensers
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, DispenseFluidContainer.getInstance());
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
	
	@Override
	public String getItemStackDisplayName(ItemStack stack)
	{
	    FluidStack fluidStack = getFluid(stack);
	    if (fluidStack == null)
	    {
            if(getEmpty() != null)
            {
                return getEmpty().getDisplayName();
            }
	        return super.getItemStackDisplayName(stack);
	    }

	    String unloc = this.getUnlocalizedNameInefficiently(stack);

	    if (I18n.canTranslate(unloc + "." + fluidStack.getFluid().getName()))
	    {
	        return I18n.translateToLocal(unloc + "." + fluidStack.getFluid().getName());
	    }

	    return I18n.translateToLocalFormatted(unloc + ".name", fluidStack.getLocalizedName());
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
	
    public int getCapacity() 
    {
        return capacity;
    }
    
    public static ItemStack getFilledBucket(SimpleBucket item, Fluid fluid)
    {
        ItemStack stack = new ItemStack(item);
        item.fill(stack, new FluidStack(fluid, item.getCapacity()), true);
        return stack;
    }
   
    @Override
    public int fill(ItemStack container, FluidStack resource, boolean doFill)
    {
        // has to be exactly 1, must be handled from the caller
        if (container.stackSize != 1)
        {
            return 0;
        }

        // can only fill exact capacity
        if (resource == null || resource.amount < getCapacity())
        {
            return 0;
        }

        // already contains fluid?
        if (getFluid(container) != null)
        {
            return 0;
        }
        // registered in the registry?
        if (bucketType.doesVariantExist(resource))
        {
            // fill the container
            if (doFill)
            {
                NBTTagCompound tag = container.getTagCompound();
                if (tag == null)
                {
                    tag = new NBTTagCompound();
                }
                resource.writeToNBT(tag);
                container.setTagCompound(tag);
            }
            return getCapacity();
        }
        return 0;
    } // end fill()

    public ItemStack getEmpty()
    {
        if (ItemStackTools.isEmpty(emptyBucketStack)) 
        {
            SimpleBucket emptyBucket = new SimpleBucket(plugin, bucketType);
            emptyBucketStack = new ItemStack(emptyBucket);
            emptyBucketStack.deserializeNBT(emptyBucketStack.serializeNBT());
        }
        return emptyBucketStack;
    } // end getEmpty()

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) 
	{
		return new SimpleBucketFluidHandler(stack, bucketType);
	}

    public boolean isNbtSensitive()
    {
        return nbtSensitive;
    }

	@Override
    public FluidStack getFluid(ItemStack container)
    {
        return FluidStack.loadFluidStackFromNBT(container.getTagCompound());
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
        if (ItemStackTools.isEmpty(emptyBucket) 
            || ! emptyBucket.isItemEqual(this.getEmpty())
            || (emptyBucket.getCapability(
                            CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null) == null)
            || (this.getFluid(emptyBucket) != null && this.getFluid(emptyBucket).amount > 0)
        )
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
        // NOTE difference here from 1.11 version...
        ItemStack filledBucket = FluidUtil.tryPickUpFluid(singleBucket, event.getEntityPlayer(), 
        												  world, pos, target.sideHit);
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
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, 
                                                    EntityPlayer playerIn, EnumHand hand)
    {
        return clOnItemRightClick(worldIn, playerIn, hand);
    }

    protected ActionResult<ItemStack> clOnItemRightClick(World world, EntityPlayer player, 
                    EnumHand hand) 
    {
        ItemStack itemstack = player.getHeldItem(hand);
        boolean flag = FluidTools.isEmptyContainer(itemstack);
        
        RayTraceResult raytraceresult = this.rayTrace(world, player, flag);
        ActionResult<ItemStack> ret = 
                        net.minecraftforge.event.ForgeEventFactory.onBucketUse(player, world, 
                                                                             itemstack, raytraceresult);
        if (ret != null) return ret;
        
        // clicked on a block?
        if(raytraceresult == null || raytraceresult.typeOfHit != RayTraceResult.Type.BLOCK)
        {
            return ActionResult.newResult(EnumActionResult.PASS, itemstack);
        }

        BlockPos clickPos = raytraceresult.getBlockPos();
        // can we place/remove liquid there?
        if (world.isBlockModifiable(player, clickPos))
        {
            // the block adjacent to the side we clicked on
            BlockPos targetPos = clickPos.offset(raytraceresult.sideHit);
            if (flag)  // empty bucket
            {
                // can the player pick up there? No?
                if (! player.canPlayerEdit(targetPos, raytraceresult.sideHit, itemstack))
                {
                    return ActionResult.newResult(EnumActionResult.FAIL, itemstack);
                }
                else // yes, player can pick up 
                {
                    ItemStack filledBucket = FluidUtil.tryPickUpFluid(itemstack, player, world, 
                                                                      clickPos, 
                                                                      raytraceresult.sideHit);
                    if (ItemStackTools.isEmpty(filledBucket)) 
                    {
                        return ActionResult.newResult(EnumActionResult.FAIL, itemstack);
                    }
                    else
                    {
                        return ActionResult.newResult(EnumActionResult.SUCCESS, filledBucket);
                    } // end-else we got a filled bucket
                } // end-else player can pick up
            } // end-if empty bucket
//            else  // non-empty bucket
//            {
//                boolean flag1 = world.getBlockState(clickPos).getBlock().isReplaceable(world, clickPos);
//                BlockPos blockpos1 = flag1 
//                                && raytraceresult.sideHit == EnumFacing.UP ? clickPos : targetPos;
//                
//                if (! player.canPlayerEdit(targetPos, raytraceresult.sideHit, itemstack))
//                {
//                    return ActionResult.newResult(EnumActionResult.FAIL, itemstack);
//                }
//                else // canPlayerEdit
//                {
//                    FluidStack fluidStack = getFluid(itemstack);
//                    // empty bucket shouldn't exist, do nothing since it should be handled by the bucket event
//                    if (fluidStack == null)
//                    {
//                        return ActionResult.newResult(EnumActionResult.PASS, itemstack);
//                    }
//                   // try placing liquid
//                    if (FluidUtil.tryPlaceFluid(player, world, fluidStack, blockpos1))
//                    {
//                        // success!
//                        player.addStat(StatList.getObjectUseStats(this));
//
//                        // empty the bucket
//                        itemstack = FluidTools.drainContainer(itemstack);
//                        return ActionResult.newResult(EnumActionResult.SUCCESS, itemstack);
//                    } // end-if tryPlaceFluid
//                    
//                } // end-else canPlayerEdit
//            } // end-else non-empty bucket
         } // end-if isBlockModifiable

        // couldn't place or remove liquid there
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
//    public boolean tryPlaceContainedLiquid(@Nullable EntityPlayer player, World world, 
//    										BlockPos posIn,  FluidStack fluidStack)
//    {
//        Block fluidBlock;
//
//    	if (this == getEmpty().getItem()) {
//    		return false;
//    	}
//    	else
//    	{
//            IBlockState iblockstate = world.getBlockState(posIn);
//            Material material = iblockstate.getMaterial();
//            boolean flag = !material.isSolid();
//            boolean flag1 = iblockstate.getBlock().isReplaceable(world, posIn);
//
//            if (!world.isAirBlock(posIn) && !flag && !flag1)
//            {
//                return false;
//            }
//            else
//            {
//                if (world.provider.doesWaterVaporize() 
//                	&& fluidStack.getFluid() == FluidRegistry.WATER)
//                	// Blocks.FLOWING_WATER)
//                {
//                    int l = posIn.getX();
//                    int i = posIn.getY();
//                    int j = posIn.getZ();
//                    world.playSound(player, posIn, SoundEvents.BLOCK_FIRE_EXTINGUISH, 
//                    				SoundCategory.BLOCKS, 0.5F, 2.6F + 
//                    				(world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
//
//                    for (int k = 0; k < 8; ++k)
//                    {
//                    	world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, 
//                    			(double)l + Math.random(), (double)i + Math.random(), 
//                    			(double)j + Math.random(), 0.0D, 0.0D, 0.0D, new int[0]);
//                    }
//                }
//                else
//                {
//                    if (!world.isRemote && (flag || flag1) && !material.isLiquid())
//                    {
//                    	world.destroyBlock(posIn, true);
//                    }
//                   
//                    SoundEvent soundevent = fluidStack.getFluid() == FluidRegistry.LAVA  
//                    		? SoundEvents.ITEM_BUCKET_EMPTY_LAVA 
//                    		: SoundEvents.ITEM_BUCKET_EMPTY;
//                    world.playSound(player, posIn, soundevent, SoundCategory.BLOCKS, 
//                    				1.0F, 1.0F);
//                    
//                    if (fluidStack.getFluid() == FluidRegistry.LAVA) {
//                    	fluidBlock = Blocks.FLOWING_LAVA;
//                    }
//                    else if (fluidStack.getFluid() == FluidRegistry.WATER) {
//                    	fluidBlock = Blocks.FLOWING_WATER;
//                    }
//                    else {
//                    	fluidBlock = fluidStack.getFluid().getBlock();
//                    }
//                    world.setBlockState(posIn, fluidBlock.getDefaultState(), 11);
//                }
//
//                return true;
//            	
//            } // end-else    		
//    	} // end-else
//    } // end tryPlaceContainedLiquid()
    
    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems)
    {
        for (FluidStack fluid : bucketType.getLiquidsList())
        {
            // add all fluids that the bucket can be filled  with
            ItemStack stack = new ItemStack(this);
            if (fill(stack, fluid, true) == fluid.amount)
            {
                subItems.add(stack);
            }
         } // end-for fluids
    } // end getSubItems()

    @Override
    public int getCapacity(ItemStack container)
    {
        return getCapacity();
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemStack)
    {
        if (getEmpty() != null)
        {
            // Create a copy such that the game can't mess with it
            return getEmpty().copy();
        }
        return super.getContainerItem(itemStack);
    }

    @Override
    public boolean hasContainerItem(ItemStack stack)
    {
        return getEmpty() != null;
    }


    @Override
    public FluidStack drain(ItemStack container, int maxDrain, boolean doDrain)
    {
        // has to be exactly 1, must be handled from the caller
        if (container.stackSize != 1)
        {
            return null;
        }

        // can only drain everything at once
        if (maxDrain < getCapacity(container))
        {
            return null;
        }

        FluidStack fluidStack = getFluid(container);
        if (doDrain && fluidStack != null)
        {
            if(getEmpty() != null)
            {
                container.deserializeNBT(getEmpty().serializeNBT());
            }
            else {
                container.stackSize = 0;
            }
        }
        return fluidStack;
    } // end drain()

} // end class
