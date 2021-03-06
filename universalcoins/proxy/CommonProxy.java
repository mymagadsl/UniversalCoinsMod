package universalcoins.proxy;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import universalcoins.UniversalCoins;
import universalcoins.blocks.BlockTradeStation;
import universalcoins.blocks.BlockVendor;
import universalcoins.items.ItemCoin;
import universalcoins.items.ItemCoinHeap;
import universalcoins.items.ItemLargeCoinBag;
import universalcoins.items.ItemLargeCoinStack;
import universalcoins.items.ItemSeller;
import universalcoins.items.ItemSmallCoinBag;
import universalcoins.items.ItemSmallCoinStack;
import universalcoins.items.ItemVendorWrench;
import universalcoins.items.ItemWrench;
import universalcoins.util.Vending;
import cpw.mods.fml.common.registry.GameRegistry;

public class CommonProxy {
	public static Item itemCoin;
	public static Item itemSmallCoinStack;
	public static Item itemLargeCoinStack;
	public static Item itemCoinHeap; //TODO removal in 1.5.4
	public static Item itemSmallCoinBag;
	public static Item itemLargeCoinBag;
	public static Item itemSeller;
	//public static Item itemCard;
	public static Item itemWrench;
	public static Item itemVendorWrench;
	
	public static Block blockTradeStation;
	public static Block blockVendor;	
	
	
	public void registerBlocks() {
		blockTradeStation = new BlockTradeStation().setBlockName("blockTradeStation");
		blockVendor = new BlockVendor(Vending.supports).setBlockName("blockVendor");
		
		GameRegistry.registerBlock(blockTradeStation, "blockTradeStation").getUnlocalizedName();
		GameRegistry.registerBlock(blockVendor, "blockVendor").getUnlocalizedName();
	}
	
	public void registerItems() {
		itemCoin = new ItemCoin().setUnlocalizedName("itemCoin");
		itemSmallCoinStack = new ItemSmallCoinStack().setUnlocalizedName("itemSmallCoinStack");
		itemLargeCoinStack = new ItemLargeCoinStack().setUnlocalizedName("itemLargeCoinStack");
		itemCoinHeap = new ItemCoinHeap().setUnlocalizedName("itemCoinHeap"); //TODO removal in 1.5.4
		itemSmallCoinBag = new ItemSmallCoinBag().setUnlocalizedName("itemSmallCoinBag");
		itemLargeCoinBag = new ItemLargeCoinBag().setUnlocalizedName("itemLargeCoinBag");
		//itemCard = new ItemUCCard().setUnlocalizedName("itemUCCard");
		itemSeller = new ItemSeller().setUnlocalizedName("itemSeller");
		itemWrench = new ItemWrench().setUnlocalizedName("itemWrench");
		itemVendorWrench = new ItemVendorWrench().setUnlocalizedName("itemVendorWrench");
		
		
		GameRegistry.registerItem(itemCoin, itemCoin.getUnlocalizedName());
		GameRegistry.registerItem(itemSmallCoinStack, itemSmallCoinStack.getUnlocalizedName());
		GameRegistry.registerItem(itemLargeCoinStack, itemLargeCoinStack.getUnlocalizedName());
		GameRegistry.registerItem(itemCoinHeap, itemCoinHeap.getUnlocalizedName()); //TODO removal in 1.5.4
		GameRegistry.registerItem(itemSmallCoinBag, itemSmallCoinBag.getUnlocalizedName());
		GameRegistry.registerItem(itemLargeCoinBag, itemLargeCoinBag.getUnlocalizedName());
		//GameRegistry.registerItem(itemCard, itemCard.getUnlocalizedName());
		GameRegistry.registerItem(itemSeller, itemSeller.getUnlocalizedName());
		GameRegistry.registerItem(itemVendorWrench, itemVendorWrench.getUnlocalizedName());
		if (UniversalCoins.wrenchEnabled) GameRegistry.registerItem(itemWrench, itemWrench.getUnlocalizedName());
	}

	public void registerRenderers() {
		
	}

}
