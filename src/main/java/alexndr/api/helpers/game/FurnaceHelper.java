package alexndr.api.helpers.game;

import mcjty.lib.tools.ItemStackList;
import net.minecraft.item.ItemStack;

/**
 * Version-specific helper functions for Furnace code. Necessary because method signatures get 
 * weird when inheriting across versions.
 * 
 * @author Sinhika
 */
public class FurnaceHelper
{
    public static ItemStack SetInSlot(ItemStackList list, int index, ItemStack stack)
    {
        return list.set(index, stack);
    }
} // end class
