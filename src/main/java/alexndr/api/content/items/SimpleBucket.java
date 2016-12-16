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
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.fluids.DispenseFluidContainer;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
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
public class SimpleBucket extends ItemFluidContainer 
	implements IConfigureItemHelper<SimpleBucket, ConfigItem> 
{
	protected Plugin plugin;
	protected ContentCategories.Item category = ContentCategories.Item.OTHER;
	protected ConfigItem entry;
	protected List<String> toolTipStrings = Lists.newArrayList();

    protected final SimpleBucketType bucketType;

	/**
	 * It's a bucket; it has a fixed volume.
	 * @param type SimpleBucketType object describing this bucket.
	 */
	public SimpleBucket(Plugin plugin, SimpleBucketType type) 
	{
		super(Fluid.BUCKET_VOLUME);
		
		this.bucketType = type;
		this.plugin = plugin;
        this.setMaxStackSize(1);
        
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
	
	@SuppressWarnings("deprecation")
    @Override
	public String getItemStackDisplayName(ItemStack stack)
	{
	    FluidStack fluidStack = getFluid(stack);
	    if (fluidStack == null)
	    {
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
    
	
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) 
	{
		return new SimpleBucketFluidHandler(stack, capacity, bucketType);
	}

    protected FluidStack getFluid(ItemStack container)
    {
		SimpleBucketFluidHandler handler = 
				(SimpleBucketFluidHandler) container.getCapability(
						CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
		return handler != null ? handler.getFluid() : null;
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
            || ! emptyBucket.isItemEqual(new ItemStack(this))
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
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand)
    {
        return clOnItemRightClick(worldIn, playerIn, hand);
    }

    protected ActionResult<ItemStack> clOnItemRightClick(World world, EntityPlayer player, 
                    EnumHand hand) 
    {
    	ItemStack itemstack = player.getHeldItem(hand);
        FluidStack fluidStack = getFluid(itemstack);
        // empty bucket shouldn't exist, do nothing since it should be handled by the bucket event
        if (fluidStack == null)
        {
            return ActionResult.newResult(EnumActionResult.PASS, itemstack);
        }

        // clicked on a block?
        RayTraceResult mop = this.rayTrace(world, player, false);
        if(mop == null || mop.typeOfHit != RayTraceResult.Type.BLOCK)
        {
            return ActionResult.newResult(EnumActionResult.PASS, itemstack);
        }

        BlockPos clickPos = mop.getBlockPos();
        // can we place liquid there?
        if (world.isBlockModifiable(player, clickPos))
        {
            // the block adjacent to the side we clicked on
            BlockPos targetPos = clickPos.offset(mop.sideHit);

            // can the player place there?
            if (player.canPlayerEdit(targetPos, mop.sideHit, itemstack))
            {
                // try placing liquid
                if (FluidUtil.tryPlaceFluid(player, player.getEntityWorld(), fluidStack, targetPos)
                        && !player.capabilities.isCreativeMode)
                {
                    // success!
                    player.addStat(StatList.getObjectUseStats(this));

                    // empty the bucket
                    SimpleBucketFluidHandler handler = 
                                    (SimpleBucketFluidHandler) itemstack.getCapability(
                                            CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY,
                                            null);
                    handler.setContainerToEmpty();
                    return ActionResult.newResult(EnumActionResult.SUCCESS, itemstack);
                } // end-if tryPlaceFluid
            } // end-if canPlayerEdit
        } // end-if isBlockModifiable

        // couldn't place liquid there2
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
        for (Fluid fluid : bucketType.getLiquidsList())
        {
            // add all fluids that the bucket can be filled  with
            FluidStack fs = new FluidStack(fluid, getCapacity());
            ItemStack stack = new ItemStack(this);
            SimpleBucketFluidHandler handler = 
                            (SimpleBucketFluidHandler) stack.getCapability(
                                    CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
            if (handler != null && handler.fill(fs, true) == fs.amount)
            {
                subItems.add(stack);
            }
        } // end-for fluids
    } // end getSubItems()

} // end class
