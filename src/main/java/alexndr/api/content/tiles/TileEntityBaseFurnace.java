/*
 * This file ("TileEntityBaseFurnace.java") is part of the Minecraft mod project project_name. 
 * It is created and owned by Sinhika and Skrallexxx and distributed under the Lesser GNU Public 
 * License v3.0, a copy of which is included in the root of this source tree ("lgpl.txt") 
 * or otherwise readable at  <http://fsf.org/>.
 * 
 * Parts of this file are also based on Actually Additions source code by Ellpeck. See
 * <https://minecraft.curseforge.com/projects/actually-additions> for details of that mod.
 * 
 * (c) 2014-2017 Sinhika and Skrallexxx (aka AleXndrTheGr8st)
 */
package alexndr.api.content.tiles;

import alexndr.api.content.blocks.SimpleFurnace;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;

/**
 * Base tile entity for Simple Ores furnaces. Differs from TileEntityFurnace in using
 * capability-based inventory.
 * @author Sinhika
 */
public abstract class TileEntityBaseFurnace extends TileEntityBaseInventory implements IInteractionObject 
{
    public static final int NDX_INPUT_SLOT = 0;
    public static final int NDX_FUEL_SLOT = 1;
    public static final int NDX_OUTPUT_SLOT = 2;
 
    /** The number of ticks that the furnace will keep burning */
    protected int furnaceBurnTime;
    
    /** The number of ticks that a fresh copy of the currently-burning item would keep the furnace burning for */
    protected int currentItemBurnTime;
    
    /** number of ticks we've cooked so far. */
    protected int cookTime;
    
    /** number of ticks it takes to cook this item. */
    protected int totalCookTime;		
    
    /** what is this for? */
    protected int maxCookTime;

    protected String furnaceCustomName;
    protected String furnaceName;
    protected String furnaceGuiId;
    
    public TileEntityBaseFurnace(String tileName, int max_cook_time,
			   				 String guiID, int furnace_stack_count) 
	{
		super(furnace_stack_count);
		this.furnaceName = tileName;
		this.maxCookTime = max_cook_time;
		this.furnaceGuiId = guiID;
		this.cookTime = 0;
	} // end ctor

	/**
     * Furnace isBurning
     */
    public boolean isBurning()
    {
        return this.furnaceBurnTime > 0;
    }
	
    /** override this as necessary in custom furnace classes. */
    public int getCookTime(ItemStack stack)
    {
        return maxCookTime;
    }

    public void setCustomInventoryName(String customName)
    {
        this.furnaceCustomName = customName;
    }

    /** If we are too far away from this tile entity you cannot use it */
    public boolean canInteractWith(EntityPlayer playerIn) 
    {
        return !isInvalid() && playerIn.getDistanceSq(pos.add(0.5D, 0.5D, 0.5D)) <= 64D;
    }

    /**
     * Returns true if the furnace can smelt an item, i.e. has a source item, destination stack isn't full, 
     * etc. Obviously will have to be overriden for special multi-slot furnaces like the Fusion Furnace.
     */
    protected boolean canSmelt()
    {
        if (slotHandler.getStackInSlot(NDX_INPUT_SLOT).isEmpty())
        {
            return false;
        }
        else
        {
            ItemStack hypothetical_result = 
                 FurnaceRecipes.instance().getSmeltingResult(
                		 					slotHandler.getStackInSlot(NDX_INPUT_SLOT));
            if (hypothetical_result.isEmpty()) 
            {
            	return false;
            }
            else {
            	ItemStack test_overflow = 
            			slotHandler.insertItem(NDX_OUTPUT_SLOT, hypothetical_result, true);
            	return test_overflow.isEmpty();
            }
        } // end-else
    } // end canSmelt()

	@Override
	public void readSyncableNBT(NBTTagCompound compound, NBTType type) 
	{
		super.readSyncableNBT(compound, type);
		if (type != NBTType.SAVE_BLOCK)
		{
			this.furnaceBurnTime = compound.getInteger("BurnTime");
			this.cookTime = compound.getInteger("CookTime");
			this.totalCookTime = compound.getInteger("CookTimeTotal");
			this.currentItemBurnTime = getItemBurnTime(slotHandler.getStackInSlot(NDX_FUEL_SLOT));
		}
        if (compound.hasKey("CustomName", 8))
        {
            this.furnaceCustomName = compound.getString("CustomName");
        }
	} // end readFromNBT()


	@Override
	public void writeSyncableNBT(NBTTagCompound compound, NBTType type) 
	{
		super.writeSyncableNBT(compound, type);
	    if (type != NBTType.SAVE_BLOCK)
	    {
	    	compound.setInteger("BurnTime", (short)this.furnaceBurnTime);
	    	compound.setInteger("CookTime", (short)this.cookTime);
	    	compound.setInteger("CookTimeTotal", (short)this.totalCookTime);
	    }
    	if (this.hasCustomName())
    	{
    		compound.setString("CustomName", this.furnaceCustomName);
    	}
	} // end writeSyncableNBT()

	@Override
	public void update() 
	{
        boolean flag = this.isBurning();
        boolean flag1 = false;
        int burnTime = 0;
        
        if (this.isBurning())
        {
            --this.furnaceBurnTime;
        }

        if (!this.getWorld().isRemote)
        {
            ItemStack itemstack = slotHandler.getStackInSlot(NDX_FUEL_SLOT);
            if (!itemstack.isEmpty()) 
			{
                burnTime = TileEntityBaseFurnace.getItemBurnTime(itemstack);
            }
            flag1 = default_cooking_update(flag1, itemstack, burnTime);
            if (flag != this.isBurning())
            {
                flag1 = true;
                SimpleFurnace.setState(this.isBurning(), this.getWorld(), this.pos);
            } // end-if
        } // end-if

        if (flag1)
        {
            this.markDirty();
        }
	} // end update()

    @Override
	public String getName() {
	       return this.hasCustomName() ? this.furnaceCustomName : this.furnaceName;	
	}


	@Override
	public boolean hasCustomName() {
        return this.furnaceCustomName != null && this.furnaceCustomName.length() > 0;
	}


	@Override
	public ITextComponent getDisplayName() {
        return (ITextComponent)(this.hasCustomName() 
        		? new TextComponentString(this.getName()) 
        		: new TextComponentTranslation(this.getName(), new Object[0]));
	}

	@Override
	public abstract Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn); 


	@Override
	public String getGuiID() {
		return this.furnaceGuiId;
	}


	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) 
	{
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY 
				|| super.hasCapability(capability, facing);	
	} // end hasCapability()


	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) 
	{
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY 
				? CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(slotHandler) 
				: super.getCapability(capability, facing);	
	} // end getCapability()

    /**
     * Turn one item from the furnace source stack into the appropriate smelted item in the furnace result 
     * stack. Override for special multi-slot furnaces like the Fusion Furnace.
     */
    public void smeltItem()
    {
        if (this.canSmelt())
        {
         	ItemStack instack = slotHandler.extractItem(NDX_INPUT_SLOT, 1, false);
            ItemStack result_stack = FurnaceRecipes.instance().getSmeltingResult(instack);
            slotHandler.insertItem(NDX_OUTPUT_SLOT, result_stack, false);
            if (instack.getItem() == Item.getItemFromBlock(Blocks.SPONGE) 
            	&& instack.getMetadata() == 1 
            	&& !slotHandler.getStackInSlot(NDX_FUEL_SLOT).isEmpty() 
            	&& (slotHandler.getStackInSlot(NDX_FUEL_SLOT)).getItem() == Items.BUCKET)
            {
            	slotHandler.setStackInSlot(NDX_FUEL_SLOT, new ItemStack(Items.WATER_BUCKET));
            }
        } // end-if
    } // end smeltItem()

	/**
     * Returns the number of ticks that the supplied fuel item will keep the furnace burning, or 0 
     * if the item isn't fuel.
     */
    public static int getItemBurnTime(ItemStack burnItemStack)
    {
        if (burnItemStack.isEmpty())
        {
            return 0;
        }
        else
        {
            Item item = burnItemStack.getItem();

            if (item instanceof net.minecraft.item.ItemBlock 
            		&& ! Block.isEqualTo(Block.getBlockFromItem(item), Blocks.AIR))
            {
                Block block = Block.getBlockFromItem(item);

                if (Block.isEqualTo(block, Blocks.WOODEN_SLAB))
                {
                    return 150;
                }

                if (block.getDefaultState().getMaterial() == Material.WOOD)
                {
                    return 300;
                }

                if (Block.isEqualTo(block, Blocks.COAL_BLOCK))
                {
                    return 16000;
                }
            }

            if (item instanceof ItemTool && ((ItemTool)item).getToolMaterialName().equals("WOOD")) return 200;
            if (item instanceof ItemSword && ((ItemSword)item).getToolMaterialName().equals("WOOD")) return 200;
            if (item instanceof ItemHoe && ((ItemHoe)item).getMaterialName().equals("WOOD")) return 200;
            if (item == Items.STICK) return 100;
            if (item == Items.COAL) return 1600;
            if (item == Items.LAVA_BUCKET) return 20000;
            if (item == Item.getItemFromBlock(Blocks.SAPLING)) return 100;
            if (item == Items.BLAZE_ROD) return 2400;
            return ForgeEventFactory.getItemBurnTime(burnItemStack);
        }
    } // end getItemBurnTime()

    /**
     * default cooking update.
     * @param flag1
     * @param readonlyFuelStack
     * @param burnTime
     * @return
     */
	protected boolean default_cooking_update(boolean flag1, ItemStack readonlyFuelStack, int burnTime)
	{
        if (this.isBurning() || ( ! readonlyFuelStack.isEmpty() 
        						 && ! slotHandler.getStackInSlot(NDX_INPUT_SLOT).isEmpty()))
        {
            if (!this.isBurning() && this.canSmelt())
            {
                this.furnaceBurnTime = burnTime;
                this.currentItemBurnTime = this.furnaceBurnTime;
            	this.totalCookTime = this.getCookTime(slotHandler.getStackInSlot(NDX_INPUT_SLOT));
            	this.cookTime = 0;
            	
                if (this.isBurning())
                {
                    flag1 = true;

                    if (!readonlyFuelStack.isEmpty())
                    {
                    	// save this for later
                    	Item item = readonlyFuelStack.getItem();
                    	
                        // pull that fuel item off stack that we just burned. 
                    	slotHandler.extractItem(NDX_FUEL_SLOT, 1, false);
                    	
                    	// checking to see if remaining stuff in fuel stack was in a bucket...
                        if (readonlyFuelStack.isEmpty()) 
                        {
                            ItemStack item1 = item.getContainerItem(readonlyFuelStack);
                            if (!item1.isEmpty()) {
                            	slotHandler.setStackInSlot(NDX_FUEL_SLOT, item1);
                            }
                        } // end-if empty
                    } // end-if !empty
                } // end-if isBurning
            } // end-if !isBurning && canSmelt

            if (this.isBurning() && this.canSmelt())
            {
                ++this.cookTime;

                if (this.cookTime >= this.totalCookTime)
                {
                    this.cookTime = 0;
                    // this.totalCookTime = this.getCookTime(slotHandler.getStackInSlot(NDX_INPUT_SLOT));
                    this.smeltItem();
                    flag1 = true;
                }
            }
            else
            {
                this.cookTime = 0;
            }
        } // end-if isBurnning && valid FUEL && valid INPUT
        else if (!this.isBurning() && this.cookTime > 0)
        {
            this.cookTime = MathHelper.clamp(this.cookTime - 2, 0, this.totalCookTime);
        }
        return flag1;
	} // end default_cooking_update()

	
	@SideOnly(Side.CLIENT)
	public int getScaledCookProgress(int scaleFactor) 
	{
		return ((this.totalCookTime != 0 && this.cookTime != 0) 
				? this.cookTime * scaleFactor / this.totalCookTime 
				: 0);
	} // end getScaledCookProgress()
  
	@SideOnly(Side.CLIENT)
	public int getScaledBurnTime(int scaleFactor) 
	{
      int j = this.currentItemBurnTime;
      if ( j == 0) 
    	  j = 200;
      return this.furnaceBurnTime * scaleFactor / j;
	} // end getScaledBurnTime()

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, 
								IBlockState newState) 
	{
		return (oldState != newState && oldState.getBlock() != newState.getBlock());
	}


} // end class
