package alexndr.api.core;

import java.io.File;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import alexndr.api.config.Configuration;
import alexndr.api.config.types.ConfigEntry;
import alexndr.api.config.types.ConfigValue;
import alexndr.api.logger.LogHelper;

/**
 * @author AleXndrTheGr8st
 */
public class APISettings {
	public static Configuration settings = new Configuration();
	
	public static void createOrLoadSettings(FMLPreInitializationEvent event) {
		settings.setModName(APIInfo.NAME);
		File configDir = new File(event.getModConfigurationDirectory(), "AleXndr");
		File settingsFile = new File(configDir, "SimpleCoreAPI_Settings.xml");
		settings.setFile(settingsFile);
		
		LogHelper.verbose("Loading API Settings...");
		try {
			settings.load();
			settings.createHelpEntry(APIInfo.URL);
				
			//Toggles
			ConfigEntry toggles = new ConfigEntry("SimpleCore Toggles", "Toggles");
			toggles.createNewValue("VerboseLogging").setActive().setDataType("@B").setCurrentValue("false")
					.setDefaultValue("false");
			toggles.createNewValue("GlobalUpdateChecking").setActive().setDataType("@B").setCurrentValue("true")
					.setDefaultValue("true");
			toggles.createNewValue("UpdateChecker").setActive().setDataType("@B").setCurrentValue("true")
					.setDefaultValue("true");
			toggles.createNewValue("Tabs").setActive().setDataType("@B").setCurrentValue("true")
					.setDefaultValue("true");
			toggles.createNewValue("SeparateTabs").setActive().setDataType("@B").setCurrentValue("true")
					.setDefaultValue("true");
			toggles = settings.get(toggles);
			verboseLogging = toggles.getValueByName("VerboseLogging");
			updateChecking = toggles.getValueByName("GlobalUpdateChecking");
			updateChecker = toggles.getValueByName("UpdateChecker");
			tabs = toggles.getValueByName("Tabs");
			separateTabs = toggles.getValueByName("SeparateTabs");
		}
		catch(Exception e) {
			LogHelper.info("Failed to load API settings");
			e.printStackTrace();
		}
		finally {
			settings.save();
		}
	}
	
	public static ConfigValue verboseLogging, updateChecking, updateChecker;
	public static ConfigValue tabs, separateTabs, customGeneration;
}
