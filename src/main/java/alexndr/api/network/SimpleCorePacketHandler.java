/*
 * This file ("SimpleCorePacketHandler.java") is part of the Minecraft mod project SimpleCore. 
 * It is created and owned by Sinhika and Skrallexxx and distributed under the Lesser GNU Public 
 * License v3.0, a copy of which is included in the root of this source tree ("lgpl.txt") or 
 * otherwise readable at <http://fsf.org/>.
 * 
 * (c) 2014-2017 Sinhika
 */
package alexndr.api.network;

import alexndr.api.core.APIInfo;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * main SimpleImpl class for Forge network packet handling.
 *
 */
public class SimpleCorePacketHandler 
{
	public static SimpleNetworkWrapper INSTANCE; 
	
	public static void init()
	{
		INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(APIInfo.ID);
		INSTANCE.registerMessage(MessageTileEntityServer.TileEntityHandler.class, 
								 MessageTileEntityServer.class, 0, Side.CLIENT);
		// TODO MessageTileEntityClient class and handler.
	} // end init()
	
} // end class
