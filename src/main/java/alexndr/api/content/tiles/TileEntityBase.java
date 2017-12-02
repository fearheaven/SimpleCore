package alexndr.api.content.tiles;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

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

	@Override
	public final void readFromNBT(NBTTagCompound compound) 
	{
		this.readSyncableNBT(compound, NBTType.SAVE_TILE);	
	}

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
	
	public final void sendUpdate() 
	{
		if(this.world != null && !this.world.isRemote)
		{
			NBTTagCompound compound = new NBTTagCompound();
			this.writeSyncableNBT(compound, NBTType.SYNC);
// TODO -- studying ActuallyAdditions to learn how to do this. See actuallyadditions.mod.network 
			// classes.
//			NBTTagCompound data = new NBTTagCompound();
//			data.setTag("Data", compound);
//			data.setInteger("X", this.pos.getX());
//			data.setInteger("Y", this.pos.getY());
//			data.setInteger("Z", this.pos.getZ());
//			PacketHandler.theNetwork.sendToAllAround(
//				new PacketServerToClient(data, PacketHandler.TILE_ENTITY_HANDLER), 
//				new NetworkRegistry.TargetPoint(this.world.provider.getDimension(), 
//												this.getPos().getX(), this.getPos().getY(), 
//												this.getPos().getZ(), 64));
		}
	} // end sendUpdate()
	
} // end class
