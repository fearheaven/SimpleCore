package alexndr.api.helpers.game;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

/**
 * Version-specific helper functions for Furnace code. Necessary because method signatures get 
 * weird when inheriting across versions.
 * 
 * @author Sinhika
 */
public class FurnaceHelper
{
    public static ItemStack SetInSlot(NonNullList<ItemStack> list, int index, ItemStack stack)
    {
        return list.set(index, stack);
    }
} // end class
