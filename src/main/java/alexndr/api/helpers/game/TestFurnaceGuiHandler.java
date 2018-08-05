package alexndr.api.helpers.game;

import alexndr.api.content.gui.TestFurnaceGui;
import alexndr.api.content.inventory.TestFurnaceContainer;
import alexndr.api.content.tiles.TestFurnaceTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class TestFurnaceGuiHandler implements IGuiHandler 
{
	public static final int TESTFURNACE_ID	= 97;

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) 
	{
		return new TestFurnaceContainer(player.inventory, 
							(TestFurnaceTileEntity) world.getTileEntity(new BlockPos(x, y, z)));
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) 
	{
		return new TestFurnaceGui(player.inventory, 
								 (TestFurnaceTileEntity) world.getTileEntity(new BlockPos(x, y, z)));
	}

} // end class
