package alexndr.api.content.blocks;

import alexndr.api.content.tiles.TestFurnaceTileEntity;
import alexndr.api.core.SimpleCoreAPI;
import alexndr.api.helpers.game.TestFurnaceGuiHandler;
import alexndr.api.registry.ContentCategories;
import alexndr.api.registry.Plugin;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TestFurnace extends SimpleFurnace<TestFurnaceTileEntity> 
{
	public TestFurnace(String furnace_name, Plugin plugin, boolean isActive) 
	{
		super(furnace_name, plugin, Material.ROCK, ContentCategories.Block.MACHINE, isActive);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TestFurnaceTileEntity();
	}

	@Override
	public Class<TestFurnaceTileEntity> getTileEntityClass() {
		return TestFurnaceTileEntity.class;
	}

	@Override
	public TestFurnaceTileEntity createTileEntity(World world, IBlockState state) {
		return new TestFurnaceTileEntity();
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) 
	{
		if (!worldIn.isRemote) {
			playerIn.openGui(SimpleCoreAPI.instance, TestFurnaceGuiHandler.TESTFURNACE_ID, 
							 worldIn, pos.getX(), pos.getY(), pos.getZ());
		}
		return true;
	} 

} // end class
