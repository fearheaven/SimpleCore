package alexndr.api.content.items;

import javax.annotation.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStackSimple;

public class SimpleBucketFluidHandler extends FluidHandlerItemStackSimple.SwapEmpty
{
    protected SimpleBucketType bucketType;

	public SimpleBucketFluidHandler(ItemStack container, ItemStack emptyContainer, 
									int capacity, SimpleBucketType type) 
	{
		super(container, emptyContainer, capacity);
		this.bucketType = type;
	}

	@Override
	public boolean canFillFluidType(FluidStack fluid) 
	{
		return bucketType.doesVariantExist(fluid.getFluid());
	}

	@Override
	public boolean canDrainFluidType(FluidStack fluid) 
	{
		return bucketType.doesVariantExist(fluid.getFluid());
	}

    @Override
    public int fill(FluidStack resource, boolean doFill)
    {
        if (container.stackSize != 1 || resource == null || resource.amount < Fluid.BUCKET_VOLUME
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

	@SuppressWarnings("deprecation")
	@Override
	protected void setFluid(FluidStack fluid) 
	{
		if (!bucketType.doesVariantExist(fluid.getFluid())) {
			return;
		}
		if (fluid.getFluid() == FluidRegistry.WATER)
        {
            container.setItem(bucketType.getBucketFromLiquid(FluidRegistry.WATER));
            container.setTagCompound(null);
            container.setItemDamage(0);
        }
        else if (fluid.getFluid() == FluidRegistry.LAVA
        		 && ! bucketType.getDestroyOnLava())
        {
            container.setItem(bucketType.getBucketFromLiquid(FluidRegistry.LAVA));
            container.setTagCompound(null);
            container.setItemDamage(0);
        }
        else if (fluid.getFluid().getName().equals("milk"))
        {
            container.setItem(bucketType.getBucketFromLiquid(FluidRegistry.getFluid("milk")));
            container.setTagCompound(null);
            container.setItemDamage(0);
        }
        else {
        	super.setFluid(fluid);
        }
	} // end setFluid()

	@Override
	@Nullable
	public FluidStack getFluid() 
	{
		Item item = container.getItem();
		if (item == bucketType.getBucketFromLiquid(FluidRegistry.WATER))
		{
			return new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME);
		}
		else if (item == bucketType.getBucketFromLiquid(FluidRegistry.LAVA))
		{
			return new FluidStack(FluidRegistry.LAVA, Fluid.BUCKET_VOLUME);
		}
		else if (item == bucketType.getBucketFromLiquid(FluidRegistry.getFluid("milk")))
		{
			return FluidRegistry.getFluidStack("milk", Fluid.BUCKET_VOLUME);
		}
		else
		{
			return super.getFluid();
		}
	} // end getFluid()
	
} // end class
