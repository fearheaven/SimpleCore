/**
 * 
 */
package alexndr.api.helpers.game;

import mcjty.lib.tools.ItemStackList;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.nbt.NBTTagCompound;

/**
 * @author cyhiggin
 * Uses CompatLayer's ItemStackList and provides backwards-compatible versions of 
 * helper functions added in 1.11.
 */
public class SimpleItemStackHelper extends ItemStackHelper 
{

    public static void read_itemStackFromNBT(NBTTagCompound compound, ItemStackList stacklist)
    {
    	ItemStackHelper.func_191283_b(compound, stacklist);
    }
    
    public static NBTTagCompound write_itemStackToNBT(NBTTagCompound compound, ItemStackList stacklist)
    {
    	return ItemStackHelper.func_191282_a(compound, stacklist);
    }

} // end class
