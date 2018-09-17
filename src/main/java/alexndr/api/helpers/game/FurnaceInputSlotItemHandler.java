package alexndr.api.helpers.game;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

/** 
 * sub-classes SlotItemHandler; handles checking for validity of input stacks
 */
public class FurnaceInputSlotItemHandler extends SlotItemHandler 
{

	public FurnaceInputSlotItemHandler(IItemHandler itemHandler, int index, int xPosition, 
										 int yPosition) 
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
} // end-class
