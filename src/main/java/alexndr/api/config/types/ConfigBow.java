package alexndr.api.config.types;

import alexndr.api.config.ConfigHelper;
import net.minecraftforge.common.config.Configuration;

public class ConfigBow extends ConfigEntry 
{
	private boolean hasDamageModifier = true;
	private float damageModifier = 1.5F;
	private boolean hasEfficiencyChance = false;
	private int efficiencyChance = 0;
	private boolean hasKnockback = false;
	private float knockBackFactor = 2.5F;
	private float zoomAmount = 0.0F;
	
	public ConfigBow(String name) 
	{
		super(name, ConfigHelper.CATEGORY_BOW, true);
	}

	@Override
	public void GetConfig(Configuration config) 
	{
		if (hasDamageModifier) {
			damageModifier = config.getFloat("DamageModifier", subcategory, damageModifier, 
											 0.0F, 32000.0F, "");
		}
		if (hasEfficiencyChance) {
			efficiencyChance = config.getInt("EfficiencyChance", subcategory, efficiencyChance, 
											 0, 100, "% chance arrow will not be consumed");
		}
		if (hasKnockback) {
			knockBackFactor = config.getFloat("KnockBackFactor", subcategory, knockBackFactor, 
											   0.0F, 255.0F, "knockback multiplier");
		}
		zoomAmount = config.getFloat("ZoomAmount", subcategory, zoomAmount, 0.0F, 1.0F, "");
	} // end GetConfig()

	public float getKnockBackFactor() {
		return knockBackFactor;
	}

	public ConfigBow setKnockBackFactor(float knockBackFactor) {
		this.knockBackFactor = knockBackFactor;
		return this;
	}

	public int getEfficiencyChance() {
		return efficiencyChance;
	}

	public ConfigBow setEfficiencyChance(int efficiencyChance) {
		this.efficiencyChance = efficiencyChance;
		return this;
	}

	public float getDamageModifier() {
		return damageModifier;
	}

	public ConfigBow setDamageModifier(float damageModifier) {
		this.damageModifier = damageModifier;
		return this;
	}

	public boolean HasEfficiencyChance() {
		return hasEfficiencyChance;
	}

	public ConfigBow setHasEfficiencyChance(boolean hasEfficiencyChance) {
		this.hasEfficiencyChance = hasEfficiencyChance;
		return this;
	}

	public float getZoomAmount() {
		return zoomAmount;
	}

	public ConfigBow setZoomAmount(float zoomAmount) {
		this.zoomAmount = zoomAmount;
		return this;
	}

	public boolean HasDamageModifier() {
		return hasDamageModifier;
	}

	public ConfigBow setHasDamageModifier(boolean hasDamageModifier) {
		this.hasDamageModifier = hasDamageModifier;
		return this;
	}

	public boolean HasKnockback() {
		return hasKnockback;
	}

	public ConfigBow setHasKnockback(boolean hasKnockback) {
		this.hasKnockback = hasKnockback;
		return this;
	}

} // end class
