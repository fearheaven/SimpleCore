package alexndr.api.core;

import java.util.List;

import alexndr.api.content.inventory.SimpleTab;
import alexndr.api.helpers.events.CommonEventHelper;
import alexndr.api.helpers.game.OreGenerator;
import alexndr.api.helpers.game.TabHelper;
import alexndr.api.helpers.game.TestFurnaceGuiHandler;
import alexndr.api.logger.LogHelper;
import alexndr.api.registry.ContentCategories;
import alexndr.api.registry.ContentRegistry;
import alexndr.api.registry.Plugin;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * @author AleXndrTheGr8st
 */
@Mod(modid = APIInfo.ID, name = APIInfo.NAME, version = APIInfo.VERSION,
	dependencies=APIInfo.DEPENDENCIES, acceptedMinecraftVersions=APIInfo.ACCEPTED_VERSIONS,
	updateJSON=APIInfo.VERSIONURL)
public class SimpleCoreAPI 
{
	@SidedProxy(clientSide = "alexndr.api.core.ProxyClient", 
			    serverSide = "alexndr.api.core.ProxyCommon")
	public static ProxyCommon proxy;
	
	@Mod.Instance
	public static SimpleCoreAPI instance;
	
	public static Plugin plugin = new Plugin(APIInfo.ID, APIInfo.NAME);
	public static Plugin vanilla = new Plugin("minecraft", "Minecraft");
	
	//Creative Tabs
	private static boolean iconsSet = false;
	private static SimpleTab simpleBlocks, simpleDecorations, simpleMaterials, 
							 simpleTools, simpleCombat, simpleMachines;

	public SimpleCoreAPI() {
		FluidRegistry.enableUniversalBucket();
	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) 
	{
		LogHelper.info("SimpleCore API Loading...");
		//Register Event Stuff
		MinecraftForge.EVENT_BUS.register(new CommonEventHelper());
		
		//Configuration
		APISettings.createOrLoadSettings(event);
		LogHelper.loggerSetup();
		
		//Content
		if (false == APISettings.tabs)
		{
			addVanillaTabs();
		}
		
		try {
			ModBlocks.configureBlocks();
		} 
		catch (Exception e) {
			LogHelper.severe(APIInfo.NAME,
					"Content pre-init failed. This is a serious problem!");
			e.printStackTrace();
		}
	
		proxy.preInit(event);
		// tabPreInit();  // plugin should call this, not API. 
	} // end preInit()
	
	@Mod.EventHandler
	public void init(FMLInitializationEvent event) 
	{
		NetworkRegistry.INSTANCE.registerGuiHandler(this, (IGuiHandler) new TestFurnaceGuiHandler());
		//World Generator
		GameRegistry.registerWorldGenerator(new OreGenerator(), 1);
 		proxy.load(event);
	}
	
	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) 
	{
		LogHelper.info("SimpleCore API Loading Complete!");
	}
	
	/**
	 * create tab or separate tabs for SimpleCore-based plugins. Note that SimpleCoreAPI itself
	 * SHOULD NOT CALL THIS, as it will result in crashes if SimpleCoreAPI is loaded stand-alone,
	 * or with any plugin that does not call setTabIcons -- Sinhika
	 */
	public static void tabPreInit() 
	{
		LogHelper.verbose("Creating tabs");
		if(APISettings.tabs) 
		{
			simpleBlocks = new SimpleTab(SimpleCoreAPI.plugin, "SimpleBlocks", ContentCategories.CreativeTab.BLOCKS);
			if(APISettings.separateTabs) 
			{
				simpleDecorations = new SimpleTab(SimpleCoreAPI.plugin, "SimpleDecorations", ContentCategories.CreativeTab.DECORATIONS);
				simpleMaterials = new SimpleTab(SimpleCoreAPI.plugin, "SimpleMaterials", ContentCategories.CreativeTab.MATERIALS);
				simpleTools = new SimpleTab(SimpleCoreAPI.plugin, "SimpleTools", ContentCategories.CreativeTab.TOOLS);
				simpleCombat = new SimpleTab(SimpleCoreAPI.plugin, "SimpleCombat", ContentCategories.CreativeTab.COMBAT);
				simpleMachines = new SimpleTab(SimpleCoreAPI.plugin, "SimpleMachines", 
						   ContentCategories.CreativeTab.REDSTONE);
			}
		}
		TabHelper.setTabInitDone(true);
	} // end tabPreInit()
	
	/**
	 * Sets the Icons for the CreativeTabs added by this mod. Call this during Initialisation phase.
	 * Must be in correct order, with the correct number of elements (5). They are: 
	 * 1. SimpleBlocks.
	 * 2. SimpleDecorations.
	 * 3. SimpleMaterials.
	 * 4. SimpleTools.
	 * 5. SimpleCombat.
	 * 6. SimpleMachines
	 * @param iconItemsList List of Items with which to set the tab icons
	 */
	public static void setTabIcons(List<Item> iconItemsList) {
		if(!iconsSet) {
			iconsSet = true;
			if(APISettings.tabs) {
				simpleBlocks.setIcon(iconItemsList.get(0));
				if(APISettings.separateTabs) 
				{
					simpleDecorations.setIcon(iconItemsList.get(1));
					simpleMaterials.setIcon(iconItemsList.get(2));
					simpleTools.setIcon(iconItemsList.get(3));
					simpleCombat.setIcon(iconItemsList.get(4));
					simpleMachines.setIcon(iconItemsList.get(5));
				}
			}
		}
	} // end setTabIcons()
	
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
