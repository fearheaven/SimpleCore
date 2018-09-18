package alexndr.api.content.blocks;

import alexndr.api.content.tiles.TestFurnaceTileEntity;
import alexndr.api.core.SimpleCoreAPI;
import alexndr.api.helpers.game.TestFurnaceGuiHandler;
import alexndr.api.registry.ContentCategories;
import alexndr.api.registry.Plugin;
import net.minecraft.block.Block;
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
	// repeat for custom furnace classes
	private static Block unlit_furnace;
	private static Block lit_furnace;
	
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

	public static Block getUnlit_furnace() {
		return TestFurnace.unlit_furnace;
	}

	public static void setUnlit_furnace(Block unlit_furnace) {
		TestFurnace.unlit_furnace = unlit_furnace;
	}

	public static Block getLit_furnace() {
		return TestFurnace.lit_furnace;
	}

	public static void setLit_furnace(Block lit_furnace) {
		TestFurnace.lit_furnace = lit_furnace;
	}
	
   /**
     * Mostly cut & pasted from BlockFurnace. This *MUST* be overridden for custom classes...
     * @param active
     * @param worldIn
     * @param pos
     */
    public static void setState(boolean active, World worldIn, BlockPos pos)
    {
        IBlockState iblockstate = worldIn.getBlockState(pos);
        TileEntity tileentity = worldIn.getTileEntity(pos);
    	
        keepInventory = true;

        if (active)
        {
            worldIn.setBlockState(pos, TestFurnace.getLit_furnace().getDefaultState().withProperty(FACING, iblockstate.getValue(FACING)), 3);
            worldIn.setBlockState(pos, TestFurnace.getLit_furnace().getDefaultState().withProperty(FACING, iblockstate.getValue(FACING)), 3);
        }
        else
        {
            worldIn.setBlockState(pos, TestFurnace.getUnlit_furnace().getDefaultState().withProperty(FACING, iblockstate.getValue(FACING)), 3);
            worldIn.setBlockState(pos, TestFurnace.getUnlit_furnace().getDefaultState().withProperty(FACING, iblockstate.getValue(FACING)), 3);
        }

        keepInventory = false;

        if (tileentity != null)
        {
            tileentity.validate();
            worldIn.setTileEntity(pos, tileentity);
        }
    } // end setState()

} // end class
