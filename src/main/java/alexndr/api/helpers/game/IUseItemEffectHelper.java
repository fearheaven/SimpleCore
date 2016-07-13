package alexndr.api.helpers.game;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** 
 * Handles interesting right-click things that need to happen.
 * @author Sinhika
 *
 */
public interface IUseItemEffectHelper 
{
	/**
	 * what should item do when right-clicked on something?
	 * @param stack
	 * @param playerIn
	 * @param worldIn
	 * @param pos
	 * @param hand
	 * @param facing
	 * @param hitX
	 * @param hitY
	 * @param hitZ
	 * @return EnumActionResult.PASS because we're not done.
	 */
	public abstract EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn,
			World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing,
			float hitX, float hitY, float hitZ);

} // end interface
