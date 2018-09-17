package alexndr.api.content.inventory;

import alexndr.api.content.tiles.TestFurnaceTileEntity;
import net.minecraft.entity.player.InventoryPlayer;

public class TestFurnaceContainer extends SimpleFurnaceContainer 
{
	
	public TestFurnaceContainer(InventoryPlayer player, TestFurnaceTileEntity tileentity) 
	{
		super(player, tileentity);
	}
 	
} // end class
