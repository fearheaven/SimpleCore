/**
 * Revised SimpleBucket API class that uses Forge's fluid handler code.
 */
package alexndr.api.content.items;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.ItemFluidContainer;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
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

    @SubscribeEvent(priority = EventPriority.LOW) // low priority so other mods can handle their stuff first
    public void onFillBucket(FillBucketEvent event)
    {
        if (event.getResult() != Event.Result.DEFAULT)
        {
            // event was already handled
            return;
        }

        // not for us to handle
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
        	if (fluid.getTemperature() >= SimpleBucketType.DESTROY_ON_LAVA_TEMP)
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
                } // end-if tryPlaceFluid
            } // end-if canPlayerEdit
        } // end-if isBlockModifiable

        // couldn't place liquid there2
        return ActionResult.newResult(EnumActionResult.FAIL, itemstack);
    } // end onItemRightClick()

    
    
} // end class
