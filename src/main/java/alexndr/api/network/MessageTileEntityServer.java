/*
 * This file ("MessageTileEntityServer.java") is part of the Minecraft mod project SimpleCore. 
 * It is created and owned by Sinhika and Skrallexxx and distributed under the Lesser GNU Public 
 * License v3.0, a copy of which is included in the root of this source tree ("lgpl.txt") or 
 * otherwise readable at <http://fsf.org/>.
 * 
 * Parts of this file are also based on Actually Additions source code by Ellpeck. See
 * <https://minecraft.curseforge.com/projects/actually-additions> for details of that mod.
 * 
 * (c) 2014-2017 Sinhika
 */
package alexndr.api.network;

import alexndr.api.content.tiles.TileEntityBase;
import alexndr.api.logger.LogHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * IMessage class for sending SimpleCore tile entity data to the client.
 */
public class MessageTileEntityServer implements IMessage 
{
    protected NBTTagCompound data;
    
	public MessageTileEntityServer() {}

	public MessageTileEntityServer(NBTTagCompound data) {
		this.data = data;
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		PacketBuffer buffer = new PacketBuffer(buf);
		try {
			this.data = buffer.readCompoundTag();
		}
		catch(Exception e) {
			LogHelper.severe("Failed to receive client message: " + e.toString());
		}
	} // end fromBytes()

	@Override
	public void toBytes(ByteBuf buf) 
	{
		PacketBuffer buffer = new PacketBuffer(buf);
		buffer.writeCompoundTag(data);
	} // end toBytes()

	
	/**
	 * IMessageHandler class for MessageTileEntityServer.
	 *
	 */
	public static class TileEntityHandler implements IMessageHandler<MessageTileEntityServer, IMessage>
	{
		@Override
		@SideOnly(Side.CLIENT)
		public IMessage onMessage(MessageTileEntityServer message, MessageContext ctx) 
		{
			Minecraft.getMinecraft().addScheduledTask(new Runnable() {
				@Override
				public void run() {
					if (message.data != null) {
						World world = Minecraft.getMinecraft().world;
						if (world != null) {
							TileEntity tile = world.getTileEntity(
									new BlockPos(message.data.getInteger("X"), 
											message.data.getInteger("Y"), 
											message.data.getInteger("Z")));
							if (tile instanceof TileEntityBase) {
								((TileEntityBase)tile).readSyncableNBT(
										message.data.getCompoundTag("Data"), 
										TileEntityBase.NBTType.SYNC);
							}
						} // end-if
					} // end-if
				} // end run()
			});
			return null;
		} // end onMessage()
		
	} // end inner class TileEntityHandler
} // end Class
