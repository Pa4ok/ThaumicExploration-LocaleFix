package flaxbeard.thaumicexploration.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import flaxbeard.thaumicexploration.ThaumicExploration;
import flaxbeard.thaumicexploration.tile.TileEntityBoundChest;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockBoundChest extends BlockContainer {
    private final Random random = new Random();

    /** 1 for trapped chests, 0 for normal chests. */
    public final int chestType;

    public BlockBoundChest() {
        super(Material.wood);
        this.chestType = 0;
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    public boolean isOpaqueCube() {
        return false;
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean renderAsNormalBlock() {
        return false;
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType() {
        return 22;
    }

    /**
     * Updates the blocks bounds based on its current state. Args: world, x, y, z
     */
    public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {

        this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
    }

    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    public void onBlockAdded(World par1World, int par2, int par3, int par4) {
        super.onBlockAdded(par1World, par2, par3, par4);
    }

    /**
     * Called when the block is placed in the world.
     */
    public void onBlockPlacedBy(
            World par1World,
            int par2,
            int par3,
            int par4,
            EntityLivingBase par5EntityLivingBase,
            ItemStack par6ItemStack) {
        // int l = par1World.getBlockId(par2, par3, par4 - 1);
        // int i1 = par1World.getBlockId(par2, par3, par4 + 1);
        // int j1 = par1World.getBlockId(par2 - 1, par3, par4);
        // int k1 = par1World.getBlockId(par2 + 1, par3, par4);
        byte b0 = 0;
        int l1 = MathHelper.floor_double((double) (par5EntityLivingBase.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        if (l1 == 0) {
            b0 = 2;
        }

        if (l1 == 1) {
            b0 = 5;
        }

        if (l1 == 2) {
            b0 = 3;
        }

        if (l1 == 3) {
            b0 = 4;
        }

        par1World.setBlockMetadataWithNotify(par2, par3, par4, b0, 3);

        if (par6ItemStack.hasDisplayName()) {
            ((TileEntityBoundChest) par1World.getTileEntity(par2, par3, par4))
                    .setChestGuiName(par6ItemStack.getDisplayName());
        }
    }

    /**
     * Turns the adjacent chests to a double chest.
     */
    public void unifyAdjacentChests(World par1World, int par2, int par3, int par4) {}

    /**
     * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
     */
    public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4) {
        return true;
    }

    @Override
    public Item getItemDropped(int par1, Random par2Random, int par3) {
        return Item.getItemFromBlock(Blocks.chest);
    }

    @Override
    public Item getItem(World par1World, int par2, int par3, int par4) {
        return Item.getItemFromBlock(Blocks.chest);
    }

    /**
     * Checks the neighbor blocks to see if there is a chest there. Args: world, x, y, z
     */
    private boolean isThereANeighborChest(World par1World, int par2, int par3, int par4) {
        return par1World.getBlock(par2, par3, par4) != this
                ? false
                : (par1World.getBlock(par2 - 1, par3, par4) == this
                        ? true
                        : (par1World.getBlock(par2 + 1, par3, par4) == this
                                ? true
                                : (par1World.getBlock(par2, par3, par4 - 1) == this
                                        ? true
                                        : par1World.getBlock(par2, par3, par4 + 1) == this)));
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, Block par5) {
        super.onNeighborBlockChange(par1World, par2, par3, par4, par5);
        TileEntityBoundChest tileentitychest = (TileEntityBoundChest) par1World.getTileEntity(par2, par3, par4);

        if (tileentitychest != null) {
            tileentitychest.updateContainingBlockInfo();
        }
    }

    public void dropItem(ItemStack itemstack, World par1World, int par2, int par3, int par4) {
        if (itemstack != null) {
            float f = this.random.nextFloat() * 0.8F + 0.1F;
            float f1 = this.random.nextFloat() * 0.8F + 0.1F;
            EntityItem entityitem;

            for (float f2 = this.random.nextFloat() * 0.8F + 0.1F;
                    itemstack.stackSize > 0;
                    par1World.spawnEntityInWorld(entityitem)) {
                int k1 = this.random.nextInt(21) + 10;

                if (k1 > itemstack.stackSize) {
                    k1 = itemstack.stackSize;
                }

                itemstack.stackSize -= k1;
                entityitem = new EntityItem(
                        par1World,
                        (double) ((float) par2 + f),
                        (double) ((float) par3 + f1),
                        (double) ((float) par4 + f2),
                        new ItemStack(itemstack.getItem(), k1, itemstack.getItemDamage()));
                float f3 = 0.05F;
                entityitem.motionX = (double) ((float) this.random.nextGaussian() * f3);
                entityitem.motionY = (double) ((float) this.random.nextGaussian() * f3 + 0.2F);
                entityitem.motionZ = (double) ((float) this.random.nextGaussian() * f3);

                if (itemstack.hasTagCompound()) {
                    entityitem.getEntityItem().setTagCompound((NBTTagCompound)
                            itemstack.getTagCompound().copy());
                }
            }
        }
    }

    /**
     * Called on server worlds only when the block has been replaced by a different block ID, or the same block with a
     * different metadata value, but before the new metadata value is set. Args: World, x, y, z, old block ID, old
     * metadata
     */
    public void breakBlock(World par1World, int par2, int par3, int par4, Block par5, int par6) {
        TileEntityBoundChest tileentitychest = (TileEntityBoundChest) par1World.getTileEntity(par2, par3, par4);

        if (tileentitychest != null) {
            this.dropItem(
                    new ItemStack(
                            ThaumicExploration.blankSeal,
                            1,
                            15 - ((TileEntityBoundChest) par1World.getTileEntity(par2, par3, par4)).getSealColor()),
                    par1World,
                    par2,
                    par3,
                    par4);
            for (int j1 = 0; j1 < tileentitychest.getSizeInventory(); ++j1) {
                ItemStack itemstack = tileentitychest.getStackInSlot(j1);
                this.dropItem(itemstack, par1World, par2, par3, par4);
            }

            par1World.func_147453_f(par2, par3, par4, par5);
        }

        super.breakBlock(par1World, par2, par3, par4, par5, par6);
    }

    /**
     * Called upon block activation (right click on the block.)
     */
    public boolean onBlockActivated(
            World par1World,
            int par2,
            int par3,
            int par4,
            EntityPlayer par5EntityPlayer,
            int par6,
            float par7,
            float par8,
            float par9) {
        if (par1World.isRemote) {

            return true;
        } else {
            IInventory iinventory = this.getInventory(par1World, par2, par3, par4);

            if (iinventory != null) {
                par5EntityPlayer.displayGUIChest(iinventory);
            }

            return true;
        }
    }

    /**
     * Gets the inventory of the chest at the specified coords, accounting for blocks or ocelots on top of the chest,
     * and double chests.
     */
    public IInventory getInventory(World par1World, int par2, int par3, int par4) {
        Object object = (TileEntityBoundChest) par1World.getTileEntity(par2, par3, par4);

        if (object == null) {
            return null;
        } else if (par1World.isSideSolid(par2, par3 + 1, par4, ForgeDirection.DOWN)) {
            return null;
        } else if (isOcelotBlockingChest(par1World, par2, par3, par4)) {
            return null;
        } else {

            return (IInventory) object;
        }
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(World par1World) {
        TileEntityBoundChest tileentitychest = new TileEntityBoundChest();
        return tileentitychest;
    }

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    public boolean canProvidePower() {
        return this.chestType == 1;
    }

    /**
     * Returns true if the block is emitting indirect/weak redstone power on the specified side. If isBlockNormalCube
     * returns true, standard redstone propagation rules will apply instead and this will not be called. Args: World, X,
     * Y, Z, side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
     */
    public int isProvidingWeakPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
        if (!this.canProvidePower()) {
            return 0;
        } else {
            int i1 = ((TileEntityBoundChest) par1IBlockAccess.getTileEntity(par2, par3, par4)).numUsingPlayers;
            return MathHelper.clamp_int(i1, 0, 15);
        }
    }

    /**
     * Returns true if the block is emitting direct/strong redstone power on the specified side. Args: World, X, Y, Z,
     * side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
     */
    public int isProvidingStrongPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
        return par5 == 1 ? this.isProvidingWeakPower(par1IBlockAccess, par2, par3, par4, par5) : 0;
    }

    /**
     * Looks for a sitting ocelot within certain bounds. Such an ocelot is considered to be blocking access to the
     * chest.
     */
    public static boolean isOcelotBlockingChest(World par0World, int par1, int par2, int par3) {
        Iterator iterator = par0World
                .getEntitiesWithinAABB(
                        EntityOcelot.class,
                        AxisAlignedBB.getBoundingBox(
                                (double) par1,
                                (double) (par2 + 1),
                                (double) par3,
                                (double) (par1 + 1),
                                (double) (par2 + 2),
                                (double) (par3 + 1)))
                .iterator();
        EntityOcelot entityocelot;

        do {
            if (!iterator.hasNext()) {
                return false;
            }

            EntityOcelot entityocelot1 = (EntityOcelot) iterator.next();
            entityocelot = (EntityOcelot) entityocelot1;
        } while (!entityocelot.isSitting());

        return true;
    }

    /**
     * If this returns true, then comparators facing away from this block will use the value from
     * getComparatorInputOverride instead of the actual redstone signal strength.
     */
    public boolean hasComparatorInputOverride() {
        return true;
    }

    /**
     * If hasComparatorInputOverride returns true, the return value from this is used instead of the redstone signal
     * strength when this block inputs to a comparator.
     */
    public int getComparatorInputOverride(World par1World, int par2, int par3, int par4, int par5) {
        return Container.calcRedstoneFromInventory(this.getInventory(par1World, par2, par3, par4));
    }

    @Override
    @SideOnly(Side.CLIENT)

    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    public void registerBlockIcons(IIconRegister par1IconRegister) {
        this.blockIcon = par1IconRegister.registerIcon("planks_oak");
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int var2) {
        TileEntityBoundChest tileentitychest = new TileEntityBoundChest();
        return tileentitychest;
    }
}
