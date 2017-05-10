package alexndr.api.config.types;

import alexndr.api.config.ConfigHelper;
import net.minecraftforge.common.config.Configuration;

/**
 * @author AleXndrTheGr8st
 */
public class ConfigTool extends ConfigEntry
{
	// private List<ConfigValue> valuesList = Lists.newArrayList();
	
	//Primary tool attributes.
	private int uses = 0;
	private int harvestLevel = 0;
	private float harvestSpeed = 0.0F;
	private float damageVsEntity = 0.0F;
	private int enchantability = 0;
	
	//Additional tool attributes.
	private boolean render3D = true;
	private String repairMaterial;

	/**
	 * Creates a new ConfigTool. This is for tool material sets, eg. Copper Tools.
	 * @param name Name of the ConfigTool
	 */
	public ConfigTool(String name) 
	{
		super(name, ConfigHelper.CATEGORY_TOOL, true);
	}

	@Override
	public void GetConfig(Configuration config) 
	{
		uses = config.getInt("Uses", subcategory, uses, 1, 32000, 
							  "number of uses this material allows. (wood = 59, stone = 131, iron = 250, diamond = 1561, gold = 32)");
		harvestLevel = config.getInt("harvestLevel", subcategory, harvestLevel, 0, 10, 
				   "tier of material tool can harvest (3 = DIAMOND, 2 = IRON, 1 = STONE, 0 = WOOD/GOLD)");
		harvestSpeed = config.getFloat("harvestSpeed", subcategory, harvestSpeed, 0.0F, 32000.0F, 
									   "how fast this tool harvests blocks (higher is faster");
		damageVsEntity = config.getFloat("damageVsEntity", subcategory, damageVsEntity, 0.0F, 255.0F, 
										 "Damage versus entities");
		enchantability = config.getInt("Enchantability", subcategory, enchantability, 1, 255,
			    "higher = more enchantable");
		repairMaterial = config.getString("RepairMaterial", subcategory, repairMaterial, 
				  "Either ore-dictionary name or actual item name");
		render3D = config.getBoolean("render3D", subcategory, render3D, "render tool in 3D");
	}
	
	/**
	 * Returns the number of uses of the ConfigTool
	 * @return Number of uses
	 */
	public int getUses() {
		return uses;
	}
	
	/**
	 * Sets the number of uses of the ConfigTool.
	 * @param uses Number of uses
	 * @return ConfigTool
	 */
	public ConfigTool setUses(int uses) {
		this.uses = uses;
		return this;
	}
	
	/**
	 * Returns the harvest level of the ConfigTool.
	 * @return Harvest level
	 */
	public int getHarvestLevel() {
		return harvestLevel;
	}
	
	/**
	 * Sets the harvest level of the ConfigTool.
	 * @param harvestLevel Harvest level
	 * @return ConfigTool
	 */
	public ConfigTool setHarvestLevel(int harvestLevel) {
		this.harvestLevel = harvestLevel;
		return this;
	}

	/**
	 * Returns the harvest speed of the ConfigTool.
	 * @return Harvest speed
	 */
	public float getHarvestSpeed() {
		return harvestSpeed;
	}
	
	/**
	 * Sets the harvest speed of the ConfigTool.
	 * @param harvestSpeed Harvest speed
	 * @return ConfigTool
	 */
	public ConfigTool setHarvestSpeed(float harvestSpeed) {
		this.harvestSpeed = harvestSpeed;
		return this;
	}

	/**
	 * Returns the damage vs entity of the ConfigTool.
	 * @return Damage vs entity
	 */
	public float getDamageVsEntity() {
		return damageVsEntity;
	}
	
	/**
	 * Sets the damage vs entity of the ConfigTool.
	 * @param damageVsEntity Damage vs entity
	 * @return ConfigTool
	 */
	public ConfigTool setDamageVsEntity(float damageVsEntity) {
		this.damageVsEntity = damageVsEntity;
		return this;
	}

	/**
	 * Returns the enchantability of the ConfigTool.
	 * @return Enchantability
	 */
	public int getEnchantability() {
		return enchantability;
	}
	
	/**
	 * Sets the enchantability of the ConfigTool.
	 * @param enchantability Enchantability
	 * @return ConfigTool
	 */
	public ConfigTool setEnchantability(int enchantability) {
		this.enchantability = enchantability;
		return this;
	}
	
	/**
	 * Returns whether or not the tool(s) should be rendered in 3D.
	 * @return render3D boolean
	 */
	public boolean getRender3D() {
		return render3D;
	}

	/**
	 * Sets whether or not the tool(s) should be rendered in 3D.
	 * @param render3d Render in 3D boolean
	 * @return ConfigTool
	 */
	public ConfigTool setRender3D(boolean render3d) {
		this.render3D = render3d;
		return this;
	}

	/**
	 * Returns the repair material of the tool(s). 
	 * Could be full item name, or OreDictionary name.
	 * @return Item repair material
	 */
	public String getRepairMaterial() {
		return repairMaterial;
	}

	/**
	 * Sets the repair material of the tool(s).
	 * Can be full item name, or OreDictionary name.
	 * @param repairMaterial Item repair material
	 * @return ConfigTool
	 */
	public ConfigTool setRepairMaterial(String repairMaterial) {
		this.repairMaterial = repairMaterial;
		return this;
	}


} // end class
