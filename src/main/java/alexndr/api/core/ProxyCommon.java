package alexndr.api.core;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import alexndr.api.helpers.game.OreGenerator;
import alexndr.api.logger.LogHelper;
import alexndr.api.registry.ContentCategories;
import alexndr.api.registry.ContentRegistry;


/**
 * @author AleXndrTheGr8st
 */
public class ProxyCommon 
{
	public void preInit(FMLPreInitializationEvent event) 
	{
		//Configuration
		APISettings.createOrLoadSettings(event);
		LogHelper.loggerSetup();
		
		//Content
		addVanillaTabs();

	} // end preInit()
	
    public void load(FMLInitializationEvent event)
    {
		//World Generator
		GameRegistry.registerWorldGenerator(new OreGenerator(), 1);
    	
    } // end load()

    public void postInit(FMLPostInitializationEvent event) 
    { 
    } // end postInit()    

	/**
	 * Adds the vanilla Minecraft tabs to the ContentRegistry.
	 */
	private void addVanillaTabs() {
		LogHelper.verbose("Adding vanilla tabs to ContentRegistry");
		ContentRegistry.registerPlugin(SimpleCoreAPI.vanilla);
		ContentRegistry.registerTab(SimpleCoreAPI.vanilla, CreativeTabs.BUILDING_BLOCKS, "tabBlock", 
									ContentCategories.CreativeTab.GENERAL);
		ContentRegistry.registerTab(SimpleCoreAPI.vanilla, CreativeTabs.BREWING, "tabBrewing", ContentCategories.CreativeTab.GENERAL);
		ContentRegistry.registerTab(SimpleCoreAPI.vanilla, CreativeTabs.COMBAT, "tabCombat", ContentCategories.CreativeTab.GENERAL);
		ContentRegistry.registerTab(SimpleCoreAPI.vanilla, CreativeTabs.DECORATIONS, "tabDecorations", ContentCategories.CreativeTab.GENERAL);
		ContentRegistry.registerTab(SimpleCoreAPI.vanilla, CreativeTabs.FOOD, "tabFood", ContentCategories.CreativeTab.GENERAL);
		ContentRegistry.registerTab(SimpleCoreAPI.vanilla, CreativeTabs.MATERIALS, "tabMaterials", ContentCategories.CreativeTab.GENERAL);
		ContentRegistry.registerTab(SimpleCoreAPI.vanilla, CreativeTabs.MISC, "tabMisc", ContentCategories.CreativeTab.GENERAL);
		ContentRegistry.registerTab(SimpleCoreAPI.vanilla, CreativeTabs.REDSTONE, "tabRedstone", ContentCategories.CreativeTab.GENERAL);
		ContentRegistry.registerTab(SimpleCoreAPI.vanilla, CreativeTabs.TOOLS, "tabTools", ContentCategories.CreativeTab.GENERAL);
		ContentRegistry.registerTab(SimpleCoreAPI.vanilla, CreativeTabs.TRANSPORTATION, "tabTransport", ContentCategories.CreativeTab.GENERAL);
	} // end addVanillaTabs()
	
} // end class
