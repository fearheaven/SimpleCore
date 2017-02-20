package alexndr.api.content.items;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class SimpleBucketFluidHandler implements IFluidHandlerItem, ICapabilityProvider
{
	protected SimpleBucketType bucketType;
    public static final String FLUID_NBT_KEY = "Fluid";
    protected final ItemStack emptyContainer;
    protected ItemStack container;
    protected final int capacity;


	public SimpleBucketFluidHandler(ItemStack container, ItemStack emptyContainer, 
									int capacity, SimpleBucketType type) 
	{
        this.container = container;
        this.capacity = capacity;
        this.emptyContainer = emptyContainer;
		this.bucketType = type;
	}

	public int getCapacity() 
	{
		return capacity;
	}
	
	public boolean canFillFluidType(FluidStack fluid) 
	{
		return bucketType.doesVariantExist(fluid.getFluid());
	}

	public boolean canDrainFluidType(FluidStack fluid) 
	{
		return bucketType.doesVariantExist(fluid.getFluid());
	}
	
    @Override
	public FluidStack drain(FluidStack resource, boolean doDrain) 
    {
        if (resource == null || resource.amount < Fluid.BUCKET_VOLUME)
        {
            return null;
        }

        return drain(resource.amount, doDrain);
	} // end drain(FluidStack, boolean)


    @Override
	public FluidStack drain(int maxDrain, boolean doDrain) 
    {
        if (maxDrain < Fluid.BUCKET_VOLUME)
        {
            return null;
        }

        FluidStack fluidStack = getFluid();
        if (fluidStack != null)
        {
            if (doDrain)
            {
                setFluid(null);
            }
            return fluidStack;
        }

        return null;
	} // end drain(int, boolean)

	@Override
    public int fill(FluidStack resource, boolean doFill)
    {
        if ( resource == null || resource.amount < Fluid.BUCKET_VOLUME
        	|| getFluid() != null || !canFillFluidType(resource)) 
        {
            return 0;
        }
        // lava check -- trying to dip bucket into something that will melt it?
        // (and we were silly enough to declare it as a valid variant)
        if (bucketType.getDestroyOnLava())
        {
        	Fluid liquid = resource.getFluid();
        	if (liquid.getTemperature() >= SimpleBucketType.DESTROY_ON_LAVA_TEMP)
        	{
        		// No, we didn't fill the bucket, because it's melting.
        		return 0;
        	}
        } // if bucket-type will melt

        if (doFill) {
        	setFluid(resource);
        }
        return Fluid.BUCKET_VOLUME;
    } // end fill()

	protected void setFluid(FluidStack fluid) 
	{
		if (fluid == null || fluid.getFluid() == null)
		{
            setContainerToEmpty();
            return;
		}
		else if (fluid.getFluid() == FluidRegistry.WATER)
        {
            container = new ItemStack(bucketType.getBucketFromLiquid(FluidRegistry.WATER));
            container.setTagCompound(null);
        }
        else if (fluid.getFluid() == FluidRegistry.LAVA
        		 && ! bucketType.getDestroyOnLava())
        {
            container = new ItemStack(bucketType.getBucketFromLiquid(FluidRegistry.LAVA));
            container.setTagCompound(null);
        }
        else {
            container = new ItemStack(bucketType.getBucketFromLiquid(fluid.getFluid()));
            if (!container.hasTagCompound())
            {
                container.setTagCompound(new NBTTagCompound());
            }

            NBTTagCompound fluidTag = new NBTTagCompound();
            fluid.writeToNBT(fluidTag);
            container.getTagCompound().setTag(FLUID_NBT_KEY, fluidTag);
        }
	} // end setFluid()

	@Nullable
	public FluidStack getFluid() 
	{
		if (container.isItemEqualIgnoreDurability(emptyContainer))
		{
			return null;
		}
		else if (container.getItem() == bucketType.getBucketFromLiquid(FluidRegistry.WATER))
		{
			return new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME);
		}
		else if (container.getItem() == bucketType.getBucketFromLiquid(FluidRegistry.LAVA))
		{
			return new FluidStack(FluidRegistry.LAVA, Fluid.BUCKET_VOLUME);
		}
		else
		{
	        NBTTagCompound tagCompound = container.getTagCompound();
	        if (tagCompound == null || !tagCompound.hasKey(FLUID_NBT_KEY))
	        {
	            return null;
	        }
	        return FluidStack.loadFluidStackFromNBT(tagCompound.getCompoundTag(FLUID_NBT_KEY));
		}
	} // end getFluid()

	@Override
	public IFluidTankProperties[] getTankProperties() 
	{
        return new FluidTankProperties[] { new FluidTankProperties(getFluid(), Fluid.BUCKET_VOLUME) };
	}

    /**
     * Override this method for special handling.
     * Can be used to swap out the container's item for a different one with "container.setItem".
     * Can be used to destroy the container with "container.stackSize--"
     */
	protected void setContainerToEmpty() 
	{
		// we don't want to mess with the tagcompound if it doesn't exist...
		if (container.hasTagCompound()) {
	        container.getTagCompound().removeTag(FLUID_NBT_KEY);
	    }
		// but we still want to update other nbt tags if they exist.
		else {
            container.deserializeNBT(emptyContainer.serializeNBT());
		}
	} // end setContainerToEmpty()
	
    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
    	// note change of capability from 1.10.2 to 1.11.2
        return capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
    	// note change of capability from 1.10.2 to 1.11.2
        return capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY ? (T) this : null;
    }

	@Override
	public ItemStack getContainer() {
		return container;
	}

} // end class
