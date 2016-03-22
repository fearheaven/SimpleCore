package alexndr.api.core;

import net.minecraftforge.common.MinecraftForge;
import alexndr.api.helpers.events.ClientEventHelper;

public class ProxyClient extends ProxyCommon{
	@Override
	public void registerEventHandlers() {
		MinecraftForge.EVENT_BUS.register(new ClientEventHelper());
	}
}
