package alexndr.api.content.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import alexndr.api.config.IConfigureBlockHelper;
import alexndr.api.config.types.ConfigBlock;
import alexndr.api.helpers.game.TabHelper;
import alexndr.api.helpers.game.TooltipHelper;
import alexndr.api.registry.ContentCategories;
import alexndr.api.registry.ContentRegistry;
import alexndr.api.registry.Plugin;

/**
 * @author AleXndrTheGr8st
 */
public class SimpleBlock extends Block implements IConfigureBlockHelper<SimpleBlock>
{
	protected Plugin plugin;
	protected Material material;
	protected ContentCategories.Block category;
	protected ConfigBlock entry;
	
	//Additional Block Attributes
	protected boolean dropItem = false;
	protected String itemToDrop;
	protected int quantityToDrop = 1;
	protected boolean fireSource = false;
	protected boolean isLeaves = false;
	protected boolean isWood = false;
	protected boolean isOre = false;
	
	/**
	 * Creates a simple block, such as an ore or a storage block.
	 * @param plugin The plugin the block belongs to
	 * @param material The material of the block
	 * @param category The category of the block
	 */
	public SimpleBlock(Plugin plugin, Material material, ContentCategories.Block category) 
	{
		super(material);
		this.plugin = plugin;
		this.material = material;
		this.category = category;
	}
	
	public SimpleBlock setStepSound(SoundType sound)
	{
		setSoundType(sound);
		return this;
	} // end setStepSound
	
	@Override
	public SimpleBlock setUnlocalizedName(String blockName) 
	{
		super.setUnlocalizedName(blockName);
        this.setRegistryName(this.plugin.getModId(), blockName);
        GameRegistry.register(this);
        GameRegistry.register(new ItemBlock(this).setRegistryName(this.getRegistryName()));
		ContentRegistry.registerBlock(this.plugin, this, blockName, this.category);
		return this;
	}
	
	/**
	 * Returns the ConfigBlock used by this block.
	 * @return ConfigBlock
	 */
	public ConfigBlock getConfigEntry() {
		return this.entry;
	}
	
	/**
	 * Sets the ConfigBlock to be used for this block.
	 * @param entry ConfigBlock
	 * @return SimpleBlock
	 */
	public SimpleBlock setConfigEntry(ConfigBlock entry) 
	{
		this.entry = entry;
		this.setHardness(entry.getHardness());
		this.setResistance(entry.getResistance());
		this.setLightLevel(entry.getLightValue());
		this.setHarvestLevel(entry.getHarvestTool(), entry.getHarvestLevel());
		this.setAdditionalProperties();
		return this;
	}

	/**
	 * Sets the CreativeTab the block will be placed in.
	 * CreativeTab needs to be added to the ContentRegistry.
	 * @param tabName Name of the CreativeTab
	 * @return ConfigBlock
	 */
	public SimpleBlock setCreativeTab(String tabName) {
		this.setCreativeTab(TabHelper.getTab(tabName));
		return this;
	}

	/**
	 * Adds a tooltip to the block. Must be unlocalised, so needs to be present in a localization file.
	 * @param toolTip Name of the localisation entry for the tooltip, as a String. Normal format is modId.theitem.info
	 * @return SimpleBlock
	 */
	public SimpleBlock addToolTip(String toolTip) {
		TooltipHelper.addTooltipToBlock(this, toolTip);
		return this;
	}

	/**
	 * Returns a boolean for whether or not to drop an item.
	 * @return Drop item boolean
	 */
	public boolean getDropItem() {
		return this.dropItem;
	}

	/**
	 * Returns the item to drop when this block is broken.
	 * @return Item to drop
	 */
	public Item getItemToDrop() {
		return (Item) Item.getByNameOrId(this.itemToDrop);
	}

	/**
	 * Sets the drop item for the block.
	 * @param dropItem Drop Item
	 * @return ConfigBlock
	 */
	public SimpleBlock setDropItem(boolean dropItem) {
		this.dropItem = dropItem;
		return this;
	}

	/**
	 * Sets the item to drop when this block is broken.
	 * @param itemToDrop Item to drop
	 * @return ConfigBlock
	 */
	public SimpleBlock setItemToDrop(Item itemToDrop) {
		this.itemToDrop = itemToDrop.getUnlocalizedName();
		return this;
	}
	
	/**
	 * Sets the item to drop when this block is broken.
	 * This method uses the String name of the item.
	 * Should be of the form modId:itemName, eg. simpleores:onyx_gem.
	 * @param itemToDrop String name of the item
	 * @return ConfigBlock
	 */
	public SimpleBlock setItemToDrop(String itemToDrop) {
		this.itemToDrop = itemToDrop;
		return this;
	}

	/**
	 * Returns the quantity to drop when this block is broken.
	 * @return Quantity to drop
	 */
	public int getQuantityToDrop() {
		return this.quantityToDrop;
	}
	
	/**
	 * Sets the quantity to drop when this block is broken. 
	 * @param quantityToDrop Quantity to drop
	 * @return configBlock
	 */
	public SimpleBlock setQuantityToDrop(int quantityToDrop) {
		this.quantityToDrop = quantityToDrop;
		return this;
	}



	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) 
	{
		if (this.getDropItem()) {
			return this.getItemToDrop();
		}
		return Item.getItemFromBlock(this);
	}
	
	@Override
	public int quantityDropped(Random random) 
	{
		if(this.getDropItem()) {
			return this.getQuantityToDrop();
		}
		return 1;
	}
	
	@Override
	public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) 
	{
        super.dropBlockAsItemWithChance(worldIn, pos, state, chance, fortune);
        if(this.getItemDropped(state, worldIn.rand, fortune) != Item.getItemFromBlock(this)) {
        	int amount = MathHelper.getInt(worldIn.rand, 16, 33);
        	this.dropXpOnBlockBreak(worldIn, pos, amount);
        }
	}
	
	@Override
	public int quantityDroppedWithBonus(int fortune, Random random) 
	{
		if(fortune > 0 && this.getItemDropped((IBlockState)this.getBlockState().getValidStates().iterator().next(), random, fortune) != Item.getItemFromBlock(this)) {
			int var = random.nextInt(fortune + 2) - 1;
			if(var < 0) var = 0;
			return this.quantityDropped(random) * (var + 1);
		}
		else return this.quantityDropped(random);
	}
	
	/**
	 * Sets whether or not the block is a fire source.
	 * Fire source blocks can sustain fire indefinitely (eg. Netherrack).
	 * @param fireSource Fire source boolean
	 * @return SimpleBlock
	 */
	public SimpleBlock setFireSource(boolean fireSource) {
		this.fireSource = fireSource;
		return this;
	}

	@Override
	public boolean isFireSource(World world, BlockPos pos, EnumFacing side) 
	{
		return fireSource;
	}
	
	/**
	 * Sets whether or not the block is leaves.
	 * Blocks that are leaves will decay after time unless near a log block.
	 * @param isLeaves Is leaves boolean
	 * @return ConfigBlock
	 */
	public SimpleBlock setIsLeaves(boolean isLeaves) {
		this.isLeaves = isLeaves;
		return this;
	}


	@Override
	public boolean isLeaves(IBlockState state, IBlockAccess world, BlockPos pos) 
	{
		return isLeaves;
	}
	
	/**
	 * Sets whether or not the block is a wood block.
	 * Wood blocks burn, and can also sustain leaves.
	 * @param isWood Is wood boolean
	 * @return ConfigBlock
	 */
	public SimpleBlock setIsWood(boolean isWood) {
		this.isWood = isWood;
		return this;
	}

	@Override
	public boolean isWood(IBlockAccess world, BlockPos pos) {
		return isWood;
	}
	
	@Override
	public boolean isBeaconBase(IBlockAccess worldObj, BlockPos pos, BlockPos beaconPos) 
	{
		return entry.getBeaconBase();
	}
	
	public void setAdditionalProperties() 
	{
		if(entry.getUnbreakable()) 
		{
			this.setBlockUnbreakable();
		}
	}
} // end class
