package alexndr.api.core;

import alexndr.api.content.blocks.TestFurnace;
import alexndr.api.content.tiles.TestFurnaceTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.registries.IForgeRegistry;

public class ModBlocks 
{
	// MACHINES
	public static TestFurnace test_furnace = 
			new TestFurnace("test_furnace", SimpleCoreAPI.plugin,false);
	public static TestFurnace lit_test_furnace =
			new TestFurnace("lit_test_furnace", SimpleCoreAPI.plugin,true);

	/**
	 * configure mod blocks from config settings.
	 */
	public static void configureBlocks() 
	{
		if (APISettings.createTestFurnace && APISettings.testFurnace.isEnabled()) {
			test_furnace.setConfigEntry(APISettings.testFurnace)
					.setStepSound(SoundType.STONE)
					.setCreativeTab(CreativeTabs.REDSTONE);
			lit_test_furnace.setConfigEntry(APISettings.testFurnace)
				.setStepSound(SoundType.STONE).setDropItem(true)
				.setItemToDrop(test_furnace.getItemToDrop());
			
			TestFurnace.setUnlit_furnace(test_furnace);
			TestFurnace.setLit_furnace(lit_test_furnace);
		}
	} // end configureBlocks

	/**
	 * Register blocks with Forge.
	 * 
	 * @param registry Forge block registry interface.
	 */
	public static void register(IForgeRegistry<Block> registry) 
	{
		if (APISettings.createTestFurnace && APISettings.testFurnace.isEnabled()) 
		{
			registry.register(test_furnace);
			registry.register(lit_test_furnace);
			TileEntity.register(TestFurnaceTileEntity.tilename, TestFurnaceTileEntity.class);
		}
	} // end register()

	/**
	 * register ItemBlocks with Forge.
	 * 
	 * @param registry Forge item registry interface.
	 */
	public static void registerItemBlocks(IForgeRegistry<Item> registry) 
	{
		if (APISettings.createTestFurnace && APISettings.testFurnace.isEnabled()) {
			registry.register(test_furnace.createItemBlock());
		}
	} // end registerItemBlocks()

	/**
	 * register models of ItemBlocks with Forge.
	 */
	public static void registerModels() 
	{
		if (APISettings.createTestFurnace && APISettings.testFurnace.isEnabled()) {
			test_furnace.registerItemModel(Item.getItemFromBlock(test_furnace));
		}
	} // end registerModels()

} // end class
