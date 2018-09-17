/*
 * This file ("TileEntityBaseInventory.java") is part of the Minecraft mod project SimpleCore. 
 * It is created and owned by Sinhika and Skrallexxx and distributed under the Lesser GNU Public 
 * License v3.0, a copy of which is included in the root of this source tree ("lgpl.txt") or 
 * otherwise readable at <http://fsf.org/>.
 * 
 * Parts of this file are also based on Actually Additions source code by Ellpeck. See
 * <https://minecraft.curseforge.com/projects/actually-additions> for details of that mod.
 * 
 * (c) 2014-2017 Sinhika and Skrallexxx (aka AleXndrTheGr8st)
 */
package alexndr.api.content.tiles;

import alexndr.api.helpers.game.TileStackHandler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

public abstract class TileEntityBaseInventory extends TileEntityBase 
{

	protected int slotCount;
	
	/** IItemHandler-based inventory */
	protected ItemStackHandler slotHandler;

	public TileEntityBaseInventory(int inv_stack_count) 
	{
		super();
		this.slotCount = inv_stack_count;
	    slotHandler = new TileStackHandler(inv_stack_count, this);
	} //ctor

	/** override to handle specific interesting changes to specific slots */
//	public void onSlotChanged(int slot) {
//	
//	} // end-onSlotChanged()
	
	public ItemStackHandler getSlotHandler() {
		return this.slotHandler;
	}

    public boolean shouldSyncSlots() {
        return false;
    }

	@Override
	public void writeSyncableNBT(NBTTagCompound compound, NBTType type) 
	{
		super.writeSyncableNBT(compound, type);
//		if(type == NBTType.SAVE_TILE || (type == NBTType.SYNC && this.shouldSyncSlots())) 
		compound.setTag("items", slotHandler.serializeNBT());
	} // end writeSyncableNBT()

	@Override
	public void readSyncableNBT(NBTTagCompound compound, NBTType type) 
	{
		super.readSyncableNBT(compound, type);
//		if(type == NBTType.SAVE_TILE || (type == NBTType.SYNC && this.shouldSyncSlots()))
		slotHandler.deserializeNBT((NBTTagCompound) compound.getTag("items"));
	} // end readSyncableNBT()

	@Override
	public void markDirty()
	{
		super.markDirty();
//        if (this.world != null)
//        {
//        	IBlockState blockstate = this.getWorld().getBlockState(this.pos);
//        	this.getWorld().notifyBlockUpdate(this.pos, blockstate, blockstate, 2);
//        }
	} // end markDirty()

    @Override
    public int getComparatorStrength() {
        return ItemHandlerHelper.calcRedstoneFromInventory(this.slotHandler);
    }
	
    
} // end class