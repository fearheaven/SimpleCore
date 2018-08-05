package alexndr.api.content.blocks;

import java.util.Random;

import alexndr.api.config.types.ConfigBlock;
import alexndr.api.content.tiles.TileEntityBaseFurnace;
import alexndr.api.content.tiles.TileEntityBaseInventory;
import alexndr.api.helpers.game.IItemHandlerHelper;
import alexndr.api.helpers.game.TooltipHelper;
import alexndr.api.registry.ContentCategories;
import alexndr.api.registry.Plugin;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * We can't just subclass BlockFurnace, because then the material does not get properly set. Why not?
 * Because whoever wrote the Block class couldn't be bothered to write a setter for Block.material, that's
 * why.
 *
 */
public abstract class SimpleFurnace<TEF extends TileEntityBaseInventory> extends SimpleTileEntityBlock<TEF> 
{
    public static final PropertyDirection FACING = BlockHorizontal.FACING;
	
	protected boolean isBurning;
	
	// override for custom furnace classes
	protected static Block unlit_furnace;
	protected static Block lit_furnace;
	
    /**
     * This flag is used to prevent the furnace inventory to be dropped upon block removal, is used internally when the
     * furnace block changes from idle to active and vice-versa.
     */
	protected static boolean keepInventory = false;

	/**
	 * basis for a mod furnace of some type.
	 * @param plugin The plugin the block belongs to
	 * @param material The material of the block
	 * @param category The category of the block
	 * @param isBurning is the furnace lit or not?
	 */
	public SimpleFurnace(String name, Plugin plugin, Material material, 
							ContentCategories.Block category, boolean isBurning) 
	{
		super(name, plugin, material, category);
	    this.hasTileEntity = true;
	    this.isBurning = isBurning;
	    this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
	    setLit_furnace(Blocks.LIT_FURNACE);
	    setUnlit_furnace(Blocks.FURNACE);
	} // end ctor()
	
	/**
	 * Sets the ConfigBlock to be used for this block.
	 * @param entry ConfigBlock
	 * @return SimpleFurnace
	 */
	@Override
	public SimpleFurnace<TEF> setConfigEntry(ConfigBlock entry) 
	{
		super.setConfigEntry(entry);
		if(this.isBurning) {
			this.setLightLevel(entry.getLightValue());
		}
		else {
			this.setLightLevel(0.0F);
		}
		this.setAdditionalProperties();
		return this;
	}
	
	/**
	 * Adds a tooltip to the block. Must be unlocalised, so needs to be present in a localization file.
	 * @param toolTip Name of the localisation entry for the tooltip, as a String. Normal format is modId.theitem.info
	 * @return SimpleBlock
	 */
	public SimpleFurnace<TEF> addToolTip(String toolTip) {
		TooltipHelper.addTooltipToBlock(this, toolTip);
		return this;
	}

	public void setAdditionalProperties() 
	{
		if(entry.getUnbreakable()) 
		{
			this.setBlockUnbreakable();
		}
	}

//	/* --- COPIED FROM BlockContainer ----- */
//    /**
//     * Called on both Client and Server when World#addBlockEvent is called. On the Server, this may perform additional
//     * changes to the world, like pistons replacing the block with an extended base. On the client, the update may
//     * involve replacing tile entities, playing sounds, or performing other visual actions to reflect the server side
//     * changes. 
//     */
//    @SuppressWarnings("deprecation")
//    public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param)
//    {
//        super.eventReceived(state, worldIn, pos, id, param);
//        TileEntity tileentity = worldIn.getTileEntity(pos);
//        return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
//    }
//	

	
	@Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        this.setDefaultFacing(worldIn, pos, state);
    }

	/**
	 * Which way should the furnace block face?
	 * @param worldIn
	 * @param pos
	 * @param state
	 */
	protected void setDefaultFacing(World worldIn, BlockPos pos, IBlockState state)
	{
		if (!worldIn.isRemote)
		{
			EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);
			EnumFacing otherface = enumfacing.getOpposite();
			IBlockState iblockstate = worldIn.getBlockState(pos.offset(enumfacing));
			IBlockState iblockstate1 = worldIn.getBlockState(pos.offset(otherface));
			
			if (iblockstate.isFullBlock() && !iblockstate1.isFullBlock()) {
				worldIn.setBlockState(pos, state.withProperty(FACING, otherface), 2);
			}
			else {
				worldIn.setBlockState(pos, state.withProperty(FACING, enumfacing), 2);
				
			}
		} // !isRemote
	} // end setDefaultFacing()
	

	/* cut & pasted from BlockFurnace */
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("incomplete-switch")
    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        if (worldIn.isRemote && this.isBurning)
        {
            EnumFacing enumfacing = (EnumFacing)stateIn.getValue(FACING);
            double d0 = (double)pos.getX() + 0.5D;
            double d1 = (double)pos.getY() + rand.nextDouble() * 6.0D / 16.0D;
            double d2 = (double)pos.getZ() + 0.5D;
            double d3 = 0.52D;
            double d4 = rand.nextDouble() * 0.6D - 0.3D;

            if (rand.nextDouble() < 0.1D)
            {
                worldIn.playSound((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
            }

            switch (enumfacing)
            {
                case WEST:
                    worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 - d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D, new int[0]);
                    worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 - d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D, new int[0]);
                    break;
                case EAST:
                    worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D, new int[0]);
                    worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 + d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D, new int[0]);
                    break;
                case NORTH:
                    worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + d4, d1, d2 - d3, 0.0D, 0.0D, 0.0D, new int[0]);
                    worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 + d4, d1, d2 - d3, 0.0D, 0.0D, 0.0D, new int[0]);
                    break;
                case SOUTH:
                    worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + d4, d1, d2 + d3, 0.0D, 0.0D, 0.0D, new int[0]);
                    worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 + d4, d1, d2 + d3, 0.0D, 0.0D, 0.0D, new int[0]);
            }
        } // end-if isRemote && isBurning
    } // end randomDisplayTick()

    /**
     * Mostly cut & pasted from BlockFurnace. This *MUST* be overridden for custom classes...
     * @param active
     * @param worldIn
     * @param pos
     */
    public static void setState(boolean active, World worldIn, BlockPos pos)
    {
        IBlockState iblockstate = worldIn.getBlockState(pos);
        TileEntity tileentity = worldIn.getTileEntity(pos);
    	
        keepInventory = true;

        if (active)
        {
            worldIn.setBlockState(pos, getLit_furnace().getDefaultState().withProperty(FACING, iblockstate.getValue(FACING)), 3);
            worldIn.setBlockState(pos, getLit_furnace().getDefaultState().withProperty(FACING, iblockstate.getValue(FACING)), 3);
        }
        else
        {
            worldIn.setBlockState(pos, getUnlit_furnace().getDefaultState().withProperty(FACING, iblockstate.getValue(FACING)), 3);
            worldIn.setBlockState(pos, getUnlit_furnace().getDefaultState().withProperty(FACING, iblockstate.getValue(FACING)), 3);
        }

        keepInventory = false;

        if (tileentity != null)
        {
            tileentity.validate();
            worldIn.setTileEntity(pos, tileentity);
        }
    } // end setState()
    
    /**
     * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
     * IBlockstate. Cut & pasted from BlockFurnace.
     */
    @Override
     public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing,
    		 				float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }


    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic. 
     * Mostly cut & pasted from BlockFurnace.
     */
    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, 
    							EntityLivingBase placer, ItemStack stack)
    {
    	TileEntity te = worldIn.getTileEntity(pos);

    	if (!worldIn.isRemote && te instanceof TileEntityBaseFurnace && placer instanceof EntityPlayer)
    	{
    		((TileEntityBaseFurnace) te).setPlayer((EntityPlayer) placer);
    	}
    } // end ()

    /**
     * cut & pasted from BlockFurnace.
     */
    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!keepInventory)
        {
            TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof TileEntityBaseFurnace)
            {
                IItemHandlerHelper.dropInventoryItems(worldIn, pos, 
                							((TileEntityBaseInventory)tileentity).getSlotHandler());
                worldIn.updateComparatorOutputLevel(pos, this);
            }
        }

        super.breakBlock(worldIn, pos, state);
    } // end ()

    @Override
    public boolean hasComparatorInputOverride(IBlockState state)
    {
        return true;
    }

    /**
     * If hasComparatorInputOverride returns true, the return value from this is used instead of the redstone signal
     * strength when this block inputs to a comparator.
     */
   @Override
    public int getComparatorInputOverride(IBlockState blockState, World worldIn, 
    									  BlockPos pos)
    {
        return Container.calcRedstone(worldIn.getTileEntity(pos));
    }

    /**
     * The type of render function called. 3 for standard block models, 2 for TESR's, 1 for liquids, -1 is no render
     */
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.VALUES[meta % 6]);
    }
    
    /**
     * Convert the BlockState into the correct metadata value
     */
    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(FACING).ordinal();
    }

    @Override
	public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) 
    {
		EnumFacing facing = world.getBlockState(pos).getValue(FACING);
		EnumFacing rotated = facing.rotateAround(axis.getAxis());
		return world.setBlockState(pos, getDefaultState().withProperty(FACING, rotated));
	}

    /**
     * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     */
    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot)
    {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }

    /**
     * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     */
    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn)
    {
        return state.withRotation(mirrorIn.toRotation((EnumFacing)state.getValue(FACING)));
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING);
    }

	public static Block getUnlit_furnace() {
		return unlit_furnace;
	}

	public static void setUnlit_furnace(Block unlit_furnace) {
		SimpleFurnace.unlit_furnace = unlit_furnace;
	}

	public static Block getLit_furnace() {
		return lit_furnace;
	}

	public static void setLit_furnace(Block lit_furnace) {
		SimpleFurnace.lit_furnace = lit_furnace;
	}
    
} // end class
