/**
 * 
 */
package alexndr.api.content.blocks;

import alexndr.api.config.types.ConfigBlock;
import alexndr.api.helpers.game.IConfigureBlockHelper;
import alexndr.api.helpers.game.TooltipHelper;
import alexndr.api.logger.LogHelper;
import alexndr.api.registry.ContentCategories;
import alexndr.api.registry.ContentRegistry;
import alexndr.api.registry.Plugin;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * @author cyhiggin
 *
 */
public class SimpleDoor extends BlockDoor implements IConfigureBlockHelper<SimpleDoor>
{
    protected Plugin plugin;
    protected Material material;
    protected ContentCategories.Block category;
    protected ConfigBlock entry;
    protected ResourceLocation ItemOfDoorResource;

    /**
     * @param plugin the mod class plugin field; stores the modid, among other things.
     * @param materialIn either Material.IRON or Material.WOOD. If Material.IRON, requires redstone signal
     *      to open. Anything else can be opened by hand.
     * @param itemOfBlockName the unlocalized name of the door *item*.
     * @param category a valid ContentCategory.
     */
    public SimpleDoor(Plugin plugin, Material materialIn, String itemOfBlockName, 
                      ContentCategories.Block category)
    {
        super(materialIn);
        this.plugin = plugin;
        this.material = materialIn;
        this.category = category;
        this.ItemOfDoorResource = new ResourceLocation(plugin.getModId(), itemOfBlockName);
    }
    
    @Override
    public SimpleDoor setUnlocalizedName(String blockName) 
    {
        super.setUnlocalizedName(blockName);
        setRegistryName(this.plugin.getModId(), blockName);
        GameRegistry.register(this);
        ContentRegistry.registerBlock(this.plugin, this, blockName, this.category);
        return this;
    }
    
    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
    {
        return new ItemStack(this.getItem());
    }

    protected Item getItem()
    {
        if (ItemOfDoorResource == null) { return null; }
        if (Item.REGISTRY.getObject(ItemOfDoorResource) == null) {
            LogHelper.severe(plugin.getName(), "Item of Door " + ItemOfDoorResource + " never initialized!");
        }
        return Item.REGISTRY.getObject(ItemOfDoorResource);
    } // end getItem()

    /**
     * Returns the ConfigBlock used by this block.
     * @return ConfigBlock
     */
    public ConfigBlock getConfigEntry() {
        return this.entry;
    }

    @Override
    public SimpleDoor setConfigEntry(ConfigBlock entry)
    {
        this.entry = entry;
        this.setHardness(entry.getHardness());
        this.setResistance(entry.getResistance());
        this.setLightLevel(entry.getLightValue());
        this.setHarvestLevel(entry.getHarvestTool(), entry.getHarvestLevel());
        this.setCreativeTab(entry.getCreativeTab());
        this.setAdditionalProperties();
        return this;
    }

    @Override
    public SimpleDoor addToolTip(String toolTip)
    {
        TooltipHelper.addTooltipToBlock(this, toolTip);
        return this;
    }

    
    @Override
    public void setAdditionalProperties() {}

} // end class
