package alexndr.api.content.items;

import alexndr.api.content.blocks.SimpleDoor;
import alexndr.api.core.SimpleCoreAPI;
import alexndr.api.helpers.game.TooltipHelper;
import alexndr.api.registry.ContentCategories;
import alexndr.api.registry.ContentRegistry;
import alexndr.api.registry.Plugin;
import net.minecraft.block.Block;
import net.minecraft.item.ItemDoor;

public class SimpleDoorItem extends ItemDoor
{
	protected String name;
    protected Plugin plugin;
    protected ContentCategories.Item category = ContentCategories.Item.OTHER;
    // protected ConfigItem entry; // not configurable
    protected Block blockDoor;
    
    public SimpleDoorItem(Plugin plugin, SimpleDoor blockDoor)
    {
        super(blockDoor);
        this.blockDoor = blockDoor;
        this.plugin = plugin;
    }
    
    @Override
    public SimpleDoorItem setUnlocalizedName(String doorName) 
    {
        super.setUnlocalizedName(doorName);
		this.name = doorName;
        this.setRegistryName(this.plugin.getModId(), doorName);
 //       GameRegistry.register(this);
        ContentRegistry.registerItem(this.plugin, this, doorName, this.category);
        return this;
    }
    
	public void registerItemModel() {
		SimpleCoreAPI.proxy.registerItemRenderer(plugin, this, 0, name);
	}

     public SimpleDoorItem addToolTip(String toolTip)
    {
        TooltipHelper.addTooltipToItem(this, toolTip);
        return this;
    }

} // end class
