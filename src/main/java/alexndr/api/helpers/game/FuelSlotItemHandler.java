package alexndr.api.helpers.game;

import javax.annotation.Nonnull;

import alexndr.api.content.tiles.TileEntityBaseFurnace;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

/** 
     * sub-classes SlotItemHandler; handles checking for validity of fuel stacks
     */
	public class FuelSlotItemHandler extends SlotItemHandler
	{
		TileEntityBaseFurnace teFurnace;
		
		public FuelSlotItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition,
								   TileEntityBaseFurnace tile_entity) 
		{
			super(itemHandler, index, xPosition, yPosition);
			teFurnace = tile_entity;
		}
		
	    /**
	     * Check if the stack is allowed to be placed in this slot, used for furnace fuel.
	     */
	    @Override
	    public boolean isItemValid(@Nonnull ItemStack stack)
	    {
	        if (stack.isEmpty())
	            return false;
	        if (! teFurnace.isItemFuel(stack))
	    		return false;
	    	return super.isItemValid(stack);
	    }
	} // end-class FuelSlotItemHandler
