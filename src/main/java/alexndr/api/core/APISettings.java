package alexndr.api.core;

import alexndr.api.config.ConfigHelper;
import alexndr.api.config.types.ConfigBlock;
import alexndr.api.logger.LogHelper;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * @author AleXndrTheGr8st
 */
public class APISettings 
{
	public static Configuration settings;
	
	public static void createOrLoadSettings(FMLPreInitializationEvent event) 
	{
		LogHelper.verbose(APIInfo.ID, "Loading API Settings...");
		settings = ConfigHelper.GetConfig(event, "AleXndr", APIInfo.ID + ".cfg");
		try {
			settings.load();
			ConfigHelper.createHelpEntry(settings, APIInfo.URL);
			
			verboseLogging = settings.getBoolean("VerboseLogging", Configuration.CATEGORY_GENERAL, 
												  true, "Turn on verbose logging"); 
			tabs = settings.getBoolean("Tabs", Configuration.CATEGORY_GENERAL, true, 
						"If true, have a SimpleOres creative tab; if false, put stuff in vanilla tabs");
			separateTabs = settings.getBoolean("SeparateTabs", Configuration.CATEGORY_GENERAL, 
												true, "Use separate creative tabs for each category");
			createTestFurnace = settings.getBoolean("createTestFurnace", Configuration.CATEGORY_GENERAL,
													 false, "create test furnace for testing furnace code");
			if (createTestFurnace)
			{
				testFurnace = new ConfigBlock("Test Furnace", ConfigHelper.CATEGORY_MACHINE)
						.setHardness(3.5F).setResistance(12.0F).setLightValue(1.0F)
						.setHarvestTool("pickaxe");
				testFurnace.GetConfig(settings);
			}
		} // end-try
		catch(Exception e) {
			LogHelper.warning(APIInfo.ID, "Failed to load API settings");
			e.printStackTrace();
		}
        finally {
            if(settings.hasChanged())
            	settings.save();
            LogHelper.verbose(APIInfo.ID, "Settings loaded successfully");
        }
	} // end createOrLoadSettings()
	
	public static boolean verboseLogging, createTestFurnace;
	public static boolean tabs, separateTabs, customGeneration;
	public static ConfigBlock testFurnace;
	
} // end class
