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
		if (false == APISettings.tabs.asBoolean())
		{
			addVanillaTabs();
		}
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
	private void addVanillaTabs() 
	{
		LogHelper.verbose("Adding vanilla tabs to ContentRegistry");
		ContentRegistry.registerPlugin(SimpleCoreAPI.vanilla);
		ContentRegistry.registerTab(SimpleCoreAPI.vanilla, CreativeTabs.BUILDING_BLOCKS,
				CreativeTabs.BUILDING_BLOCKS.getTabLabel(), ContentCategories.CreativeTab.GENERAL);
		ContentRegistry.registerTab(SimpleCoreAPI.vanilla, CreativeTabs.MISC, CreativeTabs.MISC.getTabLabel(),
				ContentCategories.CreativeTab.OTHER);
		ContentRegistry.registerTab(SimpleCoreAPI.vanilla, CreativeTabs.BREWING, CreativeTabs.BREWING.getTabLabel(),
				ContentCategories.CreativeTab.OTHER);
		ContentRegistry.registerTab(SimpleCoreAPI.vanilla, CreativeTabs.COMBAT, CreativeTabs.COMBAT.getTabLabel(),
				ContentCategories.CreativeTab.COMBAT);
		ContentRegistry.registerTab(SimpleCoreAPI.vanilla, CreativeTabs.DECORATIONS, CreativeTabs.DECORATIONS.getTabLabel(),
				ContentCategories.CreativeTab.DECORATIONS);
		ContentRegistry.registerTab(SimpleCoreAPI.vanilla, CreativeTabs.FOOD, CreativeTabs.FOOD.getTabLabel(),
				ContentCategories.CreativeTab.OTHER);
		ContentRegistry.registerTab(SimpleCoreAPI.vanilla, CreativeTabs.MATERIALS, CreativeTabs.MATERIALS.getTabLabel(),
				ContentCategories.CreativeTab.MATERIALS);
		ContentRegistry.registerTab(SimpleCoreAPI.vanilla, CreativeTabs.REDSTONE, CreativeTabs.REDSTONE.getTabLabel(),
				ContentCategories.CreativeTab.REDSTONE);
		ContentRegistry.registerTab(SimpleCoreAPI.vanilla, CreativeTabs.TOOLS, CreativeTabs.TOOLS.getTabLabel(),
				ContentCategories.CreativeTab.TOOLS);
		ContentRegistry.registerTab(SimpleCoreAPI.vanilla, CreativeTabs.TRANSPORTATION, CreativeTabs.TRANSPORTATION.getTabLabel(),
				ContentCategories.CreativeTab.OTHER);
	} // end addVanillaTabs()
	
} // end class
