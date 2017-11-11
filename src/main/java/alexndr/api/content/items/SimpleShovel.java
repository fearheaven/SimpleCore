package alexndr.api.content.items;

import java.util.List;

import com.google.common.collect.Lists;

import alexndr.api.config.IConfigureItemHelper;
import alexndr.api.config.types.ConfigTool;
import alexndr.api.core.SimpleCoreAPI;
import alexndr.api.helpers.game.TooltipHelper;
import alexndr.api.registry.Plugin;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;

/**
 * @author AleXndrTheGr8st
 */
public class SimpleShovel extends ItemSpade implements IConfigureItemHelper<SimpleShovel, ConfigTool>
{
	protected String name;
	private final ToolMaterial material;
	private Plugin plugin;
//	private ContentCategories.Item category = ContentCategories.Item.TOOL;
	private ConfigTool entry;
	@SuppressWarnings("unused")
	private List<String> toolTipStrings = Lists.newArrayList();

	/**
	 * Creates a simple shovel, such as the Tin Shovel.
	 * @param plugin The plugin the tool belongs to
	 * @param material The ToolMaterial of the tool
	 */
	public SimpleShovel(String shovelName, Plugin plugin, ToolMaterial material) 
	{
		super(material);
		this.name = shovelName;
		this.plugin = plugin;
		this.material = material;
		setUnlocalizedName(shovelName);
        setRegistryName(plugin.getModId(), shovelName);
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
	 * @return SimpleShovel
	 */
	public SimpleShovel setConfigEntry(ConfigTool entry) {
		this.entry = entry;
		this.setHarvestLevel("shovel", entry.getHarvestLevel());
		this.setAdditionalProperties();
		return this;
	}
	
	/**
	 * Adds a tooltip to the tool. Must be unlocalised, so needs to be present in a localization file.
	 * @param toolTip Name of the localisation entry for the tooltip, as a String. Normal format is modId.theitem.info
	 * @return SimpleShovel
	 */
	public SimpleShovel addToolTip(String toolTip) {
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
}
