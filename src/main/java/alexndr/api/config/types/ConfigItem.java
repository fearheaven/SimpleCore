package alexndr.api.config.types;

import net.minecraftforge.common.config.Configuration;

/**
 * @author AleXndrTheGr8st
 */
public class ConfigItem extends ConfigEntry 
{
	
	//Additional item attributes.
	private boolean render3D = true;
	private float smeltingXP = 0.0F;
	
	/**
	 * Creates a new ConfigItem. This is for items, eg. Adamantium Ingot.
	 * @param name Name of the ConfigItem
	 * @param category The category to place the ConfigItem in
	 */
	public ConfigItem(String name, String category) 
	{
		super(name, category, true);
	}
	
	@Override
	public void GetConfig(Configuration config) 
	{
		super.GetConfig(config);
		render3D = config.getBoolean("render3D", subcategory, render3D, "render item in 3D");
		smeltingXP = config.getFloat("smeltingXP", subcategory, smeltingXP, 0.0F, 1000.0F, 
									 "amount of XP given when this item is taken from a furnace");
	} // end GetConfig()
	
	/**
	 * Returns whether or not the item(s) should be rendered in 3D.
	 * @return Render 3D boolean
	 */
	public boolean getRender3D() {
		return render3D;
	}

	/**
	 * Sets whether or not the item(s) should be rendered in 3D.
	 * @param render3d render 3D boolean
	 * @return ConfigItem
	 */
	public ConfigItem setRender3D(boolean render3d) {
		this.render3D = render3d;
		return this;
	}

	/**
	 * Returns the amount of XP given when this item is taken from a furnace.
	 * Should be between 0 and 1.
	 * @return Smelting XP
	 */
	public float getSmeltingXP() {
		return this.smeltingXP;
	}

	/**
	 * Sets the amount of XP given when this item is taken from a furnace.
	 * Should be between 0 and 1.
	 * @param smeltingXP Smelting XP
	 * @return ConfigItem
	 */
	public ConfigItem setSmeltingXP(float smeltingXP) {
		this.smeltingXP = smeltingXP;
		return this;
	}
} // end class
