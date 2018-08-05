package alexndr.api.content.tiles;

import alexndr.api.content.inventory.TestFurnaceContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;

public class TestFurnaceTileEntity extends TileEntityBaseFurnace 
{
	public final static String tilename = "simplecore:container.test_furnace";
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

} // end class
