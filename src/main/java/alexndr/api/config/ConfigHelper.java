package alexndr.api.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * Holds some configuration helper methods.
 * @author Sinhika
 *
 */
public class ConfigHelper 
{
	public static final String CATEGORY_ARMOR = "Armors";
	public static final String CATEGORY_BLOCK = "Blocks";
	public static final String CATEGORY_BOW = "Bows";
	public static final String CATEGORY_HELP = "Help";
	public static final String CATEGORY_ITEM = "Items";
	public static final String CATEGORY_ORE = "Ores";
	public static final String CATEGORY_TOOL = "Tools";
	
    /**
     * Get configuration instance.
     * @param cfgdir author-specific configuration sub-directory.
     * @param configname mod-specific configuration file name.
     * @return the configuration instance.
     */
    public static Configuration GetConfig(FMLPreInitializationEvent event, String cfgdir, String configname )
    {
        File installDir = event.getModConfigurationDirectory();
        File configDir = new File(installDir, cfgdir);
        File configFile = new File(configDir, configname);
        return new Configuration(configFile, true);  // yes, custom category names are case-sensitive.
    } // end GetConfig()
    
    /**
     * Add documentation link/entry to config.
     * @param[in] config the configuration object to add this entry to.
     * @param[in] docLink link to mod site/CurseForge page/etc.
     */
    public static void createHelpEntry(Configuration config, String docLink)
    {
    	config.setCategoryComment(CATEGORY_HELP, "Link to module download site");
    	config.get(CATEGORY_HELP, "ModSite", docLink);
    	// TODO - additional stuff as I think of it.
    } // end createHelpEntry()
    
} // end class
