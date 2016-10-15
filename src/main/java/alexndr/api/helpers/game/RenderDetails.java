package alexndr.api.helpers.game;

import net.minecraft.item.Item;

/**
 * Holds data for simple items, to be passed to model baking/custom resource load methods.
 * 
 * @author AleXndrTheGr8st, Sinhika
 */
public class RenderDetails 
{
	protected Item item;
	protected String modId;
	protected String itemName;
	
	/**
	 * Creates a new RenderDetails for the item.
	 * @param item The item
	 * @param modId The modId the item belongs to
	 */
	public RenderDetails(Item item, String modId) 
	{
		this.item = item;
		this.modId = modId;
		this.itemName = item.getUnlocalizedName().substring(5);
	}
	
	/**
	 * Returns the item associated with this RenderDetails.
	 * @return Item
	 */
	public Item getItem() {
		return this.item;
	}
	
	/**
	 * Returns the modId this item belongs to.
	 * @return ModId
	 */
	public String getModId() {
		return this.modId;
	}
	
	/**
	 * Returns the name of the item.
	 * @return Item name
	 */
	public String getItemName() {
		return this.itemName;
	}
} // end class RenderDetails
