/**
 * 
 */
package alexndr.api.helpers.game;

import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;

/**
 * @author cyhiggin
 * Uses CompatLayer's ItemStackList and provides backwards-compatible versions of 
 * helper functions added in 1.11.
 */
public class SimpleItemStackHelper extends ItemStackHelper 
{
    public static void read_itemStackFromNBT(NBTTagCompound compound, NonNullList<ItemStack> stacklist)
    {
    	ItemStackHelper.loadAllItems(compound, stacklist);
    }
    
    public static NBTTagCompound write_itemStackToNBT(NBTTagCompound compound, 
    												  NonNullList<ItemStack> stacklist)
    {
    	ItemStackHelper.saveAllItems(compound, stacklist, true);
        return compound;
    } // end write_itemStackToNBT()

} // end class
