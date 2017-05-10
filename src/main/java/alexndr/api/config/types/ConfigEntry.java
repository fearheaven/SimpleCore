package alexndr.api.config.types;

import net.minecraftforge.common.config.Configuration;

/**
 * @author AleXndrTheGr8st
 */
public class ConfigEntry 
{
	protected String name;
	protected String category;
	protected String subcategory;
	protected boolean has_subcategory;
	protected boolean enabled = true;
	
	/**
	 * Creates a new ConfigEntry. This is the generic Config entry.
	 * @param name Name of the ConfigEntry
	 * @param category The category to place the ConfigEntry in
	 */
	public ConfigEntry(String name, String category, boolean hasSubCat) 
	{
		this.name = name;
		this.category = category;
		this.has_subcategory = hasSubCat;
		
		// set sub-category.
		if (this.has_subcategory) {
			subcategory = category + Configuration.CATEGORY_SPLITTER + name;
		}
	} // end ConfigEntry()
	
	public void GetConfig(Configuration config)
	{
		enabled = config.getBoolean("enabled", subcategory, enabled, "Set false to disable creation");
	} // end GetConfig()
	
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * Get full subcategory string.
	 */
	public String getSubCategory() {
		return (has_subcategory ? subcategory : category);
	}
	
	/**
	 * Do we have a subcategory name?
	 * @return true/false
	 */
	public boolean hasSubCategory() {
		return has_subcategory;
	}
	
	/**
	 * Returns the name of the ConfigEntry.
	 * @return ConfigEntry name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the category of the ConfigEntry.
	 * @return ConfigEntry category
	 */
	public String getCategory() {
		return category;
	}
} // end class
