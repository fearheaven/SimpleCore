package alexndr.api.helpers.game;

import alexndr.api.content.gui.TestFurnaceGui;
import alexndr.api.content.inventory.TestFurnaceContainer;
import alexndr.api.content.tiles.TestFurnaceTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class TestFurnaceGuiHandler implements IGuiHandler 
{
	public static final int TESTFURNACE_ID	= 97;

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) 
	{
		TileEntity machine = world.getTileEntity(new BlockPos(x, y, z));
		
        if(machine == null)
        	return null;
            
        if(machine instanceof TestFurnaceTileEntity)
        	return new TestFurnaceContainer(player.inventory, (TestFurnaceTileEntity) machine);
        
        return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) 
	{
		TileEntity machine = world.getTileEntity(new BlockPos(x, y, z));
		
        if(machine == null)
        	return null;
            
        if(machine instanceof TestFurnaceTileEntity)
        	return new TestFurnaceGui(player.inventory, (TestFurnaceTileEntity) machine);
        
        return null;
	}

} // end class
