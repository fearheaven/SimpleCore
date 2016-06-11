package alexndr.api.helpers.events;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemPickupEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemSmeltedEvent;
import alexndr.api.helpers.game.StatTriggersHelper;

/**
 * @author AleXndrTheGr8st
 */
public class CommonEventHelper {
	
	
	@SubscribeEvent
	public void onItemCrafted(ItemCraftedEvent event) {
		StatTriggersHelper.notifyCrafting(event.player, event.crafting.getItem(), event.craftMatrix);
	}
	
	@SubscribeEvent
	public void onItemSmelted(ItemSmeltedEvent event) {
		StatTriggersHelper.notifySmelting(event.player, event.smelting.getItem());
	}
	
	@SubscribeEvent
	public void onItemPickedUp(ItemPickupEvent event) {
		StatTriggersHelper.notifyPickup(event.pickedUp, event.player);
	}
}
