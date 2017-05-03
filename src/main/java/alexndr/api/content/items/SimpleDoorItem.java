package alexndr.api.content.items;

import alexndr.api.config.IConfigureItemHelper;
import alexndr.api.config.types.ConfigItem;
import alexndr.api.content.blocks.SimpleDoor;
import alexndr.api.helpers.game.TooltipHelper;
import alexndr.api.registry.ContentCategories;
import alexndr.api.registry.ContentRegistry;
import alexndr.api.registry.Plugin;
import net.minecraft.block.Block;
import net.minecraft.item.ItemDoor;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class SimpleDoorItem extends ItemDoor implements IConfigureItemHelper<SimpleDoorItem, ConfigItem>
{
    protected Plugin plugin;
    protected ContentCategories.Item category = ContentCategories.Item.OTHER;
    protected ConfigItem entry;
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
        this.setRegistryName(this.plugin.getModId(), doorName);
        GameRegistry.register(this);
        ContentRegistry.registerItem(this.plugin, this, doorName, this.category);
        return this;
    }
    

    @Override
    public ConfigItem getConfigEntry()
    {
        return this.entry;
    }

    @Override
    public SimpleDoorItem setConfigEntry(ConfigItem entry)
    {
        this.entry = entry;
        this.setAdditionalProperties();
        return this;
    }

    @Override
    public SimpleDoorItem addToolTip(String toolTip)
    {
        TooltipHelper.addTooltipToItem(this, toolTip);
        return this;
    }

    @Override
    public void setAdditionalProperties()
    {
        if(entry.getValueByName("CreativeTab") != null && entry.getValueByName("CreativeTab").isActive()) {
            this.setCreativeTab(entry.getCreativeTab());
        }
    }

} // end class
