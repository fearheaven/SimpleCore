package alexndr.api.helpers.game;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import alexndr.api.logger.LogHelper;
import alexndr.api.registry.ContentRegistry;
import alexndr.api.registry.Plugin;

import com.google.common.collect.Lists;

/**
 * make non-static so we can have a separate one for each plugin.
 * @author Sinhika
 * @author AleXndrTheGr8st
 */
public class RenderItemHelper 
{
	protected List<RenderDetails> renderList;
	protected Plugin plugin;
	
	//Bow Renders
	protected List<List<Object>> bowRenderList;
	
	
	public RenderItemHelper(Plugin plugin) 
	{
		this.renderList = Lists.newArrayList();
		this.bowRenderList = Lists.newArrayList();
		this.plugin = plugin;
	}

	/**
	 * Creates RenderItem's for all the items and blocks associated with the plugin.
	 * @param plugin The plugin
	 */
	public void renderItemsAndBlocks() 
	{
		for(Item item : ContentRegistry.getPluginItems(plugin.getModId())) {
			RenderDetails details = new RenderDetails(item, plugin.getModId());
			renderList.add(details);
		}
		for(Block block : ContentRegistry.getPluginBlocks(plugin.getModId())) 
		{
			try {
				RenderDetails details = new RenderDetails(Item.getItemFromBlock(block), plugin.getModId());
				renderList.add(details);
			}
			catch (NullPointerException e)
			{
				LogHelper.severe("Null ItemFromBlock for Block" + block.getUnlocalizedName() + "\n");
				throw e;
			}
		} // end-for
	} // end renderItemsAndBlocks()
	
	/**
	 * Returns the list of items and blocks to render.
	 * @return List of RenderDetails
	 */
	public List<RenderDetails> getRenderList() {
		return renderList;
	}

	/**
	 * Adds a bow to the list to be rendered.
	 * @param plugin The plugin the bow belongs to
	 * @param bow The bow
	 */
	public void addBowRenderDetails(Item bow) 
	{
		List<Object> list = Lists.newArrayList();
		list.add(plugin);
		list.add(bow);
		this.bowRenderList.add(list);
	}
	
	public List<List<Object>> getBowRenderList() 
	{
		return bowRenderList;
	}
	
	/**
	 * TODO: rewrite this for 1.9.4
	 * Sets up the 1.8+ Render Item details for all registered blocks and items.
	 * @param event FMLPreInitializationEvent
	 */
	public void renderItemStuff(FMLPreInitializationEvent event) 
	{
		if(event.getSide() == Side.CLIENT) 
		{
			for(List<Object> list : this.bowRenderList) 
			{
				ResourceLocation bowName = ((Item)list.get(1)).getRegistryName();
				
				List<ModelResourceLocation> variants = new ArrayList<ModelResourceLocation>();
				variants.add(new ModelResourceLocation(bowName, "inventory"));
				variants.add(new ModelResourceLocation(bowName + "_pulling_0", "inventory"));
				variants.add(new ModelResourceLocation(bowName + "_pulling_1", "inventory"));
				variants.add(new ModelResourceLocation(bowName + "_pulling_2", "inventory"));
				for (ModelResourceLocation v : variants) {
					ModelBakery.registerItemVariants((Item)list.get(1), v);
				}
			} // end-for
			
			for(RenderDetails details : this.getRenderList()) 
			{
				ModelLoader.setCustomModelResourceLocation(
						details.getItem(), 0, 
						new ModelResourceLocation(details.getItem().getRegistryName(), 
												  "inventory"));
			} // end-for
			
		} // end-if Side.CLIENT
	} // end renderItemStuff()

} // end class