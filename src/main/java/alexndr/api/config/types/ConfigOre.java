package alexndr.api.config.types;

import alexndr.api.config.ConfigHelper;
import net.minecraftforge.common.config.Configuration;

public class ConfigOre extends ConfigBlock 
{
	//World Gen Attributes
	private int spawnRate = 0;
	private int veinSize = 0;
	private int minHeight = 1;
	private int maxHeight = 255;
	private boolean enableOreGen = true;
	
	public ConfigOre(String name) 
	{
		super(name, ConfigHelper.CATEGORY_ORE);
	}

	
	@Override
	public void GetConfig(Configuration config) 
	{
		super.GetConfig(config);
		enableOreGen = config.getBoolean("enableOreGen", subcategory, enableOreGen, 
										 "Set false to disable generation of this ore");
		spawnRate = config.getInt("spawnRate", subcategory, spawnRate, 0, 65280, 
								  "number of generated blocks per chunk");
		veinSize = config.getInt("veinSize", subcategory, veinSize, 0, 65280, 
						"maximum number of blocks that can generate adjacent to each other in a vein");
		minHeight = config.getInt("minHeight", subcategory, minHeight, 1, 254, 
								  "minimum height in the world that the block can spawn at");
		maxHeight = config.getInt("maxHeight", subcategory, maxHeight, 1, 255, 
								  "maximum height in the world that the block can spawn at");
	} // end GetConfig()


	/**
	 * Returns the spawn rate of the block.
	 * @return Spawn rate
	 */
	public int getSpawnRate() {
		return this.spawnRate;
	}
	
	/**
	 * Sets the spawn rate of the block.
	 * @param spawnRate Spawn rate
	 * @return ConfigBlock
	 */
	public ConfigBlock setSpawnRate(int spawnRate) {
		this.spawnRate = spawnRate;
		return this;
	}

	/**
	 * Returns the vein size of the block.
	 * @return Vein size
	 */
	public int getVeinSize() {
		return this.veinSize;
	}
	
	/**
	 * Sets the vein size of the block.
	 * @param veinSize Vein size
	 * @return ConfigBlock
	 */
	public ConfigBlock setVeinSize(int veinSize) {
		this.veinSize = veinSize;
		return this;
	}

	/**
	 * Returns the minimum spawn height of the block.
	 * @return Minimum spawn height
	 */
	public int getMinHeight() {
		return this.minHeight;
	}
	
	/**
	 * Sets the minimum spawn height of the block.
	 * @param minHeight Minimum spawn height
	 * @return ConfigBlock
	 */
	public ConfigBlock setMinHeight(int minHeight) {
		this.minHeight = minHeight;
		return this;
	}

	/**
	 * Returns the maximum spawn height of the block.
	 * @return Maximum spawn height
	 */
	public int getMaxHeight() {
		return this.maxHeight;
	}
	
	/**
	 * Sets the maximum spawn height of the block.
	 * @param maxHeight Maximum spawn height
	 * @return ConfigBlock
	 */
	public ConfigBlock setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
		return this;
	}


	public boolean isEnableOreGen() {
		return enableOreGen;
	}


	public void setEnableOreGen(boolean enableOreGen) {
		this.enableOreGen = enableOreGen;
	}
	

} // end class
