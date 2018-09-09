package alexndr.api.content.tiles;

import alexndr.api.content.inventory.TestFurnaceContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

public class TestFurnaceTileEntity extends TileEntityBaseFurnace 
{
	public final static String tilename = "container.test_furnace";
	public final static String guiID = "simplecore:test_furnace_gui";
	
	public TestFurnaceTileEntity()
	{
		super(TestFurnaceTileEntity.tilename, 600, TestFurnaceTileEntity.guiID, 3);
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
        return TileEntityBaseFurnace.getItemBurnTime(stack) > 0;
    }


} // end class
