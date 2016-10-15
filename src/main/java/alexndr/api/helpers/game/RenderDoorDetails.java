package alexndr.api.helpers.game;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

public class RenderDoorDetails extends RenderDetails 
{
	protected Block doorBlock;
	
	public RenderDoorDetails(Block block, Item item, String modId) 
	{
		super(item, modId);
		this.doorBlock = block;
	}

	public Block getBlock()
	{
		return doorBlock;
	}
} // end class
