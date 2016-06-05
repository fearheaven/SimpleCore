package alexndr.api.content.items;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
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
        if (container.stackSize != 1 || resource == null || resource.amount <= 0
        	|| !canFillFluidType(resource)) 
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

        FluidStack contained = getFluid();
        if (contained == null)
        {
            int fillAmount = Math.min(capacity, resource.amount);
            if (fillAmount == capacity) {
                if (doFill) {
                    FluidStack filled = resource.copy();
                    filled.amount = fillAmount;
                    setFluid(filled);
                }

                return fillAmount;
            }
        }

        return 0;
    } // end fill()
	
} // end class
