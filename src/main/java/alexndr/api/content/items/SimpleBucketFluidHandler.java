package alexndr.api.content.items;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStackSimple;

public class SimpleBucketFluidHandler extends FluidHandlerItemStackSimple
{
	protected SimpleBucketType bucketType;

	public SimpleBucketFluidHandler(ItemStack container, int capacity, SimpleBucketType type) 
	{
		super(container, capacity);
		this.bucketType = type;
	}

	public int getCapacity() 
	{
		return capacity;
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
	public FluidStack drain(FluidStack resource, boolean doDrain) 
    {
        if (resource == null || resource.amount < Fluid.BUCKET_VOLUME)
        {
            return null;
        }

        return super.drain(resource.amount, doDrain);
	} // end drain(FluidStack, boolean)


	@Override
    public int fill(FluidStack resource, boolean doFill)
    {
	    // don't bother with 'lava check' if nothing there.
        if ( resource == null || resource.amount <= 0 ) 
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

        // otherwise, let parent handle it.
        return super.fill(resource, doFill);
    } // end fill()

	@Override
	public void setContainerToEmpty()
	{
	    super.setContainerToEmpty();
	}
	
} // end class
