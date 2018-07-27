/*
 * This file ("TileEntityBase.java") is part of the Minecraft mod project SimpleCore. 
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

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

/**
 * A TileEntity class with some standard NBT and network sync handling methods added. 
 *
 */
public abstract class TileEntityBase extends TileEntity implements ITickable
{
    public enum NBTType {
        SAVE_TILE,
        SYNC,
        SAVE_BLOCK
    }

	@Override
	public abstract void update();
    
	public void writeSyncableNBT(NBTTagCompound compound, NBTType type)
	{
        if(type != NBTType.SAVE_BLOCK) {
            super.writeToNBT(compound);
        }
	} // end writeSyncableNBT()
	
	public void readSyncableNBT(NBTTagCompound compound, NBTType type)
	{
		if (type != NBTType.SAVE_BLOCK) {
			super.readFromNBT(compound);
		}
	}

	/**
	 * read NBT data from permanent storage. Class-specific details handled by
	 * readSyncableNBT().
	 * 
	 * @param compound NBT tag to be filled in by read.
	 * @see readSyncableNBT().
	 */
	@Override
	public final void readFromNBT(NBTTagCompound compound) 
	{
		this.readSyncableNBT(compound, NBTType.SAVE_TILE);	
	}

	/**
	 * Save NBT data to permanent storage. Actual class-specific details handled
	 * by writeSyncableNBT().
	 * 
	 * @param compound NBT tag to be written to storage.
	 * @see writeSyncableNBT().
	 */
	@Override
	public final NBTTagCompound writeToNBT(NBTTagCompound compound) 
	{
		this.writeSyncableNBT(compound, NBTType.SAVE_TILE);
		return compound;
	}

	public int getComparatorStrength()
	{
        return 0;
    }

	@Override
	public final SPacketUpdateTileEntity getUpdatePacket() 
	{
        NBTTagCompound compound = new NBTTagCompound();
        this.writeSyncableNBT(compound, NBTType.SYNC);
        return new SPacketUpdateTileEntity(this.pos, -1, compound);
	}

	@Override
	public final NBTTagCompound getUpdateTag() 
	{
        NBTTagCompound compound = new NBTTagCompound();
        this.writeSyncableNBT(compound, NBTType.SYNC);
        return compound;
	}

	@Override
	public final void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) 
	{
		this.readSyncableNBT(pkt.getNbtCompound(), NBTType.SYNC);
	}

	@Override
	public final void handleUpdateTag(NBTTagCompound tag) {
		 this.readSyncableNBT(tag, NBTType.SYNC);
	}
	
//	public final void sendUpdate() 
//	{
//		if(this.world != null && !this.world.isRemote)
//		{
//			NBTTagCompound compound = new NBTTagCompound();
//			this.writeSyncableNBT(compound, NBTType.SYNC);
//			NBTTagCompound data = new NBTTagCompound();
//			data.setTag("Data", compound);
//			data.setInteger("X", this.pos.getX());
//			data.setInteger("Y", this.pos.getY());
//			data.setInteger("Z", this.pos.getZ());
//			SimpleCorePacketHandler.INSTANCE.sendToAllAround(
//				new MessageTileEntityServer(data), 
//				new NetworkRegistry.TargetPoint(this.world.provider.getDimension(), 
//												this.getPos().getX(), this.getPos().getY(), 
//												this.getPos().getZ(), 64));
//		} // end-if !isRemote
//	} // end sendUpdate()
	
} // end class
