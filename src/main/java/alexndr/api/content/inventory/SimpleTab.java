package alexndr.api.content.inventory;

import alexndr.api.registry.ContentCategories;
import alexndr.api.registry.ContentRegistry;
import alexndr.api.registry.Plugin;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * @author AleXndrTheGr8st
 */
public class SimpleTab extends CreativeTabs
{
	private Item itemForIcon;

	/**
	 * Creates a new Creative Inventory Tab with the specified properties.
	 * @param plugin The plugin the tab belongs to
	 * @param label The name of the tab
	 * @param category The category the tab belongs to
	 */
	public SimpleTab(Plugin plugin, String label, ContentCategories.CreativeTab category) 
	{
		super(label);
		ContentRegistry.registerTab(plugin, this, label, category);
	}
	
	/**
	 * Sets the item that will be used as the icon for the CreativeTab.
	 * @param item The item to use as the icon
	 * @return SimpleTab
	 */
	public SimpleTab setIcon(Item item) {
		this.itemForIcon = item;
		return this;
	}

	@Override
	public ItemStack getTabIconItem() {
		// TODO Auto-generated method stub
		return new ItemStack(itemForIcon);
	}
} // end class
