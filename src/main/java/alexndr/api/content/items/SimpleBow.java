package alexndr.api.content.items;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import alexndr.api.config.types.ConfigBow;
import alexndr.api.core.SimpleCoreAPI;
import alexndr.api.helpers.game.TooltipHelper;
import alexndr.api.registry.Plugin;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

/**
 * @author AleXndrTheGr8st
 */
@SuppressWarnings("deprecation")
public class SimpleBow extends ItemBow
{
	protected String name;
	protected Plugin plugin;
//	protected ContentCategories.Item category = ContentCategories.Item.WEAPON;
	protected ConfigBow entry;
	protected List<String> toolTipStrings = Lists.newArrayList();
	protected HashMap<SimpleBowEffects, Object> effects = new HashMap<SimpleBowEffects, Object>();
	protected ItemStack repairMaterial;
	protected float zoomAmount = 0.165F;
	
	/**
	 * Creates a simple bow, such as the Mythril Bow.
	 * @param plugin The plugin the bow belongs to
	 * @param maxUses The max uses the bow has
	 */
	public SimpleBow(String bowName, Plugin plugin, int maxUses) {
		super();
		this.name = bowName;
		this.plugin = plugin;
		this.setMaxDamage(maxUses);
		this.bFull3D = true;
		setUnlocalizedName(bowName);
        setRegistryName(plugin.getModId(), bowName);
	}
	
	public void registerItemModel() 
	{
		String bowName = this.name;
		
		// the stock renderer includes the basic inventory item.
		SimpleCoreAPI.proxy.registerItemRenderer(plugin, this, 0, bowName);
		
		// now register the variants for the various degrees of draw.
		bowName = name + "_pulling_0";
		ModelBakery.registerItemVariants(this, 
					new ModelResourceLocation(new ResourceLocation(plugin.getModId(), bowName), 
											  "inventory"));
		bowName = name + "_pulling_1";
		ModelBakery.registerItemVariants(this, 
				new ModelResourceLocation(new ResourceLocation(plugin.getModId(), bowName), 
										  "inventory"));
		bowName = name + "_pulling_2";
		ModelBakery.registerItemVariants(this, 
				new ModelResourceLocation(new ResourceLocation(plugin.getModId(), bowName), 
										  "inventory"));
	} // end registerItemModel()


	/**
	 * Returns the ConfigEntry used by this tool.
	 * @return ConfigEntry
	 */
	public ConfigBow getConfigEntry() {
		return this.entry;
	}
	
	/**
	 * Sets the ConfigEntry to be used for this tool.
	 * @param entry ConfigEntry
	 * @return SimpleBow
	 */
	public SimpleBow setConfigEntry(ConfigBow entry) {
		this.entry = entry;
		this.setAdditionalProperties();
		return this;
	}
	
	/**
	 * Adds a tooltip to the bow. Must be unlocalised, so needs to be present in a localization file.
	 * @param toolTip Name of the localisation entry for the tooltip, as a String. Normal format is modId.theitem.info
	 * @return SimpleBow
	 */
	public SimpleBow addToolTip(String toolTip) {
		TooltipHelper.addTooltipToItem(this, toolTip);
		return this;
	}
	
	/**
	 * Adds a tooltip to the bow. Must be unlocalised, so needs to be present in a localization file.
	 * @param toolTip Name of the localisation entry for the tooltip, as a String. Normal format is modId.theitem.info
	 * @return SimpleBow
	 */
	public SimpleBow addToolTip(String toolTip, TextFormatting color) {
		TooltipHelper.addTooltipToItem(this, color + I18n.translateToLocal(toolTip));
		return this;
	}
	
	/**
	 * Sets the repair material that is used to repair the item in an anvil. 
	 * @param repairMaterial The ItemStack of the material that can repair the item.
	 * @return SimpleBow
	 */
	public SimpleBow setRepairMaterial(ItemStack repairMaterial)
	{
		this.repairMaterial = repairMaterial;
		return this;
	}
	
	/**
	 * Sets an effect on the bow. In this case, sets effects that do not require a modifier.
	 * For all the effects, see SimpleBowEffects, where effects are detailed, and their required modifiers shown.
	 * @param effect The effect to be put on the bow.
	 * @return SimpleBow
	 */
	public SimpleBow setEffect(SimpleBowEffects effect)
	{
		this.effects.put(effect, null);
		return this;
	}
	
	/**
	 * Sets an effect on the bow. In this case, sets effects that require a modifier that can be a non-integer.
	 * For all effects, see SimpleBowEffects, where effects are detailed and their required modifiers shown.
	 * @param effect The effect to be put on the bow.
	 * @param modifier The float modifier of the effect. Check SimpleBowEffects.
	 * @return SimpleBow
	 */
	public SimpleBow setEffect(SimpleBowEffects effect, float modifier)
	{
		this.effects.put(effect, modifier);
		return this;
	}

	/**
	 * Sets an effect on the bow. In this case, sets effects that require a modifier that can be an integer.
	 * For all effects, see SimpleBowEffects, where effects are detailed and their required modifiers shown.
	 * @param effect The effect to be put on the bow.
	 * @param modifier The int modifier of the effect. Check SimpleBowEffects.
	 * @return SimpleBow
	 */
	public SimpleBow setEffect(SimpleBowEffects effect, int modifier)
	{
		this.effects.put(effect, modifier);
		return this;
	}
	
	/**
	 * Returns the zoom amount of the bow. This is how far in the bow will zoom.
	 * Default is 0.165F.
	 * @return Zoom amount
	 */
	public float getZoomAmount() {
		return this.zoomAmount;
	}
	
	/**
	 * Sets the zoom amount of the bow. If not set, defaults to 0.165F.
	 * @param zoomAmount Float amount that the bow should zoom in to. Default = 0.165F.
	 * @return SimpleBow
	 */
	public SimpleBow setZoomAmount(float zoomAmount)
	{
		this.zoomAmount = zoomAmount;
		return this;
	}
	
	@Override
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
		return this.repairMaterial.getItem() == repair.getItem() ? true : super.getIsRepairable(toRepair, repair);
	}
	
	/**
	 * What happens when player uses this item to right-click?  overrides ItemBow because the Forge
	 * implementation is bugged.
	 */
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, 
													EnumHand handIn) 
	{
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        boolean hasAmmo = !this.findAmmo(playerIn).isEmpty()
        		|| EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, itemstack) > 0
				|| (this.effects.containsKey(SimpleBowEffects.infiniteArrows));

        ActionResult<ItemStack> ret = 
        		net.minecraftforge.event.ForgeEventFactory.onArrowNock(itemstack, worldIn, playerIn, 
        															   handIn, hasAmmo);
        if (ret != null) return ret;

        if (!playerIn.capabilities.isCreativeMode && !hasAmmo)
        {
            return hasAmmo ? new ActionResult<ItemStack>(EnumActionResult.PASS, itemstack) : 
            			  new ActionResult<ItemStack>(EnumActionResult.FAIL, itemstack);
        }
        else
        {
            playerIn.setActiveHand(handIn);
            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
        }
	} // end onItemRightClick()

	/**
     * Called when the player stops using an Item (stops holding the right mouse 
     * button). This override will have to be re-written everytime the base
     * ItemBow.onPlayerStoppedUsing() gets re-written.
     */
	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft)
	{
		if (entityLiving instanceof EntityPlayer) 
		{
			EntityPlayer entityplayer = (EntityPlayer)entityLiving;
			boolean efficient = false;
            boolean flag = entityplayer.capabilities.isCreativeMode 
            				|| EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0
            				|| (this.effects.containsKey(SimpleBowEffects.infiniteArrows));
            ItemStack itemstack = this.findAmmo(entityplayer);

            int i = this.getMaxItemUseDuration(stack) - timeLeft;
			i = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(stack, worldIn, 
											entityplayer, i, !itemstack.isEmpty() || flag);
			
            if (i < 0) return;

            if (!itemstack.isEmpty() || flag)
            {
                if (itemstack.isEmpty())
                {
                    itemstack = new ItemStack(Items.ARROW);
                }
                // MCP note: getArrowVelocity(i)
                float f = ItemBow.getArrowVelocity(i);

                if ((double)f >= 0.1D)
                {
                    boolean flag1 = flag && itemstack.getItem() instanceof ItemArrow; //Forge: Fix consuming custom arrows.

                    if (!worldIn.isRemote)
                    {
                        ItemArrow itemarrow = (ItemArrow)((ItemArrow)(itemstack.getItem() instanceof ItemArrow ? itemstack.getItem() : Items.ARROW));
                        EntityArrow entityarrow = itemarrow.createArrow(worldIn, itemstack, entityplayer);
                        entityarrow.shoot(entityplayer, entityplayer.rotationPitch, entityplayer.rotationYaw, 
                        					0.0F, f * 3.0F, 1.0F);

                        if (f == 1.0F)
                        {
                            entityarrow.setIsCritical(true);
                            if (this.effects.containsKey(SimpleBowEffects.critFlameEffect))
                            {
                            	entityarrow.setFire(100);
                            }
                        }

                        int j = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);
                        if (j > 0)
                        {
                            entityarrow.setDamage(entityarrow.getDamage() + (double)j * 0.5D + 0.5D);
                        }

                        int k = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack);
                        if (k > 0)
                        {
                            entityarrow.setKnockbackStrength(k);
                        }

                        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack) > 0
                        	|| effects.containsKey(SimpleBowEffects.flameEffect))
                        {
                            entityarrow.setFire(100);
                        }
        				if (effects.containsKey(SimpleBowEffects.damageEffect))
        				{
        					entityarrow.setDamage(entityarrow.getDamage()
        							* (Float) effects.get(SimpleBowEffects.damageEffect));
        				}
        				if (effects.containsKey(SimpleBowEffects.knockbackEffect)) 
        				{
        					entityarrow.setKnockbackStrength(k > 0 ? k
        							+ (Integer) effects.get(SimpleBowEffects.knockbackEffect)
        							: (Integer) effects.get(SimpleBowEffects.knockbackEffect));
        				}
        				if (effects.containsKey(SimpleBowEffects.efficiencyEffect))
        				{
        					efficient = randomChance((Integer) 
        								effects.get(SimpleBowEffects.efficiencyEffect));
        				}
                        stack.damageItem(1, entityplayer);

                        if (flag1 || efficient)
                        {
                            entityarrow.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
                        }

                        worldIn.spawnEntity(entityarrow);
                    }

                    worldIn.playSound((EntityPlayer)null, entityplayer.posX, 
                    		entityplayer.posY, entityplayer.posZ, 
                    		SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 
                    		1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);

                    if (!flag1 && !efficient)
                    {
                    	itemstack.shrink(1);
                        if (itemstack.isEmpty())
                        {
                            entityplayer.inventory.deleteStack(itemstack);
                        }
                    }

                    entityplayer.addStat(StatList.getObjectUseStats(this));
                }
            } // end-if
		} // end-if
	} // end OnPlayerStoppedUsing()

	/**
	 * find the right stack of ammo to use.
	 * @param player
	 * @return ammo items as ItemStack
	 */
    protected ItemStack findAmmo(EntityPlayer player)
    {
        if (this.isArrow(player.getHeldItem(EnumHand.OFF_HAND)))
        {
            return player.getHeldItem(EnumHand.OFF_HAND);
        }
        else if (this.isArrow(player.getHeldItem(EnumHand.MAIN_HAND)))
        {
            return player.getHeldItem(EnumHand.MAIN_HAND);
        }
        else
        {
            for (int i = 0; i < player.inventory.getSizeInventory(); ++i)
            {
                ItemStack itemstack = player.inventory.getStackInSlot(i);

                if (this.isArrow(itemstack))
                {
                    return itemstack;
                }
            }

            return ItemStack.EMPTY;
        }
    } // end ()

	public boolean randomChance(int chance)
	{
		Random random = new Random();
		int rand = random.nextInt(100);
		
		if(rand <= chance)
			return true;
		else
			return false;
	}
	
    
	public void setAdditionalProperties() {
	}
} // end class
