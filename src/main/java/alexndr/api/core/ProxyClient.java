package alexndr.api.core;

import alexndr.api.helpers.events.ClientEventHelper;
import alexndr.api.registry.Plugin;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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
	
	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event) 
	{
    	ModBlocks.registerModels();
	}

	@Override
    public void registerItemRenderer(Plugin plugin, Item item, int meta, String id) {
    	ModelLoader.setCustomModelResourceLocation(item, meta, 
    						new ModelResourceLocation(
    								new ResourceLocation(plugin.getModId(), id), "inventory"));
    } // end registerItemRenderer()

} // end class
