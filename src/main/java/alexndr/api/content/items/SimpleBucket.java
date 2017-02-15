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
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;

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
     * @param plugin
	 * @param type SimpleBucketType object describing this bucket.
	 * @param emptyBucket ItemStack form of the emptyBucket item.
	 */
	public SimpleBucket(Plugin plugin, SimpleBucketType type, ItemStack emptyBucket) 
	{
		this.capacity = Fluid.BUCKET_VOLUME;
		this.bucketType = type;
		this.plugin = plugin;
        this.setMaxStackSize(1);
        this.nbtSensitive = false;
        this.emptyBucketStack = emptyBucket;
        
        // allows to work with dispensers
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, DispenseFluidContainer.getInstance());
	} // end ctor

	/**
	 * constructor for empty bucket.
	 * @param plugin
	 * @param type SimpleBucketType object describing this bucket.
	 */
    public SimpleBucket(Plugin plugin, SimpleBucketType type) 
    {
        this(plugin, type, ItemStackTools.getEmptyStack());
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
            if(ItemStackTools.isValid(this.getEmpty()))
            {
                return this.getEmpty().getDisplayName();
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
        if (ItemStackTools.getStackSize(container) != 1)
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
            || (isNbtSensitive() && ItemStack.areItemStackTagsEqual(emptyBucket, this.getEmpty()))
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
        else if (this.bucketType.getDestroyOnLava())
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

    /*
     * CompatItem override for OnItemRightClick().
     */
    @Override
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
                    ItemStackTools.incStackSize(itemstack, -1);
                    ItemStack emptyStack = ItemStackTools.isValid(this.getEmpty()) 
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
                } // end-if tryPlaceFluid
            } // end-if canPlayerEdit
        } // end-if BlockModifiable

        // couldn't place or remove liquid there
        return ActionResult.newResult(EnumActionResult.FAIL, itemstack);
    } // end onItemRightClick()

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    @SideOnly(Side.CLIENT)
    @Override
    public void clGetSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems)
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
        if (ItemStackTools.isValid(this.getEmpty()))
        {
            // Create a copy such that the game can't mess with it
            return getEmpty().copy();
        }
        return super.getContainerItem(itemStack);
    }

    @Override
    public boolean hasContainerItem(ItemStack stack)
    {
        return ItemStackTools.isValid(this.getEmpty());
    }


    @Override
    public FluidStack drain(ItemStack container, int maxDrain, boolean doDrain)
    {
        // has to be exactly 1, must be handled from the caller
        if (ItemStackTools.getStackSize(container) != 1)
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
            if(ItemStackTools.isValid(getEmpty()))
            {
                container.deserializeNBT(getEmpty().serializeNBT());
            }
            else {
                ItemStackTools.setStackSize(container, 0);
            }
        }
        return fluidStack;
    } // end drain()

} // end class
