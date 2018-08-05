package alexndr.api.content.inventory;

import alexndr.api.content.tiles.TestFurnaceTileEntity;
import alexndr.api.content.tiles.TileEntityBaseFurnace;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class TestFurnaceContainer extends Container 
{
	protected TestFurnaceTileEntity tileFurnace;
	
	public TestFurnaceContainer(InventoryPlayer player, TestFurnaceTileEntity tileentity) 
	{
		this.tileFurnace = tileentity;
		AddOwnSlots();
		AddPlayerSlots(player);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) 
	{
        return this.tileFurnace.canInteractWith(playerIn);
	}

	private void AddPlayerSlots(InventoryPlayer player) 
	{
		// main player inventory slots
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) 
				this.addSlotToContainer(new Slot(player, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
		}
		// player hotbar slots
		for (int i = 0; i < 9; ++i) 
			this.addSlotToContainer(new Slot(player, i, 8 + i * 18, 142));
	} // end AddPlayerSlots()

	private void AddOwnSlots() 
	{
		ItemStackHandler itemhandler = tileFurnace.getSlotHandler();
		this.addSlotToContainer(
				new SlotItemHandler(itemhandler, TileEntityBaseFurnace.NDX_INPUT_SLOT, 56, 17));
		this.addSlotToContainer(
				new SlotItemHandler(itemhandler, TileEntityBaseFurnace.NDX_FUEL_SLOT, 56, 53));
		this.addSlotToContainer(
				new SlotItemHandler(itemhandler, TileEntityBaseFurnace.NDX_OUTPUT_SLOT, 116, 35) );
	} // end AddOwnSlots()

} // end class
