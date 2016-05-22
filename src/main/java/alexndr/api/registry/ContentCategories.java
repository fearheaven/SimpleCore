package alexndr.api.registry;

import net.minecraft.creativetab.CreativeTabs;

/**
 * @author AleXndrTheGr8st
 */
public class ContentCategories 
{
	/**
	 * @author AleXndrTheGr8st
	 * Categories of blocks.
	 */
	public static enum Block {
		/**
		 * General blocks.
		 */
		GENERAL, 
		
		/**
		 * Ore blocks.
		 */
		ORE,
		
		/**
		 * Machine blocks, eg. Furnaces.
		 */
		MACHINE, 
		
		/**
		 * Other blocks.
		 */
		OTHER;
	}
	
	/**
	 * @author AleXndrTheGr8st
	 * Categories of items.
	 */
	public static enum Item {
		/**
		 * Ingots of metals, etc..
		 */
		INGOT, 
		
		/**
		 * Other materials that aren't ingots.
		 */
		MATERIAL, 
		
		/**
		 * Tool items.
		 */
		TOOL, 
		
		/**
		 * Armor items.
		 */
		ARMOR, 
		
		/**
		 * Weapon items.
		 */
		WEAPON, 
		
		/**
		 * Other items.
		 */
		OTHER;
	}
	
	/**
	 * @author AleXndrTheGr8st
	 * Categories of CreativeTabs.
	 */
	public static enum CreativeTab {
		/**
		 * Creative tab for general blocks and items. Used if separate tabs are disabled.
		 */
		GENERAL(CreativeTabs.BUILDING_BLOCKS),
		
		/**
		 * Creative tab for blocks.
		 */
		BLOCKS(CreativeTabs.BUILDING_BLOCKS), 
		
		/**
		 * Creative tab for materials such as ingots, etc.
		 */
		MATERIALS(CreativeTabs.MATERIALS), 
		
		/**
		 * Creative tab for decoration blocks/items.
		 */
		DECORATIONS(CreativeTabs.DECORATIONS), 
		
		/**
		 * Creative tab for tools.
		 */
		TOOLS(CreativeTabs.TOOLS), 
		
		/**
		 * Creative tab for armor and weapons.
		 */
		COMBAT(CreativeTabs.COMBAT), 
		
		/**
		 * Creative tab for redstone-related or powered items & blocks.
		 */
		REDSTONE(CreativeTabs.REDSTONE),
		
		/**
		 * Creative tab for other blocks/items.
		 */
		OTHER(CreativeTabs.MISC);
		
		public final CreativeTabs vanillaTab;
		
		private CreativeTab(CreativeTabs vanillaTab) {
			this.vanillaTab = vanillaTab;
		}
	} // end enum CreativeTab
} // end class ContentCategories
