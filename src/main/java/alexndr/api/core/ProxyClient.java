package alexndr.api.core;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import alexndr.api.helpers.events.ClientEventHelper;
import alexndr.api.helpers.game.RenderDetails;
import alexndr.api.helpers.game.RenderItemHelper;
import alexndr.api.logger.LogHelper;
import alexndr.api.registry.Plugin;

public class ProxyClient extends ProxyCommon 
{
	@Override
	public void registerEventHandlers() {
		MinecraftForge.EVENT_BUS.register(new ClientEventHelper());
	}
	
	/**
	 * Sets up the 1.8+ Render Item details for all registered blocks and items.
	 * @param event FMLPostInitializationEvent
	 */
	@Override
	public void renderItemStuff(FMLPostInitializationEvent event) 
	{
		LogHelper.verbose("Creating RenderItem's for all plugins");
		if(event.getSide() == Side.CLIENT) 
		{
			RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
			
			for(RenderDetails details : RenderItemHelper.getRenderList()) {
				renderItem.getItemModelMesher().register(details.getItem(), 0, 
					new ModelResourceLocation(details.getModId() + ":" + details.getItemName(), "inventory"));
			}
			
			for(List<Object> list : SimpleCoreAPI.bowRenderList) {
				String modId = ((Plugin)list.get(0)).getModId();
				String bowName = ((Item)list.get(1)).getUnlocalizedName().substring(5);
				
				List<ModelResourceLocation> variants = new ArrayList<ModelResourceLocation>();
				variants.add(new ModelResourceLocation(modId + ":" + bowName, "inventory"));
				variants.add(new ModelResourceLocation(modId + ":" + bowName + "_pulling_0", "inventory"));
				variants.add(new ModelResourceLocation(modId + ":" + bowName + "_pulling_1", "inventory"));
				variants.add(new ModelResourceLocation(modId + ":" + bowName + "_pulling_2", "inventory"));
				for (ModelResourceLocation v : variants) {
					ModelBakery.registerItemVariants((Item)list.get(1), v);
				}
			}
		} // end-if Side.CLIENT
	} // end renderItemStuff()
	
} // end class
