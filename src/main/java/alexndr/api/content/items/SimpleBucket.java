package alexndr.api.content.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import alexndr.api.config.types.ConfigItem;
import alexndr.api.helpers.game.TooltipHelper;
import alexndr.api.registry.ContentCategories;
import alexndr.api.registry.ContentRegistry;
import alexndr.api.registry.Plugin;

import com.google.common.collect.Lists;

/**
 * @author AleXndrTheGr8st
 */
public class SimpleBucket extends ItemBucket
{
	private Plugin plugin;
	private ContentCategories.Item category = ContentCategories.Item.OTHER;
	private ConfigItem entry;
	@SuppressWarnings("unused")
	private List<String> toolTipStrings = Lists.newArrayList();
	private Block liquid;
	private SimpleBucketType bucketType;
	
	/**
	 * Creates a new SimpleBucket.
	 * @param liquidBlock The liquid in the bucket
	 * @param type The SimpleBucketType of the bucket
	 */
	public SimpleBucket(Plugin plugin, Block liquidBlock, SimpleBucketType type) {
		super(liquidBlock);
		this.plugin = plugin;
		this.liquid = liquidBlock;
		this.bucketType = type;
	}
	
	@Override
	public SimpleBucket setUnlocalizedName(String itemName) {
		super.setUnlocalizedName(itemName);
		GameRegistry.registerItem(this, itemName);
		ContentRegistry.registerItem(this.plugin, this, itemName, this.category);
		return this;
	}
	
	/**
	 * Returns the ConfigItem used by this bucket.
	 * @return ConfigItem
	 */
	public ConfigItem getConfigEntry() {
		return this.entry;
	}
	
	/**
	 * Sets the ConfigItem to be used for this bucket.
	 * @param entry ConfigItem
	 * @return SimpleBucket
	 */
	public SimpleBucket setConfigEntry(ConfigItem entry) {
		this.entry = entry;
		this.setMaxStackSize(entry.getStackSize());
		this.setAdditionalProperties();
		return this;
	}
	
	/**
	 * Adds a tooltip to the bucket. Must be unlocalised, so needs to be present in a localization file.
	 * @param toolTip Name of the localisation entry for the tooltip, as a String. Normal format is modId.theitem.info
	 * @return SimpleBucket
	 */
	public SimpleBucket addToolTip(String toolTip) {
		TooltipHelper.addTooltipToItem(this, toolTip);
		return this;
	}
	
	public void setAdditionalProperties() {
		if(entry.getValueByName("CreativeTab") != null && entry.getValueByName("CreativeTab").isActive()) {
			this.setCreativeTab(entry.getCreativeTab());
		}
	}
	
	/**
	 * What happens when player tries to use bucket. Note that this has to be re-written everytime
	 * base method ItemBucket.onItemRightClick() changes. TODO Consider switching class to 
	 * extending UniversalBucket instead.
	 */
	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, 
													EntityPlayer playerIn, EnumHand hand) 
	{
        boolean flag = this.liquid == Blocks.air;
        RayTraceResult raytraceresult = this.getMovingObjectPositionFromPlayer(worldIn, playerIn, flag);
        ActionResult<ItemStack> ret = 
        	net.minecraftforge.event.ForgeEventFactory.onBucketUse(playerIn, worldIn, 
        														   itemStackIn, raytraceresult);
        if (ret != null) return ret;

        if (raytraceresult == null)
        {
            return new ActionResult<ItemStack>(EnumActionResult.PASS, itemStackIn);
        }
        else if (raytraceresult.typeOfHit != RayTraceResult.Type.BLOCK)
        {
            return new ActionResult<ItemStack>(EnumActionResult.PASS, itemStackIn);
        }
        else {
            BlockPos blockpos = raytraceresult.getBlockPos();

            if (!worldIn.isBlockModifiable(playerIn, blockpos))
            {
                return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemStackIn);
            }
            else if (flag)  // we haz an empty bucket that hit a block we can modify, try to fill it.
            {
            	// THIS player can't affect this block, fail.
                if (!playerIn.canPlayerEdit(blockpos.offset(raytraceresult.sideHit), raytraceresult.sideHit, itemStackIn))
                {
                    return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemStackIn);
                }
                else  // THIS player can affect the block; try to fill the damn bucket.
                {
                    IBlockState iblockstate = worldIn.getBlockState(blockpos);
                    Material material = iblockstate.getMaterial();

                    // is this even a liquid and is there any of it?
                    if (material.isLiquid() && ((Integer)iblockstate.getValue(BlockLiquid.LEVEL)).intValue() == 0)
                    {
                      	Block liquid = iblockstate.getBlock();
                      	
                      	// does my bucket know how to fill with this liquid?
                      	// but not lava. Lava gets special handling.
                    	if(this.bucketType.getLiquidsList().contains(liquid) 
                    		&& this.bucketType.doesVariantExist(liquid)
                    		&& material != Material.lava) 
                    	{
                    		worldIn.setBlockState(blockpos, Blocks.air.getDefaultState(), 11);
							playerIn.addStat(StatList.func_188057_b(this));
							playerIn.playSound(SoundEvents.item_bucket_fill,
									1.0F, 1.0F);
							return new ActionResult<ItemStack>(
									EnumActionResult.SUCCESS, 
									this.fillBucket(itemStackIn, playerIn,
											this.bucketType.getBucketFromLiquid(liquid)));
                    	} // end if bucket has liquid it can contain
                    	else if (material == Material.lava)
                    	{
                    		 // are we an idiot who dipped a meltable/burnable bucket into lava?
                    		 if (this.bucketType.getDestroyOnLava()) 
                    		 {
                    			 // We're a creative mode idiot, so no harm, no foul
                    			if(playerIn.capabilities.isCreativeMode)
                    			{
        							return new ActionResult<ItemStack>(
        									EnumActionResult.SUCCESS, 
        									this.fillBucket(itemStackIn, playerIn, null));
                    			}
                    			// we're just an idiot, destroy the bucket.
                    			else {
                    				--itemStackIn.stackSize;
                    				playerIn.playSound(SoundEvents.block_lava_extinguish, 
                    									0.5F, 2.6F + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.8F);
                    			}
                    		 } // end if destroyed
                    		 // Okay, it doesn't melt, but does it handle lava?
                    		 else if (this.bucketType.getLiquidsList().contains(liquid) 
                    				  && this.bucketType.doesVariantExist(liquid))
                    		 {
                                 playerIn.playSound(SoundEvents.item_bucket_fill_lava, 1.0F, 1.0F);
                                 worldIn.setBlockState(blockpos, Blocks.air.getDefaultState(), 11);
                                 playerIn.addStat(StatList.func_188057_b(this));
                                 return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, 
                                 						this.fillBucket(itemStackIn, playerIn, 
                                 										this.bucketType.getBucketFromLiquid(liquid)));
                    		 }
                    		 // no, you don't get a filled bucket!
                             return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemStackIn);
                    	} // end-else-if lava
                    } // end-if some kind of liquid
                    else
                    {
                        return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemStackIn);
                    } // end else not a liquid
                } // end else player-editable
            } // end-if flag
            else  // we haz a full bucket we tried to pour somewhere
            {
            	boolean flag1 = worldIn.getBlockState(blockpos).getBlock().isReplaceable(worldIn, blockpos);
            	BlockPos blockpos1 = flag1 && raytraceresult.sideHit == EnumFacing.UP ? blockpos : blockpos.offset(raytraceresult.sideHit);

            	if (!playerIn.canPlayerEdit(blockpos1, raytraceresult.sideHit, itemStackIn))
            	{
            		return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemStackIn);
            	}
            	else if (this.tryPlaceContainedLiquid(playerIn, worldIn, blockpos1)
            			 && this.bucketType.doesVariantExist(Blocks.air))
            	{
            		playerIn.addStat(StatList.func_188057_b(this));
            		return !playerIn.capabilities.isCreativeMode 
            				? new ActionResult<ItemStack>(EnumActionResult.SUCCESS, 
            									new ItemStack(this.bucketType.getBucketFromLiquid(Blocks.air))) 
            				: new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
            	}
            	else
            	{
            		return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemStackIn);
            	}
            } // end-else ! flag
        } // end-else raytraceresult is something useful
        
        // we probably shouldn't get here, but let's return something safe anyway.
        return new ActionResult<ItemStack>(EnumActionResult.PASS, itemStackIn);

    } // end onItemRightClick()

    protected ItemStack fillBucket(ItemStack emptyBuckets, EntityPlayer player, Item fullBucket)
    {
        if (player.capabilities.isCreativeMode)
        {
            return emptyBuckets;
        }
        else if (--emptyBuckets.stackSize <= 0)
        {
            return new ItemStack(fullBucket);
        }
        else
        {
            if (!player.inventory.addItemStackToInventory(new ItemStack(fullBucket)))
            {
                player.dropPlayerItemWithRandomChoice(new ItemStack(fullBucket), false);
            }

            return emptyBuckets;
        }
    }
    
//    protected ItemStack giveNewBucket(ItemStack itemstack, EntityPlayer player, Item bucket) 
//    {
//    	if(player.capabilities.isCreativeMode) 
//    		return itemstack;
//    	
//    	else if(--itemstack.stackSize <= 0) 
//    		return new ItemStack(bucket);
//    	
//    	else {
//    		if(!player.inventory.addItemStackToInventory(new ItemStack(bucket)))
//    			player.dropPlayerItemWithRandomChoice(new ItemStack(bucket, 1, 0), false);
//    		return itemstack;
//    	}
//    }
} // end class
