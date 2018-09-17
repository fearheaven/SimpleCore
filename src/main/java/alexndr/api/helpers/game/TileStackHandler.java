package alexndr.api.helpers.game;

import alexndr.api.content.tiles.TileEntityBaseInventory;
import net.minecraftforge.items.ItemStackHandler;

/** 
     * inner class that sub-classes ItemHandler.
     */
    public class TileStackHandler extends ItemStackHandler
    {
    	TileEntityBaseInventory base_tile;
    	
    	public TileStackHandler(int size, TileEntityBaseInventory baseTile) {
			super(size);
			base_tile = baseTile;
		}
    	
        @Override
        public void onContentsChanged(int slot) {
        	super.onContentsChanged(slot);
        	base_tile.markDirty();
        }
    };  // end inner-class TileStackHandler
    
