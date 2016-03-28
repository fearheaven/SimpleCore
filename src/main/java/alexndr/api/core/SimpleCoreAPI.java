package alexndr.api.core;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import alexndr.api.content.inventory.SimpleTab;
import alexndr.api.helpers.events.CommonEventHelper;
import alexndr.api.helpers.game.OreGenerator;
import alexndr.api.logger.LogHelper;
import alexndr.api.registry.ContentCategories;
import alexndr.api.registry.ContentRegistry;
import alexndr.api.registry.Plugin;

import com.google.common.collect.Lists;

/**
 * @author AleXndrTheGr8st
 */
@Mod(modid = APIInfo.ID, name = APIInfo.NAME, version = APIInfo.VERSION)
public class SimpleCoreAPI 
{
	@SidedProxy(clientSide = "alexndr.api.core.ProxyClient", serverSide = "alexndr.api.core.ProxyCommon")
	public static ProxyCommon proxy;
	public static Plugin plugin = new Plugin(APIInfo.ID, APIInfo.NAME);
	public static Plugin vanilla = new Plugin("minecraft", "Minecraft");
	
	//Bow Renders
	public static List<List<Object>> bowRenderList = Lists.newArrayList();
	
	//Creative Tabs
	private static boolean iconsSet = false;
	private static SimpleTab simpleBlocks, simpleDecorations, simpleMaterials, simpleTools, simpleCombat;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		LogHelper.info("SimpleCore API Loading...");
		
		//Register Event Stuff
		MinecraftForge.EVENT_BUS.register(new CommonEventHelper());
		proxy.registerEventHandlers();
		
		//Configuration
		// APIInfo.setModInfoProperties(event); // USE mcmod.info file instead -- Sinhika
		APISettings.createOrLoadSettings(event);
		LogHelper.loggerSetup();
//		if(APISettings.updateChecker.asBoolean()) {
//			UpdateChecker checker = new UpdateChecker(APIInfo.ID, APIInfo.VERSION, APIInfo.VERSIONURL);
//		}
		
		//Content
		addVanillaTabs();
		// tabPreInit();  // plugin should call this, not API. 
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		//Custom World Gen
		
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) 
	{
		//Inventory Render Stuff
		if(event.getSide() == Side.CLIENT) 
		{
			proxy.renderItemStuff(event);
		}
		//World Generator
		GameRegistry.registerWorldGenerator(new OreGenerator(), 1);
		// LogHelper.verbose("Total number of mods UpdateChecker is checking for = " + UpdateChecker.getNumberOfMods());
		LogHelper.info("SimpleCore API Loading Complete!");
	}
	
	/**
	 * Adds the vanilla Minecraft tabs to the ContentRegistry.
	 */
	private void addVanillaTabs() {
		LogHelper.verbose("Adding vanilla tabs to ContentRegistry");
		ContentRegistry.registerPlugin(vanilla);
		ContentRegistry.registerTab(vanilla, CreativeTabs.tabBlock, "tabBlock", ContentCategories.CreativeTab.GENERAL);
		ContentRegistry.registerTab(vanilla, CreativeTabs.tabBrewing, "tabBrewing", ContentCategories.CreativeTab.GENERAL);
		ContentRegistry.registerTab(vanilla, CreativeTabs.tabCombat, "tabCombat", ContentCategories.CreativeTab.GENERAL);
		ContentRegistry.registerTab(vanilla, CreativeTabs.tabDecorations, "tabDecorations", ContentCategories.CreativeTab.GENERAL);
		ContentRegistry.registerTab(vanilla, CreativeTabs.tabFood, "tabFood", ContentCategories.CreativeTab.GENERAL);
		ContentRegistry.registerTab(vanilla, CreativeTabs.tabMaterials, "tabMaterials", ContentCategories.CreativeTab.GENERAL);
		ContentRegistry.registerTab(vanilla, CreativeTabs.tabMisc, "tabMisc", ContentCategories.CreativeTab.GENERAL);
		ContentRegistry.registerTab(vanilla, CreativeTabs.tabRedstone, "tabRedstone", ContentCategories.CreativeTab.GENERAL);
		ContentRegistry.registerTab(vanilla, CreativeTabs.tabTools, "tabTools", ContentCategories.CreativeTab.GENERAL);
		ContentRegistry.registerTab(vanilla, CreativeTabs.tabTransport, "tabTransport", ContentCategories.CreativeTab.GENERAL);
	}
	
	/**
	 * create tab or separate tabs for SimpleCore-based plugins. Note that SimpleCoreAPI itself
	 * SHOULD NOT CALL THIS, as it will result in crashes if SimpleCoreAPI is loaded stand-alone,
	 * or with any plugin that does not call setTabIcons -- Sinhika
	 */
	public static void tabPreInit() 
	{
		LogHelper.verbose("Creating tabs");
		if(APISettings.tabs.asBoolean()) 
		{
			simpleBlocks = new SimpleTab(SimpleCoreAPI.plugin, "SimpleBlocks", ContentCategories.CreativeTab.BLOCKS);
			if(APISettings.separateTabs.asBoolean()) 
			{
				simpleDecorations = new SimpleTab(SimpleCoreAPI.plugin, "SimpleDecorations", ContentCategories.CreativeTab.DECORATIONS);
				simpleMaterials = new SimpleTab(SimpleCoreAPI.plugin, "SimpleMaterials", ContentCategories.CreativeTab.MATERIALS);
				simpleTools = new SimpleTab(SimpleCoreAPI.plugin, "SimpleTools", ContentCategories.CreativeTab.TOOLS);
				simpleCombat = new SimpleTab(SimpleCoreAPI.plugin, "SimpleCombat", ContentCategories.CreativeTab.COMBAT);
			}
		}
	} // end tabPreInit()
	
	/**
	 * Sets the Icons for the CreativeTabs added by this mod. Call this during Initialisation phase.
	 * Must be in correct order, with the correct number of elements (5). They are: 
	 * 1. SimpleBlocks.
	 * 2. SimpleDecorations.
	 * 3. SimpleMaterials.
	 * 4. SimpleTools.
	 * 5. SimpleCombat.
	 * @param iconItemsList List of Items with which to set the tab icons
	 */
	public static void setTabIcons(List<Item> iconItemsList) {
		if(!iconsSet) {
			iconsSet = true;
			if(APISettings.tabs.asBoolean()) {
				simpleBlocks.setIcon(iconItemsList.get(0));
				if(APISettings.separateTabs.asBoolean()) {
					simpleDecorations.setIcon(iconItemsList.get(1));
					simpleMaterials.setIcon(iconItemsList.get(2));
					simpleTools.setIcon(iconItemsList.get(3));
					simpleCombat.setIcon(iconItemsList.get(4));
				}
			}
		}
	}
	
	
	/**
	 * Adds a bow to the list to be rendered.
	 * @param plugin The plugin the bow belongs to
	 * @param bow The bow
	 */
	public static void addBowRenderDetails(Plugin plugin, Item bow) {
		List<Object> list = Lists.newArrayList();
		list.add(plugin);
		list.add(bow);
		bowRenderList.add(list);
	}
} // end class
