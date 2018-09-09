/**
 * 
 */
package alexndr.api.content.blocks;

import javax.annotation.Nullable;

import alexndr.api.registry.ContentCategories;
import alexndr.api.registry.Plugin;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * An abstract tile entity-providing block that inherits from SimpleBlock.
 * @author Sinhika
 *
 */
public abstract class SimpleTileEntityBlock<TE extends TileEntity> extends SimpleBlock 
							implements ITileEntityProvider
{

	/**
	 * @param name
	 * @param plugin
	 * @param material
	 * @param category
	 */
	public SimpleTileEntityBlock(String name, Plugin plugin, Material material, 
								 ContentCategories.Block category) 
	{
		super(name, plugin, material, category);
		// TODO Auto-generated constructor stub
	}

	public abstract Class<TE> getTileEntityClass();
	
	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}
	
	@Nullable
	@Override
	public abstract TE createTileEntity(World world, IBlockState state);
} // end class
