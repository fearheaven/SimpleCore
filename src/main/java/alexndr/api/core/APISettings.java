package alexndr.api.core;

import alexndr.api.config.ConfigHelper;
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
		settings = ConfigHelper.GetConfig(event, "AleXndr", APIInfo.ID + ".cfg");
		
		LogHelper.verbose("Loading API Settings...");
		try {
			settings.load();
			ConfigHelper.createHelpEntry(settings, APIInfo.URL);
			
			verboseLogging = settings.getBoolean("VerboseLogging", Configuration.CATEGORY_GENERAL, 
												  true, "Turn on verbose logging"); 
			tabs = settings.getBoolean("Tabs", Configuration.CATEGORY_GENERAL, true, 
						"If true, have a SimpleOres creative tab; if false, put stuff in vanilla tabs");
			separateTabs = settings.getBoolean("SeparateTabs", Configuration.CATEGORY_GENERAL, 
												true, "Use separate creative tabs for each category"); 
		}
		catch(Exception e) {
			LogHelper.info("Failed to load API settings");
			e.printStackTrace();
		}
        finally {
            if(settings.hasChanged())
            	settings.save();
            LogHelper.verbose(APIInfo.ID, "Settings loaded successfully");
        }
	} // end createOrLoadSettings()
	
	public static boolean verboseLogging;
	public static boolean tabs, separateTabs, customGeneration;
} // end class
