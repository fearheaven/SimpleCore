package alexndr.api.content.gui;

import alexndr.api.content.inventory.TestFurnaceContainer;
import alexndr.api.content.tiles.TestFurnaceTileEntity;
import alexndr.api.core.APIInfo;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TestFurnaceGui extends SimpleFurnaceGui 
{
//    protected static final ResourceLocation furnaceGuiTextures 
//		= new ResourceLocation(APIInfo.ID,
//						   "textures/gui/container/test_furnace.png");

	public TestFurnaceGui(InventoryPlayer player, TestFurnaceTileEntity iinv) 
	{
		super(new TestFurnaceContainer(player, iinv),
			  new ResourceLocation(APIInfo.ID, "textures/gui/container/test_furnace.png"),
			  player, iinv);
	}


} // end class
