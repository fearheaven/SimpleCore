package alexndr.api.content.items;

import java.util.List;

import com.google.common.collect.Lists;

import alexndr.api.config.IConfigureItemHelper;
import alexndr.api.config.types.ConfigItem;
import alexndr.api.core.SimpleCoreAPI;
import alexndr.api.helpers.game.TooltipHelper;
import alexndr.api.registry.Plugin;
import net.minecraft.item.Item;

/**
 * @author AleXndrTheGr8st
 */
public class SimpleItem extends Item implements IConfigureItemHelper<SimpleItem, ConfigItem> 
{
	protected String name;
	private Plugin plugin;
//	private ContentCategories.Item category;
	private ConfigItem entry;
	@SuppressWarnings("unused")
	private List<String> toolTipStrings = Lists.newArrayList();
	
	/**
	 * Creates a simple item, such as an ingot.
	 * @param plugin The plugin the item belongs to
	 * @param category The category of the item
	 */
	public SimpleItem(String itemName, Plugin plugin) 
	{
		this.name = itemName;
		this.plugin = plugin;
//		this.category = category;
		setUnlocalizedName(itemName);
        setRegistryName(plugin.getModId(), itemName);
	}
	
	public void registerItemModel() {
		SimpleCoreAPI.proxy.registerItemRenderer(plugin, this, 0, name);
	}

	/**
	 * Returns the ConfigItem used by this item.
	 * @return ConfigItem
	 */
	public ConfigItem getConfigEntry() {
		return this.entry;
	}
	
	/**
	 * Sets the ConfigItem to be used for this item.
	 * @param entry ConfigItem
	 * @return SimpleItem
	 */
	public SimpleItem setConfigEntry(ConfigItem entry) {
		this.entry = entry;
		this.setAdditionalProperties();
		return this;
	}
	
	/**
	 * Adds a tooltip to the item. Must be unlocalised, so needs to be present in a localization file.
	 * @param toolTip Name of the localisation entry for the tooltip, as a String. Normal format is modId.theitem.info
	 * @return SimpleItem
	 */
	public SimpleItem addToolTip(String toolTip) {
		TooltipHelper.addTooltipToItem(this, toolTip);
		return this;
	}
	
	public void setAdditionalProperties() {
	}
} // end class
