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
