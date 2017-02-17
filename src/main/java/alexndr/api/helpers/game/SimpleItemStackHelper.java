/**
 * 
 */
package alexndr.api.helpers.game;

import mcjty.lib.tools.ItemStackList;
import mcjty.lib.tools.ItemStackTools;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

/**
 * @author cyhiggin
 * Uses CompatLayer's ItemStackList and provides backwards-compatible versions of 
 * helper functions added in 1.11.
 */
public class SimpleItemStackHelper extends ItemStackHelper 
{
    public static ItemStack getAndSplit(ItemStackList stacks, int index, int amount)
    {
    	return ItemStackHelper.getAndSplit(stacks, index, amount);
    }

    public static ItemStack getAndRemove(ItemStackList stacks, int index)
    {
    	return ItemStackHelper.getAndRemove(stacks, index);
    }
    
    public static void read_itemStackFromNBT(NBTTagCompound compound, ItemStackList stacklist)
    {
        NBTTagList nbttaglist = compound.getTagList("Items", 10);

        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
            int j = nbttagcompound.getByte("Slot") & 255;

            if (j >= 0 && j < stacklist.size())
            {
                stacklist.set(j, ItemStack.loadItemStackFromNBT(nbttagcompound));
            }
        }
    }
    
    public static NBTTagCompound write_itemStackToNBT(NBTTagCompound compound, 
                    ItemStackList stacklist)
    {
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < stacklist.size(); ++i)
        {
            ItemStack itemstack = (ItemStack)stacklist.get(i);

            if (ItemStackTools.isValid(itemstack))
            {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte)i);
                itemstack.writeToNBT(nbttagcompound);
                nbttaglist.appendTag(nbttagcompound);
            }
        }

        if (!nbttaglist.hasNoTags())
        {
            compound.setTag("Items", nbttaglist);
        }

        return compound;
    } // end write_itemStackToNBT()

} // end class
