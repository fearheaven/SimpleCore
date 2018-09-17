package alexndr.api.helpers.game;

import alexndr.api.content.tiles.TileEntityBaseFurnace;
import net.minecraft.item.ItemStack;

public class FurnaceTileStackHandler extends TileStackHandler {

	public FurnaceTileStackHandler(int size, TileEntityBaseFurnace baseTile) 
	{
		super(size, baseTile);
	}

    /**
     * Inserts an ItemStack into the given slot and return the remainder.
     * The ItemStack should not be modified in this function!
     * Note: This behaviour is subtly different from IFluidHandlers.fill()
     *
     * @param slot     Slot to insert into.
     * @param stack    ItemStack to insert. This must not be modified by the item handler.
     * @param simulate If true, the insertion is only simulated
     * @return The remaining ItemStack that was not inserted (if the entire stack is accepted, then return an empty ItemStack).
     *         May be the same as the input ItemStack if unchanged, otherwise a new ItemStack.
     *         The returned ItemStack can be safely modified after.
     **/
	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) 
	{
		if (slot == TileEntityBaseFurnace.NDX_INPUT_SLOT)
		{
			// only accept smeltable items
			if (! ((TileEntityBaseFurnace)base_tile).isItemSmeltable(stack)) {
				return stack; // unchanged input means not accepted
			}
		}
		else if (slot == TileEntityBaseFurnace.NDX_FUEL_SLOT)
		{
			// only accept fuel items
			if (! ((TileEntityBaseFurnace) base_tile).isItemFuel(stack))
			{
				return stack; // unchanged input means not accepted
			}
		}
		else if (slot == TileEntityBaseFurnace.NDX_OUTPUT_SLOT)
		{
			// no special handling
			return super.insertItem(slot, stack, simulate);
		}
		return super.insertItem(slot, stack, simulate);
	} // end insertItem()

} // end-class
