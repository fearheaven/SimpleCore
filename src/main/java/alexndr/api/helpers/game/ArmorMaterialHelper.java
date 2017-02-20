package alexndr.api.helpers.game;

import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;

/** 
 * Deal with custom repair material being handled completely differently between 1.10.2 and 1.11
 * @author Sinhika
 *
 */
public class ArmorMaterialHelper
{
    public static ArmorMaterial setRepairItem(ArmorMaterial material, ItemStack stack)
    {
        // BEGIN 1.10.2 version
//        if (material.customCraftingMaterial != null) {
//            throw new RuntimeException("Can not change already set repair material");
//        }
//        if (material == ArmorMaterial.GOLD || material == ArmorMaterial.IRON 
//            || material == ArmorMaterial.DIAMOND || material == ArmorMaterial.DIAMOND
//            || material == ArmorMaterial.LEATHER) 
//        {
//            throw new RuntimeException("Can not change vanilla tool repair materials");
//        }
//        material.customCraftingMaterial = stack.getItem();
//        return material;
        // END 1.10.2 version
        // BEGIN 1.11.2 version
         return material.setRepairItem(stack);
        // END 1.11.2 version
    } // end setRepairItem()
} // end class
