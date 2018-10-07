package alexndr.api.content.inventory;

import alexndr.api.content.tiles.TileEntitySimpleFurnace;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnaceFuel;
import net.minecraft.inventory.SlotFurnaceOutput;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SimpleFurnaceContainer extends Container 
{
	protected TileEntitySimpleFurnace tileFurnace;
	protected int cookTime;
	protected int totalCookTime;
	protected int furnaceBurnTime;
	protected int currentItemBurnTime;
	
	public SimpleFurnaceContainer(InventoryPlayer player, TileEntitySimpleFurnace tileentity) 
	{
		this.tileFurnace = tileentity;
		AddOwnSlots(player);
		AddPlayerSlots(player);
	}

	protected void AddPlayerSlots(InventoryPlayer player) 
	{
		// main player inventory slots
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) 
				this.addSlotToContainer(new Slot(player, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
		}
		// player hotbar slots
		for (int i = 0; i < 9; ++i) 
			this.addSlotToContainer(new Slot(player, i, 8 + i * 18, 142));
	} // end AddPlayerSlots()

	protected void AddOwnSlots(InventoryPlayer player) 
	{
		this.addSlotToContainer(new Slot(tileFurnace, TileEntitySimpleFurnace.NDX_INPUT_SLOT, 56, 17));
		this.addSlotToContainer(new SlotFurnaceFuel(tileFurnace, TileEntitySimpleFurnace.NDX_FUEL_SLOT, 56, 53));
		this.addSlotToContainer(
				new SlotFurnaceOutput(player.player, tileFurnace, TileEntitySimpleFurnace.NDX_OUTPUT_SLOT, 116, 35));
	} // end AddOwnSlots()


	@Override
    public void addListener(IContainerListener listener)
    {
        super.addListener(listener);
        listener.sendAllWindowProperties(this, this.tileFurnace);
    }


	@Override
	public boolean canInteractWith(EntityPlayer playerIn) 
	{
        return this.tileFurnace.isUsableByPlayer(playerIn);
	}

   /**
     * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
     * inventory and the other inventory(s).
     */
	@Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            // output slot?
            if (index == TileEntitySimpleFurnace.NDX_OUTPUT_SLOT)
            {
                if (!this.mergeItemStack(itemstack1, 3, 39, true))
                {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(itemstack1, itemstack);
            }
            // player inv/hotbar slot?
            else if (index != TileEntitySimpleFurnace.NDX_FUEL_SLOT 
            		&& index != TileEntitySimpleFurnace.NDX_INPUT_SLOT)
            {
                if (!FurnaceRecipes.instance().getSmeltingResult(itemstack1).isEmpty())
                {
                    if (!this.mergeItemStack(itemstack1, 0, 1, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
                else if (TileEntityFurnace.isItemFuel(itemstack1))
                {
                    if (!this.mergeItemStack(itemstack1, 1, 2, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
                else if (index >= 3 && index < 30)
                {
                    if (!this.mergeItemStack(itemstack1, 30, 39, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
                else if (index >= 30 && index < 39 && !this.mergeItemStack(itemstack1, 3, 30, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 3, 39, false))
            {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty())
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount())
            {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemstack1);
        }

        return itemstack;
    } // end transferStackInSlot()
    
    /**
     * Looks for changes made in the container, sends them to every listener.
     */
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        for (int i = 0; i < this.listeners.size(); ++i)
        {
            IContainerListener icontainerlistener = this.listeners.get(i);

			if (this.cookTime != this.tileFurnace.getField(TileEntitySimpleFurnace.FIELD_COOK_TIME)) 
			{
				icontainerlistener.sendWindowProperty(this, TileEntitySimpleFurnace.FIELD_COOK_TIME,
						this.tileFurnace.getField(TileEntitySimpleFurnace.FIELD_COOK_TIME));
			}

			if (this.furnaceBurnTime != this.tileFurnace.getField(TileEntitySimpleFurnace.FIELD_BURN_TIME)) 
			{
				icontainerlistener.sendWindowProperty(this, TileEntitySimpleFurnace.FIELD_BURN_TIME,
						this.tileFurnace.getField(TileEntitySimpleFurnace.FIELD_BURN_TIME));
			}

			if (this.currentItemBurnTime != this.tileFurnace.getField(TileEntitySimpleFurnace.FIELD_ITEM_BURN_TIME)) 
			{
				icontainerlistener.sendWindowProperty(this, TileEntitySimpleFurnace.FIELD_ITEM_BURN_TIME,
						this.tileFurnace.getField(TileEntitySimpleFurnace.FIELD_ITEM_BURN_TIME));
			}

			if (this.totalCookTime != this.tileFurnace.getField(TileEntitySimpleFurnace.FIELD_TOTAL_COOK_TIME)) 
			{
				icontainerlistener.sendWindowProperty(this, TileEntitySimpleFurnace.FIELD_TOTAL_COOK_TIME,
						this.tileFurnace.getField(TileEntitySimpleFurnace.FIELD_TOTAL_COOK_TIME));
			}
        }

        this.cookTime = this.tileFurnace.getField(TileEntitySimpleFurnace.FIELD_COOK_TIME);
        this.furnaceBurnTime = this.tileFurnace.getField(TileEntitySimpleFurnace.FIELD_BURN_TIME);
        this.currentItemBurnTime = this.tileFurnace.getField(TileEntitySimpleFurnace.FIELD_ITEM_BURN_TIME);
        this.totalCookTime = this.tileFurnace.getField(TileEntitySimpleFurnace.FIELD_TOTAL_COOK_TIME);
    } // end detectAndSendChanges()

    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data)
    {
        this.tileFurnace.setField(id, data);
    }


} // end class
