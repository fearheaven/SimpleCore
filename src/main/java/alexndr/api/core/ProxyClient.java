package alexndr.api.core;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import alexndr.api.helpers.events.ClientEventHelper;

public class ProxyClient extends ProxyCommon 
{
	@Override
	public void preInit(FMLPreInitializationEvent event) 
	{
		super.preInit(event);
		registerClientEventHandlers();
	}
	
	@Override
    public void load(FMLInitializationEvent event)
    {
    	super.load(event);
    } // end load()

	@Override
    public void postInit(FMLPostInitializationEvent event) 
    { 
		super.postInit(event);
    } // end postInit()
    
	public void registerClientEventHandlers() {
		MinecraftForge.EVENT_BUS.register(new ClientEventHelper());
	} // end registerEventHandlers()
	
	
} // end class
