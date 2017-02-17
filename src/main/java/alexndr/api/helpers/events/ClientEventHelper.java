package alexndr.api.helpers.events;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import alexndr.api.content.items.SimpleBow;
import alexndr.api.helpers.game.TooltipHelper;
import mcjty.lib.tools.MinecraftTools;

/**
 * @author AleXndrTheGr8st
 */
public class ClientEventHelper 
{
	@SubscribeEvent
	public void fovEvent(FOVUpdateEvent event) 
	{
		EntityPlayer player = MinecraftTools.getPlayer(Minecraft.getMinecraft());
		float baseFOV = event.getFov();
		if(player.isHandActive() && (player.getHeldItem(player.getActiveHand()) != null)
			&& (player.getHeldItem(player.getActiveHand()).getItem() instanceof SimpleBow)) 
		{
			SimpleBow bow = (SimpleBow)player.getActiveItemStack().getItem();
			int useRemaining = new ItemStack(bow).getMaxItemUseDuration() - player.getItemInUseCount();
			float fov = baseFOV - (useRemaining * bow.getZoomAmount() / 20.0F);
			if (fov < baseFOV - bow.getZoomAmount())
				fov = (baseFOV - bow.getZoomAmount());
			event.setNewfov(fov);
		} 
		else {
			event.setNewfov(event.getFov());
		}
	} // end fovEvent
	
	@SubscribeEvent
	public void toolTipEvent(ItemTooltipEvent event) {
		TooltipHelper.notifyTooltip(event);
	}
}
