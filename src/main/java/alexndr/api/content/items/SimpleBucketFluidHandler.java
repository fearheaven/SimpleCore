package alexndr.api.content.items;

import javax.annotation.Nullable;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class SimpleBucketFluidHandler implements IFluidHandler, ICapabilityProvider
{
	protected SimpleBucketType bucketType;
    protected final ItemStack container;
    public static final String FLUID_NBT_KEY = "Fluid";

	public SimpleBucketFluidHandler(ItemStack container, SimpleBucketType type) 
	{
        this.container = container;
		this.bucketType = type;
	}

	public boolean canFillFluidType(FluidStack fluid) 
	{
		return bucketType.doesVariantExist(fluid);
	}

	public boolean canDrainFluidType(FluidStack fluid) 
	{
		return bucketType.doesVariantExist(fluid);
	}

    @Nullable
    public FluidStack getFluid()
    {
        Item item = container.getItem();
        if (item instanceof SimpleBucket)
        {
            return ((SimpleBucket) item).getFluid(container);
        }
        else
        {
            return null;
        }
    }

    protected void setFluid(Fluid fluid)
    {
        if (fluid == null)
        {
            container.deserializeNBT(new ItemStack(container.getItem()).serializeNBT());
        }
        else if (bucketType.doesVariantExist(fluid))
        {
            ItemStack filledBucket = 
                 SimpleBucket.getFilledBucket((SimpleBucket) container.getItem(), fluid);
            container.deserializeNBT(filledBucket.serializeNBT());
        }
    } // end setFluid()
    
    @Override
    public IFluidTankProperties[] getTankProperties()
    {
        return new FluidTankProperties[] { new FluidTankProperties(getFluid(), Fluid.BUCKET_VOLUME) };
    }

    @Override
    public int fill(FluidStack resource, boolean doFill)
    {
        // don't bother with 'lava check' if nothing there.
        if (container.stackSize != 1 ||  resource == null || resource.amount <= 0 ) 
        {
            return 0;
        }
        // lava check -- trying to dip bucket into something that will melt it?
        if (bucketType.getDestroyOnLava())
        {
            Fluid liquid = resource.getFluid();
            if (liquid.getTemperature() >= SimpleBucketType.DESTROY_ON_LAVA_TEMP)
            {
                // No, we didn't fill the bucket, because it's melting.
                return 0;
            }
        } // if bucket-type will melt

        if (resource.amount <= Fluid.BUCKET_VOLUME || !canFillFluidType(resource))
        {
            return 0;
        }

        if (doFill)
        {
            setFluid(resource.getFluid());
        }

        return Fluid.BUCKET_VOLUME;
    } // end fill()

   @Override
	public FluidStack drain(FluidStack resource, boolean doDrain) 
    {
        if (container.stackSize != 1 || resource == null || resource.amount < Fluid.BUCKET_VOLUME)
        {
            return null;
        }

        FluidStack fluidStack = getFluid();
        if (fluidStack != null && fluidStack.isFluidEqual(resource))
        {
            if (doDrain)
            {
                setFluid(null);
            }
            return fluidStack;
        }

        return null;
	} // end drain(FluidStack, boolean)

   @Nullable
   @Override
   public FluidStack drain(int maxDrain, boolean doDrain)
   {
       if (container.stackSize != 1 || maxDrain < Fluid.BUCKET_VOLUME)
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
   }

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
	    return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
	    if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
	    {
	        return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(this);
	    }
	    return null;
	}

} // end class
