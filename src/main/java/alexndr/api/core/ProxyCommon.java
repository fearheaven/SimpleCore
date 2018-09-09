package alexndr.api.core;

import alexndr.api.registry.Plugin;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


/**
 * @author AleXndrTheGr8st
 */
@Mod.EventBusSubscriber
public class ProxyCommon 
{
	public void preInit(FMLPreInitializationEvent event) 
	{
	} // end preInit()
	
    public void load(FMLInitializationEvent event)
    {
    } // end load()

    public void postInit(FMLPostInitializationEvent event) 
    { 
    } // end postInit()    

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) 
	{
   	 	//Registers
		ModBlocks.register(event.getRegistry());
	} // end registerBlocks()

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) 
	{
    	ModBlocks.registerItemBlocks(event.getRegistry());
	}
    
    public void registerItemRenderer(Plugin plugin, Item item, int meta, String id) {
    }
    
	
} // end class
