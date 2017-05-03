package alexndr.api.content.blocks;

import alexndr.api.config.IConfigureBlockHelper;
import alexndr.api.config.types.ConfigBlock;
import alexndr.api.helpers.game.TooltipHelper;
import alexndr.api.registry.ContentCategories;
import alexndr.api.registry.ContentRegistry;
import alexndr.api.registry.Plugin;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class SimpleStairs extends BlockStairs implements IConfigureBlockHelper<SimpleStairs>
{
    protected Plugin plugin;
    protected ContentCategories.Block category;
    protected ConfigBlock entry;

    /**
     * Creates a simple stair block.
     * @param plugin The plugin the stairs belong to
     * @param modelState BlockState of the block the stairs are made of.
     * @param category The category of the stairs block
     */
    public SimpleStairs(Plugin plugin, IBlockState modelState, ContentCategories.Block category)
    {
        super(modelState);
        this.plugin = plugin;
        this.category = category;
        this.useNeighborBrightness = true;
    }
    
    @Override
    public SimpleStairs setUnlocalizedName(String blockName) 
    {
        super.setUnlocalizedName(blockName);
        setRegistryName(this.plugin.getModId(), blockName);
        GameRegistry.register(this);
        GameRegistry.register(new ItemBlock(this).setRegistryName(this.getRegistryName()));
        ContentRegistry.registerBlock(this.plugin, this, blockName, this.category);
        return this;
    }

    @Override
	public ConfigBlock getConfigEntry() {
		return this.entry;
	}
	
    @Override
    public SimpleStairs setConfigEntry(ConfigBlock entry) 
    {
    	// most properties are obtained from the block the stair as made of, and not configured
    	// and set separately.
		this.entry = entry;
		this.setLightLevel(entry.getLightValue());
		this.setCreativeTab(entry.getCreativeTab());
		this.setAdditionalProperties();
		return this;
    }

    @Override
    public SimpleStairs addToolTip(String toolTip)
    {
        TooltipHelper.addTooltipToBlock(this, toolTip);
        return this;
    }

    @Override
    public void setAdditionalProperties() {}
    
} // end class()
