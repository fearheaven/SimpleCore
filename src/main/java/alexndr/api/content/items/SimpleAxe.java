package alexndr.api.content.items;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

import com.google.common.collect.Lists;

import alexndr.api.config.IConfigureItemHelper;
import alexndr.api.config.types.ConfigTool;
import alexndr.api.core.SimpleCoreAPI;
import alexndr.api.helpers.game.TooltipHelper;
import alexndr.api.registry.ContentCategories;
import alexndr.api.registry.ContentRegistry;
import alexndr.api.registry.Plugin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author AleXndrTheGr8st
 */
public class SimpleAxe extends ItemAxe implements IConfigureItemHelper<SimpleAxe, ConfigTool>
{
	private final ToolMaterial material;
	private Plugin plugin;
	private ContentCategories.Item category = ContentCategories.Item.TOOL;
	private ConfigTool entry;
	@SuppressWarnings("unused")
	private List<String> toolTipStrings = Lists.newArrayList();
	protected String name;

	/**
	 * Creates a simple axe, such as the Mythril Axe.
	 * @param plugin The plugin the tool belongs to
	 * @param material The ToolMaterial of the tool
	 * @param damage axe damage to entity
	 * @param speed axe weapon speed.
	 */
	public SimpleAxe(Plugin plugin, ToolMaterial material, float damage, float speed) 
	{
		super(material, damage, speed);
		this.plugin = plugin;
		this.material = material;
	}
	
	/**
	 * constructor using helper functions for axe damage and speed.
	 * @param plugin The plugin the tool belongs to
	 * @param material The ToolMaterial of the tool
	 */
	public SimpleAxe(Plugin plugin, ToolMaterial material) 
	{
		super(material, getAttackDamage(material), getAttackSpeed(material));
		this.plugin = plugin;
		this.material = material;
	}

	/**
	 * Lifted from Zot201's OnlySilver code, with permission.
	 * @param m
	 * @return
	 */
	protected static float getAttackDamage(ToolMaterial m) 
	{
		return 2 * (int) Math.rint(
				0.5 * (m.getDamageVsEntity() * 0.7058823529 + 6.352941176));
	}

	/**
	 * Lifted from Zot201's OnlySilver code, with permission.
	 * @param m
	 * @return
	 */
	protected static float getAttackSpeed(ToolMaterial m) 
	{
		return roundToFloatDecimal(
				(m.getHarvestLevel() + m.getEfficiencyOnProperMaterial()) * 0.02312138728 - 3.275722543,
				RoundingMode.HALF_EVEN, 1);
	}

	/**
	 * Lifted from Zot201's OnlySilver code, with permission.
	 * @param a
	 * @param mode
	 * @param precision
	 * @return
	 */
	protected static float roundToFloatDecimal(double a, RoundingMode mode, int precision) {
		return new BigDecimal(a).round(new MathContext(precision, mode)).floatValue();
	}

	@Override
	public SimpleAxe setUnlocalizedName(String axeName) 
	{
		super.setUnlocalizedName(axeName);
		this.name = axeName;
        setRegistryName(this.plugin.getModId(), axeName);
//        GameRegistry.register(this);
		ContentRegistry.registerItem(this.plugin, this, axeName, this.category);
		this.setHarvestLevel("axe", entry.getHarvestLevel());
		return this;
	}
	
	public void registerItemModel() {
		SimpleCoreAPI.proxy.registerItemRenderer(plugin, this, 0, name);
	}

	/**
	 * Returns the ConfigTool used by this tool.
	 * @return ConfigTool
	 */
	public ConfigTool getConfigEntry() {
		return this.entry;
	}
	
	/**
	 * Sets the ConfigTool to be used for this tool.
	 * @param entry ConfigTool
	 * @return SimpleAxe
	 */
	public SimpleAxe setConfigEntry(ConfigTool entry) {
		this.entry = entry;
		this.setAdditionalProperties();
		return this;
	}
	
	/* lifted from McJty's CompatItemTool class */
    protected ActionResult<ItemStack> clOnItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
        return super.onItemRightClick(worldIn, playerIn, hand);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
        return clOnItemRightClick(worldIn, playerIn, hand);
    }

    /**
     * Called when a Block is right-clicked with this Item
     */
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return clOnItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
    }

    protected EnumActionResult clOnItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
    }
    /* end McJty */
    
 
	/**
	 * Adds a tooltip to the tool. Must be unlocalised, so needs to be present in a localization file.
	 * @param toolTip Name of the localisation entry for the tooltip, as a String. Normal format is modId.theitem.info
	 * @return SimpleAxe
	 */
	public SimpleAxe addToolTip(String toolTip) {
		TooltipHelper.addTooltipToItem(this, toolTip);
		return this;
	}
	
	@Override
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
		return this.material.getRepairItemStack().getItem() == repair.getItem() ? true : super.getIsRepairable(toRepair, repair);
	}
	
	public void setAdditionalProperties() {
//		if(entry.getValueByName("CreativeTab") != null && entry.getValueByName("CreativeTab").isActive()) {
//			this.setCreativeTab(entry.getCreativeTab());
//		}
	}
} // end class
