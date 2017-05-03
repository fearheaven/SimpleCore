package alexndr.api.content.items;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.registry.GameRegistry;
import alexndr.api.config.IConfigureItemHelper;
import alexndr.api.config.types.ConfigArmor;
import alexndr.api.helpers.game.TooltipHelper;
import alexndr.api.registry.ContentCategories;
import alexndr.api.registry.ContentRegistry;
import alexndr.api.registry.Plugin;
import mcjty.lib.compat.CompatItemArmor;

import com.google.common.collect.Lists;

/**
 * @author AleXndrTheGr8st
 */
@SuppressWarnings("deprecation")
public class SimpleArmor extends CompatItemArmor implements IConfigureItemHelper<SimpleArmor, ConfigArmor>
{
	// { FEET, LEGS, CHEST, HEAD }
	protected ItemArmor[] armor = { null, null, null, null };

	private final ArmorMaterial material;
	protected Plugin plugin;
	protected ContentCategories.Item category = ContentCategories.Item.ARMOR;
	protected ConfigArmor entry;
	protected EntityEquipmentSlot slot;
	protected String type, texturePath;
	@SuppressWarnings("unused")
	private List<String> toolTipStrings = Lists.newArrayList();
	
	/**
	 * Creates a simple armor item, such as the Mythril Chestplate.
	 * @param plugin The plugin the armor belongs to
	 * @param material The ArmorMaterial of the armor
	 * @param slot The armor slot the piece fits
	 */
	public SimpleArmor(Plugin plugin, ArmorMaterial material, EntityEquipmentSlot slot) 
	{
		super(material, 1, slot);
		this.plugin = plugin;
		this.material = material;
		this.slot = slot;
	}

	@Override
	public SimpleArmor setUnlocalizedName(String armorName) {
		super.setUnlocalizedName(armorName);
        setRegistryName(this.plugin.getModId(), armorName);
        GameRegistry.register(this);
		ContentRegistry.registerItem(this.plugin, this, armorName, this.category);
		return this;
	}
	
	/**
	 * Returns the ConfigArmor used by this armor.
	 * @return ConfigArmor
	 */
	public ConfigArmor getConfigEntry() {
		return this.entry;
	}
	
	/**
	 * Sets the ConfigArmor to be used for this armor.
	 * @param entry ConfigArmor
	 * @return SimpleArmor
	 */
	public SimpleArmor setConfigEntry(ConfigArmor entry) {
		this.entry = entry;
		return this;
	}
	
	/**
	 * Sets the type of armor, ie. "copper", "mythril", etc. Needs to match the names of the image files.
	 * ie. copper_1.png, mythril_2.png.
	 * @param armorType String of the armor type name, ie. "copper"
	 * @return SimpleArmor
	 */
	public SimpleArmor setType(String armorType) {
		this.type = armorType;
		this.setArmorTexturePath(this.plugin.getModId(), armorType, this.slot);
		return this;
	}
	
	/**
	 * Adds a tooltip to the armor. Must be unlocalised, so needs to be present in a localization file.
	 * @param toolTip Name of the localisation entry for the tooltip, as a String. Normal format is modId.theitem.info
	 * @return SimpleArmor
	 */
	public SimpleArmor addToolTip(String toolTip) {
		TooltipHelper.addTooltipToItem(this, toolTip);
		return this;
	}
	
	/**
	 * Adds a tooltip to the armor. Must be unlocalised, so needs to be present in a localization file.
	 * @param toolTip Name of the localisation entry for the tooltip, as a String. Normal format is modId.theitem.info
	 * @param color Color of the tooltip
	 * @return SimpleArmor
	 */
	public SimpleArmor addToolTip(String toolTip, TextFormatting color) {
		TooltipHelper.addTooltipToItem(this, color + I18n.translateToLocal(toolTip));
		return this;
	}
	
	@Override
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) 
	{
		return this.material.getRepairItem() == repair.getItem() 
				? true 
				: super.getIsRepairable(toRepair, repair);
	}
	
	@Override
	public String getArmorTexture(ItemStack itemstack, Entity entity, EntityEquipmentSlot slot, String layer)
	{
		return this.texturePath;
	}
	
	public void setArmorTexturePath(String modId, String type, EntityEquipmentSlot slot)
	{
		switch(slot) {
		case FEET:  // FEET 1.9
			this.texturePath = modId + ":textures/models/armor/" + type + "_layer_1.png";
			break;
		case LEGS:  // LEGS 1.9
			this.texturePath = modId + ":textures/models/armor/" + type + "_layer_2.png";
			break;
		case CHEST:  // CHEST 1.9
			this.texturePath = modId + ":textures/models/armor/" + type + "_layer_1.png";
			break;
		case HEAD:  // HEAD 1.9
			this.texturePath = modId + ":textures/models/armor/" + type + "_layer_1.png";
			break;
		default:
			break;
		}
	} // end setArmorTexturePath
	
	/**
	 * Helper method to sift through player's armor and figure out which piece
	 * is helm, chest, legs, boots, etc.
	 * 
	 * @param player
	 */
	public static void getArmorSet(EntityPlayer player, ItemArmor[] ar)
	{
		for (int ii = 0; ii < ar.length; ii++)	{ 
			ar[ii] = null; 
		}
		Iterable<ItemStack> armorList = player.getArmorInventoryList();
		for (ItemStack stack : armorList)
		{
			if (stack == null) { continue; }
			if (! (stack.getItem() instanceof ItemArmor)) {continue; }
			ItemArmor piece = (ItemArmor) stack.getItem();
			ar[piece.armorType.getIndex()] = piece;
		} // end-for
	} // end getArmorSet()
	
	public void getArmorPieces(EntityPlayer player) 
	{
		SimpleArmor.getArmorSet(player, this.armor);
	} // end getArmorPieces()

	/**
	 * Is player wearing a full set of this armor type?
	 * @param player
	 * @return true if wearing a full set of this armor, false if not.
	 */
	public boolean isFullSet(EntityPlayer player)
	{
		this.getArmorPieces(player);
		for (ItemArmor ar0 : this.armor)
		{
			if (! (ar0 instanceof SimpleArmor)) return false;
			SimpleArmor ar = (SimpleArmor) ar0;
			if (ar.material != this.material) return false;
		}
		return true;
	} // end isFullSet()

    @Override
    public void setAdditionalProperties()
    {}
} // end class
