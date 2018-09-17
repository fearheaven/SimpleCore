package alexndr.api.helpers.game;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

/**
	 * sub-classes SlotItemHander; handles making output slot unmodifiable.
	 */
	public class OutputSlotItemHandler extends SlotItemHandler
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
