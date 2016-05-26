package alexndr.api.content.items;

import java.util.List;

import javax.annotation.Nullable;

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
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
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
 * TODO rewrite this class to use Forge's UniversalBucket class -- cch
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
	 * Creates a new SimpleBucket (ItemBucket).
	 * @param liquidBlock The liquid in the bucket
	 * @param type The SimpleBucketType of the bucket
	 */
	public SimpleBucket(Plugin plugin, Block liquidBlock, SimpleBucketType type) {
		super(liquidBlock);
		this.plugin = plugin;
		this.liquid = liquidBlock;
		this.bucketType = type;
	}
	
	/*
	 * Creates a new SimpleBucket (UniversalBucket).
	 * @param plugin which plugin created this.
	 */
//	public SimpleBucket(Plugin plugin) 
//	{
//		super();
//		this.plugin = plugin;
//	}
	
	/*
	 * Creates a new SimpleBucket (UniversalBucket).
	 * @param plugin 		  which plugin created this.
     * @param capacity        Capacity of the container
     * @param empty           Item used for filling with the bucket event and returned when emptied
     * @param nbtSensitive    Whether the empty item is NBT sensitive (usually true if empty and full are the same items)
	 */
//    public SimpleBucket(Plugin plugin, int capacity, ItemStack empty, boolean nbtSensitive)
//    {
//    	super(capacity, empty, nbtSensitive);
//		this.plugin = plugin;
//    }
    
	@Override
	public SimpleBucket setUnlocalizedName(String itemName) {
		super.setUnlocalizedName(itemName);
        setRegistryName(this.plugin.getModId(), itemName);
        GameRegistry.register(this);
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
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, 
													EntityPlayer playerIn, EnumHand hand) 
	{
		boolean flag = this.liquid == Blocks.AIR;
        RayTraceResult raytraceresult = this.rayTrace(worldIn, playerIn, flag);
        ActionResult<ItemStack> ret = net.minecraftforge.event.ForgeEventFactory.onBucketUse(playerIn, worldIn, itemStackIn, raytraceresult);
        if (ret != null) return ret;

        if (raytraceresult == null)
        {
            return new ActionResult(EnumActionResult.PASS, itemStackIn);
        }
        else if (raytraceresult.typeOfHit != RayTraceResult.Type.BLOCK)
        {
            return new ActionResult(EnumActionResult.PASS, itemStackIn);
        }
        else
        {
            BlockPos blockpos = raytraceresult.getBlockPos();

            if (!worldIn.isBlockModifiable(playerIn, blockpos))
            {
                return new ActionResult(EnumActionResult.FAIL, itemStackIn);
            }
        	// else if we haz an empty bucket that hit a block we can modify, try to fill it.
            else if (flag) 
            {
            	// THIS player can't affect this block, fail.
                if (!playerIn.canPlayerEdit(blockpos.offset(raytraceresult.sideHit), 
                							 raytraceresult.sideHit, itemStackIn))
                {
                    return new ActionResult(EnumActionResult.FAIL, itemStackIn);
                }
                else // THIS player can affect the block; try to fill the damn bucket.
                {
                    IBlockState iblockstate = worldIn.getBlockState(blockpos);
                    Material material = iblockstate.getMaterial();
                    
                    if (material.isLiquid() 
                    	&& ((Integer)iblockstate.getValue(BlockLiquid.LEVEL)).intValue() 
                    				== 0) 
                    {
                        liquid = iblockstate.getBlock();
                    }
                    else // not liquid, why are we here?
                    {
                        return new ActionResult(EnumActionResult.FAIL, itemStackIn);
                    }
                    
                    // all buckets can handle water.
                    if (material == Material.WATER )
                    {
                        worldIn.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 11);
                        playerIn.addStat(StatList.getObjectUseStats(this));
                        playerIn.playSound(SoundEvents.ITEM_BUCKET_FILL, 1.0F, 1.0F);
                        return new ActionResult(EnumActionResult.SUCCESS, 
                        		this.fillBucket(itemStackIn, playerIn, 
                        				this.bucketType.getBucketFromLiquid(liquid)));
                    }
                    // some buckets can handle lava. Some handle it badly.
                    else if (material == Material.LAVA)
                    {
						// are we an idiot who dipped a meltable/burnable bucket
						// into lava?
						if (this.bucketType.getDestroyOnLava()) 
						{
							// We're a creative mode idiot, so no harm, no foul
							if (playerIn.capabilities.isCreativeMode) 
							{
								return new ActionResult<ItemStack>(
										EnumActionResult.SUCCESS,
										this.fillBucket(itemStackIn, playerIn,
												null));
							}
							// we're just an idiot, destroy the bucket.
							else {
								--itemStackIn.stackSize;
								playerIn.playSound(
										SoundEvents.BLOCK_LAVA_EXTINGUISH,
										0.5F,
										2.6F + (worldIn.rand.nextFloat() - worldIn.rand
												.nextFloat()) * 0.8F);
							}
						} // end if destroyed
						// Okay, it doesn't melt, but does it handle lava?
						else if (this.bucketType.getLiquidsList().contains(liquid) 
              				  	  && this.bucketType.doesVariantExist(liquid))
						{
							playerIn.playSound(
									SoundEvents.ITEM_BUCKET_FILL_LAVA, 1.0F,
									1.0F);
							worldIn.setBlockState(blockpos,
									Blocks.AIR.getDefaultState(), 11);
							playerIn.addStat(StatList.getObjectUseStats(this));
							return new ActionResult(EnumActionResult.SUCCESS,
									this.fillBucket(itemStackIn, playerIn,
											this.bucketType.getBucketFromLiquid(liquid)));
						} // end else lava fill
	                    else // no, you don't get a filled bucket.
	                    {
	                        return new ActionResult(EnumActionResult.FAIL, itemStackIn);
	                    }
                    } // end lava handler
                    // is it some other liquid we know how to handle?
                    else if(this.bucketType.getLiquidsList().contains(liquid) 
                		&& this.bucketType.doesVariantExist(liquid)) 
                    {
                        worldIn.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 11);
                        playerIn.addStat(StatList.getObjectUseStats(this));
                        playerIn.playSound(SoundEvents.ITEM_BUCKET_FILL, 1.0F, 1.0F);
                        return new ActionResult(EnumActionResult.SUCCESS, 
                        		this.fillBucket(itemStackIn, playerIn, 
                        				this.bucketType.getBucketFromLiquid(liquid)));
                    }
                    else
                    {
                        return new ActionResult(EnumActionResult.FAIL, itemStackIn);
                    }
                } // end-else can affect block
            } // end-if flag
            else
            {
                boolean flag1 = worldIn.getBlockState(blockpos).getBlock().isReplaceable(worldIn, blockpos);
                BlockPos blockpos1 = flag1 && raytraceresult.sideHit == EnumFacing.UP ? blockpos : blockpos.offset(raytraceresult.sideHit);

                if (!playerIn.canPlayerEdit(blockpos1, raytraceresult.sideHit, itemStackIn))
                {
                    return new ActionResult(EnumActionResult.FAIL, itemStackIn);
                }
                else if (this.tryPlaceContainedLiquid(playerIn, worldIn, blockpos1))
                {
                    playerIn.addStat(StatList.getObjectUseStats(this));
                    return !playerIn.capabilities.isCreativeMode 
                    		? new ActionResult(EnumActionResult.SUCCESS, 
                    				new ItemStack(this.bucketType.getBucketFromLiquid(Blocks.AIR))) 
                    		: new ActionResult(EnumActionResult.SUCCESS, itemStackIn);
                }
                else
                {
                    return new ActionResult(EnumActionResult.FAIL, itemStackIn);
                }
            } // end else ! flag
        }  // end else     

      // we probably shouldn't get here, but let's return something safe anyway.
      return new ActionResult<ItemStack>(EnumActionResult.PASS, itemStackIn);

    } // end onItemRightClick()

	/**
	 * cut & paste of ItemBucket.fillBucket(), which is private so we could not
	 * inherit it.
	 * 
	 * @param emptyBuckets
	 * @param player
	 * @param fullBucket
	 * @return
	 */
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
                player.dropItem(new ItemStack(fullBucket), false);
            }

            return emptyBuckets;
        }
    } // end fillBucket()
    
    /**
     * cut & paste of ItemBucket.tryPlaceContainedLiquid(), because isFull is private,
     * and we replace it with this.liquid.
     */
    @Override
    public boolean tryPlaceContainedLiquid(@Nullable EntityPlayer worldIn, World pos, BlockPos posIn)
    {
        if (this.liquid == Blocks.AIR)
        {
            return false;
        }
        else
        {
            IBlockState iblockstate = pos.getBlockState(posIn);
            Material material = iblockstate.getMaterial();
            boolean flag = !material.isSolid();
            boolean flag1 = iblockstate.getBlock().isReplaceable(pos, posIn);

            if (!pos.isAirBlock(posIn) && !flag && !flag1)
            {
                return false;
            }
            else
            {
                if (pos.provider.doesWaterVaporize() && this.liquid == Blocks.FLOWING_WATER)
                {
                    int l = posIn.getX();
                    int i = posIn.getY();
                    int j = posIn.getZ();
                    pos.playSound(worldIn, posIn, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (pos.rand.nextFloat() - pos.rand.nextFloat()) * 0.8F);

                    for (int k = 0; k < 8; ++k)
                    {
                        pos.spawnParticle(EnumParticleTypes.SMOKE_LARGE, (double)l + Math.random(), (double)i + Math.random(), (double)j + Math.random(), 0.0D, 0.0D, 0.0D, new int[0]);
                    }
                }
                else
                {
                    if (!pos.isRemote && (flag || flag1) && !material.isLiquid())
                    {
                        pos.destroyBlock(posIn, true);
                    }

                    SoundEvent soundevent = this.liquid == Blocks.FLOWING_LAVA ? SoundEvents.ITEM_BUCKET_EMPTY_LAVA : SoundEvents.ITEM_BUCKET_EMPTY;
                    pos.playSound(worldIn, posIn, soundevent, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    pos.setBlockState(posIn, this.liquid.getDefaultState(), 11);
                }

                return true;
            }
        }
    } // end tryPlaceContainedLiquid()
} // end class
