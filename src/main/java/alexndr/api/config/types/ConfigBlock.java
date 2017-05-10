package alexndr.api.config.types;

import net.minecraftforge.common.config.Configuration;

/**
 * @author AleXndrTheGr8st
 */
public class ConfigBlock extends ConfigEntry
{
	//Primary Block Attributes
	private float hardness = 0.0F;
	private float resistance = 0.0F;
	private int lightValue = 0;
	private int harvestLevel = 0;
	private String harvestTool;
	
	//Additional Block Attributes
	private boolean unbreakable = false;
	private boolean beaconBase = false;
	
	/**
	 * Creates a new ConfigBlock. This is for blocks, eg. Copper Ore or Block of Tin.
	 * @param name Name of the ConfigBlock
	 * @param category The category to place the ConfigBlock in
	 */
	public ConfigBlock(String name, String category) 
	{
		super(name, category, true);
	}
	
	@Override
	public void GetConfig(Configuration config) 
	{
		super.GetConfig(config);
		hardness = config.getFloat("hardness", subcategory, hardness, 0.0F, 32000.0F, 
									"how many hits it takes to break a block");
		resistance = config.getFloat("resistance", subcategory, resistance, 0.0F, 32000.0F, 
								     "how much this block can resist explosions");
		lightValue = config.getInt("lightValue", subcategory, lightValue, 0, 255, 
									  "Amount of light emitted (15 is max torchlight)");
		harvestLevel = config.getInt("harvestLevel", subcategory, harvestLevel, 0, 8, 
									 "level required to harvest this block (0=wood, 3=diamond)");
		harvestTool = config.getString("harvestTool", subcategory, harvestTool, 
										"tool Class");
		unbreakable = config.getBoolean("unbreakable", subcategory, unbreakable, 
										"is literally unbreakable?");
		beaconBase = config.getBoolean("beaconBase", subcategory, beaconBase, 
										"block can serve as a beacon base");
	} // end GetConfig()


	/**
	 * Returns the hardness of the block.
	 * @return Hardness
	 */
	public float getHardness() {
		return this.hardness;
	}
	
	/**
	 * Sets the hardness of the block.
	 * @param hardness Hardness
	 * @return ConfigBlock
	 */
	public ConfigBlock setHardness(float hardness) {
		this.hardness = hardness;
		return this;
	}

	/**
	 * Returns the blast resistance of the block.
	 * @return Blast resistance
 	 */
	public float getResistance() {
		return this.resistance;
	}
	
	/**
	 * Sets the blast resistance of the block.
	 * @param resistance Blast resistance
	 * @return ConfigBlock
	 */
	public ConfigBlock setResistance(float resistance) {
		this.resistance = resistance;
		return this;
	}

	/**
	 * Returns the light value of the block.
	 * @return Light value
	 */
	public int getLightValue() {
		return this.lightValue;
	}
	
	/**
	 * Set sets the light value of the block.
	 * @param lightValue Light value
	 * @return ConfigBlock
	 */
	public ConfigBlock setLightValue(int lightValue) {
		this.lightValue = lightValue;
		return this;
	}

	/**
	 * Returns the harvest level of the block.
	 * @return Harvest level
	 */
	public int getHarvestLevel() {
		return this.harvestLevel;
	}
	
	/**
	 * Sets the harvest level of the block.
	 * @param harvestLevel Harvest level
	 * @return ConfigBlock
	 */
	public ConfigBlock setHarvestLevel(int harvestLevel) {
		this.harvestLevel = harvestLevel;
		return this;
	}

	/**
	 * Returns the harvest tool for the block.
	 * @return Harvest tool
	 */
	public String getHarvestTool() {
		return this.harvestTool;
	}

	/**
	 * Sets the harvest tool for the block.
	 * @param harvestTool Harvest tool
	 * @return ConfigBlock
	 */
	public ConfigBlock setHarvestTool(String harvestTool) {
		this.harvestTool = harvestTool;
		return this;
	}

	/**
	 * Returns whether or not the block is unbreakable.
	 * @return Unbreakable
	 */
	public boolean getUnbreakable() {
		return this.unbreakable;
	}

	/**
	 * Sets whether or not the block is unbreakable.
	 * @param unbreakable Unbreakable boolean
	 * @return ConfigBlock
	 */
	public ConfigBlock setUnbreakable(boolean unbreakable) {
		this.unbreakable = unbreakable;
		return this;
	}

	/**
	 * Returns whether or not the block is valid as a beacon base.
	 * @return Valid beacon base boolean
	 */
	public boolean getBeaconBase() {
		return this.beaconBase;
	}

	/**
	 * Sets whether or not the block is valid as a beacon base.
	 * @param beaconBase Valid beacon base boolean
	 * @return ConfigBlock
	 */
	public ConfigBlock setBeaconBase(boolean beaconBase) {
		this.beaconBase = beaconBase;
		return this;
	}

} // end class
