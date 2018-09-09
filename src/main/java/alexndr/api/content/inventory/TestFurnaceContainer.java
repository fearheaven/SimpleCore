package alexndr.api.content.inventory;

import javax.annotation.Nonnull;

import alexndr.api.content.tiles.TestFurnaceTileEntity;
import alexndr.api.content.tiles.TileEntityBaseFurnace;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.items.IItemHandler;
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

    /**
     * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
     * inventory and the other inventory(s).
     */
	@Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index == TileEntityBaseFurnace.NDX_OUTPUT_SLOT)
            {
                if (!this.mergeItemStack(itemstack1, 3, 39, true))
                {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(itemstack1, itemstack);
            }
            else if (index != TileEntityBaseFurnace.NDX_FUEL_SLOT 
            		&& index != TileEntityBaseFurnace.NDX_INPUT_SLOT)
            {
                if (!FurnaceRecipes.instance().getSmeltingResult(itemstack1).isEmpty())
                {
                    if (!this.mergeItemStack(itemstack1, 0, 1, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
                else if (TileEntityFurnace.isItemFuel(itemstack1))
                {
                    if (!this.mergeItemStack(itemstack1, 1, 2, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
                else if (index >= 3 && index < 30)
                {
                    if (!this.mergeItemStack(itemstack1, 30, 39, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
                else if (index >= 30 && index < 39 && !this.mergeItemStack(itemstack1, 3, 30, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 3, 39, false))
            {
                return ItemStack.EMPTY;
            }
            
            if (itemstack1.isEmpty())
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount())
            {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemstack1);
        }

        return itemstack;
    } // end transferStackInSlot()
    
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
				new InputSlotItemHandler(itemhandler, TileEntityBaseFurnace.NDX_INPUT_SLOT, 56, 17));
		this.addSlotToContainer(
				new FuelSlotItemHandler(itemhandler, TileEntityBaseFurnace.NDX_FUEL_SLOT, 56, 53));
		this.addSlotToContainer(
				new OutputSlotItemHandler(itemhandler, TileEntityBaseFurnace.NDX_OUTPUT_SLOT, 116, 35) );
	} // end AddOwnSlots()

    /** 
     * inner class that sub-classes SlotItemHandler; handles checking for validity of fuel stacks
     */
	protected class FuelSlotItemHandler extends SlotItemHandler
	{
		public FuelSlotItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition) 
		{
			super(itemHandler, index, xPosition, yPosition);
		}
		
	    /**
	     * Check if the stack is allowed to be placed in this slot, used for furnace fuel.
	     */
	    @Override
	    public boolean isItemValid(@Nonnull ItemStack stack)
	    {
	        if (stack.isEmpty())
	            return false;
	        if (! TestFurnaceContainer.this.tileFurnace.isItemFuel(stack))
	    		return false;
	    	return super.isItemValid(stack);
	    }
	} // end-class FuelSlotItemHandler
	
    /** 
     * inner class that sub-classes SlotItemHandler; handles checking for validity of input stacks
     */
	protected class InputSlotItemHandler extends SlotItemHandler
	{
		public InputSlotItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition) 
		{
			super(itemHandler, index, xPosition, yPosition);
		}
		
	    /**
	     * Check if the stack is allowed to be placed in this slot, used for smeltable items.
	     */
	    @Override
	    public boolean isItemValid(@Nonnull ItemStack stack)
	    {
	        if (stack.isEmpty())
	            return false;
            ItemStack hypothetical_result = FurnaceRecipes.instance().getSmeltingResult(stack);
            if (hypothetical_result.isEmpty()) 
            	return false;
	    	return super.isItemValid(stack);
	    }
	} // end class InputSlotItemHandler
	
	/**
	 * inner class that sub-classes SlotItemHander; handles making output slot unmodifiable.
	 */
	protected class OutputSlotItemHandler extends SlotItemHandler
	{
		public OutputSlotItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition) 
		{
			super(itemHandler, index, xPosition, yPosition);
		}
		
	    /**
	     * Check if the stack is allowed to be placed in this slot; used for output-only slots.
	     */
	    @Override
	    public boolean isItemValid(@Nonnull ItemStack stack)
	    {
	    	return false;
	    }
		
	} // end class OutputSlotItemHandler
} // end class
