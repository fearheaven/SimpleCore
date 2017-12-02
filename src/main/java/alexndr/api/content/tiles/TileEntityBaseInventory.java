package alexndr.api.content.tiles;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

public abstract class TileEntityBaseInventory extends TileEntityBase 
{

	protected int slotCount;
	
	/** IItemHandler-based inventory */
	protected ItemStackHandler slotHandler;

	public TileEntityBaseInventory(int furnace_stack_count) 
	{
		super();
		this.slotCount = furnace_stack_count;
	    slotHandler = new ItemStackHandler(furnace_stack_count) {
	        @Override
	        protected void onContentsChanged(int slot) {
	        	TileEntityBaseInventory.this.markDirty();
	        }
	    };
	} //ctor

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
		if(type == NBTType.SAVE_TILE || (type == NBTType.SYNC && this.shouldSyncSlots())) 
		{
	        compound.setTag("items", slotHandler.serializeNBT());
		}
	} // end writeSyncableNBT()

	@Override
	public void readSyncableNBT(NBTTagCompound compound, NBTType type) 
	{
		super.readSyncableNBT(compound, type);
		if(type == NBTType.SAVE_TILE || (type == NBTType.SYNC && this.shouldSyncSlots()))
		{
			slotHandler.deserializeNBT((NBTTagCompound) compound.getTag("items"));
		}
	} // end readSyncableNBT()

	@Override
	public void markDirty()
	{
		super.markDirty();
		if(this.shouldSyncSlots()){
			this.sendUpdate();
		}
	} // end markDirty()

    @Override
    public int getComparatorStrength() {
        return ItemHandlerHelper.calcRedstoneFromInventory(this.slotHandler);
    }
	
} // end class