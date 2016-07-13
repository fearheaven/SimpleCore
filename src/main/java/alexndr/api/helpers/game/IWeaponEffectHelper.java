package alexndr.api.helpers.game;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

/**
 * interface for helper classes for weapons with special effects on hitting
 * or using.
 * @author Sinhika
 *
 */
public interface IWeaponEffectHelper 
{
	public abstract boolean hitEntity(ItemStack stack, EntityLivingBase target,
			EntityLivingBase attacker); 

}
