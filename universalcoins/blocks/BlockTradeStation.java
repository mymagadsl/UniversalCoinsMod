package universalcoins.blocks;

import java.util.Random;

import universalcoins.UniversalCoins;
import universalcoins.tile.TileTradeStation;
import buildcraft.api.tools.IToolWrench;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;

public class BlockTradeStation extends BlockContainer {
	
	private IIcon[] icons;

	public BlockTradeStation() {
		super(new Material(MapColor.stoneColor));
		setHardness(3.0f);
		setCreativeTab(UniversalCoins.tabUniversalCoins);
		setHarvestLevel("pickaxe", 1);	
	}

	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister register){
		icons = new IIcon[2];
		
		for (int i = 0; i < icons.length; i++){
			icons[i] = register.registerIcon(UniversalCoins.modid + ":" +
													  		this.getUnlocalizedName().substring(5) + i);
		}
	}
	
	public IIcon getIcon(int par1, int par2){
		if (par1 == 0 || par1 == 1){
			return icons[1];
		}
		return icons[0];
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer player, int par6, float par7, float par8, float par9) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (tileEntity == null || player.isSneaking()) {
			if (player.getCurrentEquippedItem() != null
					&& player.getCurrentEquippedItem().getItem() instanceof IToolWrench) {
				IToolWrench wrench = (IToolWrench) player.getCurrentEquippedItem().getItem();
				if (wrench.canWrench(player, x, y, z)) {
					Random rand = new Random();
					if (!world.isRemote) {
						ItemStack stack = getItemStackWithData(world, x, y, z);
						EntityItem entityItem = new EntityItem(world, x, y, z,
								stack);
						world.spawnEntityInWorld(entityItem);
						removedByPlayer(world, player, x, y, z);
						if (player.getCurrentEquippedItem().getItem() instanceof IToolWrench) {
							((IToolWrench) player.getCurrentEquippedItem()
									.getItem()).wrenchUsed(player, x, y, z);
						}
					}
					return true;
				}
			}
		}
		player.openGui(UniversalCoins.instance, 0, world, x, y, z);
		return true;
	}
	
	public ItemStack getItemStackWithData(World world, int x, int y, int z) {
		ItemStack stack = new ItemStack(world.getBlock(x, y, z), 1);
		TileEntity tentity = world.getTileEntity(x, y, z);
		if (tentity instanceof TileTradeStation) {
			TileTradeStation te = (TileTradeStation) tentity;
			NBTTagList itemList = new NBTTagList();
			NBTTagCompound tagCompound = new NBTTagCompound();
			for (int i = 0; i < te.getSizeInventory(); i++) {
				ItemStack invStack = te.getStackInSlot(i);
				if (invStack != null) {
					NBTTagCompound tag = new NBTTagCompound();
					tag.setByte("Slot", (byte) i);
					invStack.writeToNBT(tag);
					itemList.appendTag(tag);
				}
			}
			tagCompound.setTag("Inventory", itemList);
			tagCompound.setInteger("CoinsLeft", te.coinSum);
			tagCompound.setInteger("AutoMode", te.autoMode);
			tagCompound.setInteger("CoinMode", te.coinMode);
			tagCompound.setInteger("ItemPrice", te.itemPrice);
			tagCompound.setString("CustomName", te.getInventoryName());
			stack.setTagCompound(tagCompound);
			return stack;
		} else
			return stack;
	}
		
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
		if (world.isRemote) return;
		if (stack.hasTagCompound()) {
			TileEntity te = world.getTileEntity(x, y, z);
			if (te instanceof TileTradeStation) {
				TileTradeStation tentity = (TileTradeStation) te;
				NBTTagCompound tagCompound = stack.getTagCompound();
				if (tagCompound == null) {
					return;
				}
				NBTTagList tagList = tagCompound.getTagList("Inventory", Constants.NBT.TAG_COMPOUND);
				for (int i = 0; i < tagList.tagCount(); i++) {
					NBTTagCompound tag = (NBTTagCompound) tagList.getCompoundTagAt(i);
					byte slot = tag.getByte("Slot");
					if (slot >= 0 && slot < tentity.getSizeInventory()) {
						tentity.setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(tag));
					}
				}
				tentity.coinSum = tagCompound.getInteger("CoinsLeft");
				tentity.autoMode = tagCompound.getInteger("AutoMode");
				tentity.coinMode = tagCompound.getInteger("CoinMode");
				tentity.itemPrice = tagCompound.getInteger("ItemPrice");
				tentity.customName = tagCompound.getString("CustomName");
			}
			world.markBlockForUpdate(x, y, z);
		} else if (stack.hasDisplayName()) {
            ((TileTradeStation)world.getTileEntity(x, y, z)).setInventoryName(stack.getDisplayName());
        }
	}
	
	public void breakBlock(World world, int x, int y, int z, Block par5, int par6) {
        super.breakBlock(world, x, y, z, par5, par6);
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileTradeStation();
	}
}
