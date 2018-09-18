package alexndr.api.content.tiles;

import alexndr.api.content.blocks.TestFurnace;
import alexndr.api.content.inventory.TestFurnaceContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

public class TestFurnaceTileEntity extends TileEntitySimpleFurnace 
{
	public final static String tilename = "container.test_furnace";
	public final static String guiID = "simplecore:test_furnace_gui";
	
	public TestFurnaceTileEntity()
	{
		super(TestFurnaceTileEntity.tilename, 200, TestFurnaceTileEntity.guiID, 3);
	}
	
	@Override
	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) 
	{
        return new TestFurnaceContainer(playerInventory, this);	
    }

    /**
     * Returns true if item stack is fuel for this furnace, false if is not
     */
	@Override
    public boolean isItemFuel(ItemStack stack)
    {
        return TileEntitySimpleFurnace.getItemBurnTime(stack) > 0;
    }

	/* (non-Javadoc)
	 * @see alexndr.api.content.tiles.TileEntitySimpleFurnace#update()
	 */
	@Override
	public void update() {
        boolean was_burning_flag = this.isBurning();
        boolean flag1 = false;
        int burnTime = 0;
        
        if (this.isBurning())
        {
            --this.furnaceBurnTime;
        }

        if (!this.getWorld().isRemote)
        {
            ItemStack fuelstack = (ItemStack)this.getStackInSlot(NDX_FUEL_SLOT);
            if (!fuelstack.isEmpty()) 
			{
                burnTime = TileEntitySimpleFurnace.getItemBurnTime(fuelstack);
            }
            flag1 = default_cooking_update(flag1, fuelstack, burnTime);
            
            if (was_burning_flag != this.isBurning())
            {
                flag1 = true;
                TestFurnace.setState(this.isBurning(), this.getWorld(), this.pos);
            } // end-if
        } // end-if

        if (flag1)
        {
            this.markDirty();
        }
	}


} // end class
