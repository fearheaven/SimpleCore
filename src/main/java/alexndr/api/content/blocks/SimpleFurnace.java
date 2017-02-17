package alexndr.api.content.blocks;

import java.util.Random;

import alexndr.api.config.types.ConfigBlock;
import alexndr.api.content.tiles.TileEntitySimpleFurnace;
import alexndr.api.helpers.game.IConfigureBlockHelper;
import alexndr.api.helpers.game.TooltipHelper;
import alexndr.api.registry.ContentCategories;
import alexndr.api.registry.ContentRegistry;
import alexndr.api.registry.Plugin;
import mcjty.lib.compat.CompatBlock;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
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
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * We can't just subclass BlockFurnace, because then the material does not get properly set. Why not?
 * Because whoever wrote the Block class couldn't be bothered to write a setter for Block.material, that's
 * why.
 *
 */
public abstract class SimpleFurnace extends CompatBlock 
    implements ITileEntityProvider, IConfigureBlockHelper<SimpleFurnace>
{
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	
    /**
     * Is the random generator used by furnace to drop the inventory contents in random directions.
     */
	protected Random furnaceRand = new Random();
	
	protected Plugin plugin;
	protected Material material;
	protected ContentCategories.Block category;
	protected ConfigBlock entry;
	protected boolean isBurning;
	
	// override for custom furnace classes
	protected static Block unlit_furnace = Blocks.LIT_FURNACE;
	protected static Block lit_furnace = Blocks.FURNACE;
	
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
	protected SimpleFurnace(Plugin plugin, Material material, ContentCategories.Block category,
							boolean isBurning) 
	{
		super(material);
		this.plugin = plugin;
		this.material = material;
		this.category = category;
		this.isBurning = isBurning;
	    this.isBlockContainer = true;
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
	} // end ctor()
	
	@Override
	public SimpleFurnace setUnlocalizedName(String blockName) 
	{
		super.setUnlocalizedName(blockName);
        setRegistryName(this.plugin.getModId(), blockName);
        GameRegistry.register(this);
        GameRegistry.register(new ItemBlock(this).setRegistryName(this.getRegistryName()));
		ContentRegistry.registerBlock(this.plugin, this, blockName, this.category);
		return this;
	}
	
	/**
	 * Returns the ConfigBlock used by this block.
	 * @return ConfigBlock
	 */
	public ConfigBlock getConfigEntry() {
		return this.entry;
	}
	
	/**
	 * Sets the ConfigBlock to be used for this block.
	 * @param entry ConfigBlock
	 * @return SimpleFurnace
	 */
	public SimpleFurnace setConfigEntry(ConfigBlock entry) {
		this.entry = entry;
		this.setHardness(entry.getHardness());
		this.setResistance(entry.getResistance());
		if(this.isBurning) this.setLightLevel(entry.getLightValue());
		this.setHarvestLevel(entry.getHarvestTool(), entry.getHarvestLevel());
		this.setCreativeTab(entry.getCreativeTab());
		this.setAdditionalProperties();
		return this;
	}
	
	/**
	 * Adds a tooltip to the block. Must be unlocalised, so needs to be present in a localization file.
	 * @param toolTip Name of the localisation entry for the tooltip, as a String. Normal format is modId.theitem.info
	 * @return SimpleBlock
	 */
	public SimpleFurnace addToolTip(String toolTip) {
		TooltipHelper.addTooltipToBlock(this, toolTip);
		return this;
	}

	public void setAdditionalProperties() {
		if(entry.getValueByName("CreativeTab") != null 
				&& entry.getValueByName("CreativeTab").isActive()) 
		{
			this.setCreativeTab(entry.getCreativeTab());
		}
		if(entry.getValueByName("Unbreakable") != null 
				&& entry.getValueByName("Unbreakable").isActive()) 
		{
			this.setBlockUnbreakable();
		}
	}

	/* --- COPIED FROM BlockContainer ----- */
    /**
     * Called on both Client and Server when World#addBlockEvent is called. On the Server, this may perform additional
     * changes to the world, like pistons replacing the block with an extended base. On the client, the update may
     * involve replacing tile entities, playing sounds, or performing other visual actions to reflect the server side
     * changes. 
     */
    @SuppressWarnings("deprecation")
    public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param)
    {
        super.eventReceived(state, worldIn, pos, id, param);
        TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
    }
	
	/*------------- MOST OF THE FOLLOWING ARE CUT & PASTED FROM BlockFurnace ----------------------*/
	/* If BlockFurnace changes, you will need to change these methods -- Sinhika                   */
	/*---------------------------------------------------------------------------------------------*/
	/* cut & pasted from BlockFurnace */
	@Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        this.setDefaultFacing(worldIn, pos, state);
    }

	/* cut & pasted from BlockFurnace */
	protected void setDefaultFacing(World worldIn, BlockPos pos, IBlockState state)
	{
		if (!worldIn.isRemote)
		{
			IBlockState iblockstate = worldIn.getBlockState(pos.north());
			IBlockState iblockstate1 = worldIn.getBlockState(pos.south());
			IBlockState iblockstate2 = worldIn.getBlockState(pos.west());
			IBlockState iblockstate3 = worldIn.getBlockState(pos.east());
			EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);

			if (enumfacing == EnumFacing.NORTH && iblockstate.isFullBlock() && !iblockstate1.isFullBlock())
			{
				enumfacing = EnumFacing.SOUTH;
			}
			else if (enumfacing == EnumFacing.SOUTH && iblockstate1.isFullBlock() && !iblockstate.isFullBlock())
			{
				enumfacing = EnumFacing.NORTH;
			}
			else if (enumfacing == EnumFacing.WEST && iblockstate2.isFullBlock() && !iblockstate3.isFullBlock())
			{
				enumfacing = EnumFacing.EAST;
			}
			else if (enumfacing == EnumFacing.EAST && iblockstate3.isFullBlock() && !iblockstate2.isFullBlock())
			{
				enumfacing = EnumFacing.WEST;
			}

			worldIn.setBlockState(pos, state.withProperty(FACING, enumfacing), 2);
		}
	}
	

	/* cut & pasted from BlockFurnace */
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("incomplete-switch")
    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        if (this.isBurning)
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
        }
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
     public IBlockState clGetStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }


    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic. 
     * Mostly cut & pasted from BlockFurnace.
     */
    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);

        if (stack.hasDisplayName())
        {
            TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof TileEntitySimpleFurnace)
            {
                ((TileEntitySimpleFurnace)tileentity).setCustomInventoryName(stack.getDisplayName());
            }
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

            if (tileentity instanceof TileEntitySimpleFurnace)
            {
                InventoryHelper.dropInventoryItems(worldIn, pos, (TileEntitySimpleFurnace)tileentity);
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
        EnumFacing enumfacing = EnumFacing.getFront(meta);

        if (enumfacing.getAxis() == EnumFacing.Axis.Y)
        {
            enumfacing = EnumFacing.NORTH;
        }

        return this.getDefaultState().withProperty(FACING, enumfacing);
    }
    
    /**
     * Convert the BlockState into the correct metadata value
     */
    @Override
    public int getMetaFromState(IBlockState state)
    {
        return ((EnumFacing)state.getValue(FACING)).getIndex();
    }

    /**
     * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     */
    public IBlockState withRotation(IBlockState state, Rotation rot)
    {
        return state.withProperty(FACING, rot.rotate((EnumFacing)state.getValue(FACING)));
    }

    /**
     * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     */
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn)
    {
        return state.withRotation(mirrorIn.toRotation((EnumFacing)state.getValue(FACING)));
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {FACING});
    }
    
    /* ----------- Special to SimpleFurnace, not cut & pasted from BlockFurnace -------- */
    /* ----------- MUST BE RE-IMPLEMENTED IN CHILD CLASSES ----------------------------- */
    
    public static SimpleFurnace getUnlit_furnace()
	{
		return (SimpleFurnace) unlit_furnace;
	}

	public static SimpleFurnace getLit_furnace()
	{
		return (SimpleFurnace) lit_furnace;
	}

    /* -------------- EVERYTHING BELOW HERE MUST BE OVERRIDDEN IN CHILD CLASSES ----------- */
	
    @SideOnly(Side.CLIENT)
    @Override
    public abstract ItemStack getItem(World worldIn, BlockPos pos, IBlockState state);

    /**
     * Get the Item that this Block should drop when harvested.
     */
	@Override
    public abstract Item getItemDropped(IBlockState state, Random rand, int fortune);
	
    /**
     * this must be overridden by instance class...
     */
    @Override
	public abstract TileEntity createNewTileEntity(World worldIn, int meta);

} // end class
