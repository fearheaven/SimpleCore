package alexndr.api.config.types;

import alexndr.api.config.ConfigHelper;
import net.minecraftforge.common.config.Configuration;

/**
 * @author AleXndrTheGr8st
 */
public class ConfigArmor extends ConfigEntry
{
	//Primary armor attributes.
	protected int durability;
	protected int enchantability;
	protected int helmReduction;
	protected int chestReduction;
	protected int legsReduction;
	protected int bootsReduction;
	
	//Additional armor attributes.
	protected String repairMaterial;
	
	/**
	 * Creates a ConfigArmor. This is for armor material sets, eg. Mythril Armor.
	 * @param name Name of the ConfigArmor
	 */
	public ConfigArmor(String name) 
	{
		super(name, ConfigHelper.CATEGORY_ARMOR, true);
	}
	
	/**
	 * get current settings from config file.
	 */
	public void GetConfig(Configuration config) 
	{
		super.GetConfig(config);
		repairMaterial = config.getString("RepairMaterial", subcategory, repairMaterial, 
										  "Either ore-dictionary name or actual item name");
		durability = config.getInt("Durability", subcategory, durability, 1, 32000, 
								   "How many hits it can take");
		enchantability = config.getInt("Enchantability", subcategory, enchantability, 1, 255,
									    "higher = more enchantable");
		helmReduction =  config.getInt("HelmReduction", subcategory, helmReduction, 1, 255,
			    "Helm armor value");
		chestReduction =  config.getInt("ChestReduction", subcategory, chestReduction, 1, 255,
			    "Chest armor value");
		legsReduction =  config.getInt("LegsReduction", subcategory, legsReduction, 1, 255,
			    "Leggings armor value");
		bootsReduction =  config.getInt("BootsReduction", subcategory, bootsReduction, 1, 255,
			    "Boots armor value");	
	} // end GetConfig()
	
	/**
	 * Returns the durability of the armor.
	 * @return Armor durability
	 */
	public int getDurability() 
	{
		return durability;
	}

	/**
	 * Sets the durability of the armor.
	 * @param durability Armor durability
	 * @return ConfigArmor
	 */
	public ConfigArmor setDurability(int durability) 
	{
		this.durability = durability;
		return this;
	}

	/**
	 * Returns the enchantability of the armor.
	 * @return Armor enchantability
	 */
	public int getEnchantability() 
	{
		return enchantability;
	}

	/**
	 * Sets the enchantability of the armor.
	 * @param enchantability Armor enchantability
	 * @return ConfigArmor
	 */
	public ConfigArmor setEnchantability(int enchantability) 
	{
		this.enchantability = enchantability;
		return this;
	}

	/**
	 * Returns the helmet reduction of the armor.
	 * @return Helmet reduction
	 */
	public int getHelmReduction() 
	{
		return helmReduction;
	}

	/**
	 * Sets the helmet reduction of the armor.
	 * @param helmReduction Helmet reduction
	 * @return ConfigArmor
	 */
	public ConfigArmor setHelmReduction(int helmReduction) {
		this.helmReduction = helmReduction;
		return this;
	}

	/**
	 * Returns the chest-plate reduction of the armor.
	 * @return Chest-plate reduction
	 */
	public int getChestReduction() {
		return chestReduction;
	}

	/**
	 * Sets the chest-plate reduction of the armor.
	 * @param chestReduction Chest-plate reduction
	 * @return ConfigArmor
	 */
	public ConfigArmor setChestReduction(int chestReduction) {
		this.chestReduction = chestReduction;
		return this;
	}

	/**
	 * Returns the leggings reduction of the armor.
	 * @return Leggings reduction
	 */
	public int getLegsReduction() 
	{
		return legsReduction;
	}

	/**
	 * Sets the leggings reduction of the armor.
	 * @param legsReduction Leggings reduction
	 * @return ConfigArmor
	 */
	public ConfigArmor setLegsReduction(int legsReduction) {
		this.legsReduction = legsReduction;
		return this;
	}

	/**
	 * Returns the boots reduction of the armor.
	 * @return Boots reduction
	 */
	public int getBootsReduction() {
		return bootsReduction;
	}

	/**
	 * Sets the boots reduction of the armor.
	 * @param bootsReduction Boots reduction
	 * @return ConfigArmor
	 */
	public ConfigArmor setBootsReduction(int bootsReduction) {
		this.bootsReduction = bootsReduction;
		return this;
	}
	
	/**
	 * Returns the repair material of the armor.
	 * Can be full item name, or OreDictionary name.
	 * @return Armor repair material
	 */
	public String getRepairMaterial() {
		return repairMaterial;
	}
	
	/**
	 * Sets the repair material of the armor.
	 * Can be full item name, or OreDictionary name.
	 * @param repairMaterial Armor repair material
	 * @return ConfigArmor
	 */
	public ConfigArmor setRepairMaterial(String repairMaterial) {
		this.repairMaterial = repairMaterial;
		return this;
	}
} // end class
