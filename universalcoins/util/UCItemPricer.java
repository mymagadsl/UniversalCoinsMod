package universalcoins.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import universalcoins.UniversalCoins;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.registry.GameData;

public class UCItemPricer {

	private static Map<String, Integer> ucPriceMap = new HashMap<String, Integer>(0);
	private static Map<String, String> ucModnameMap = new HashMap<String, String>(0);
	private static String configPath = "config/universalcoins/";
	
	public static void initializeConfigs(){
		if (!new File(configPath).exists()) {
			//FMLLog.info("Universal Coins: Building Pricelists");
			buildInitialPricelistHashMap();
			try {
				loadDefaults();
			} catch (IOException e) {
				FMLLog.warning("Universal Coins: Failed to load default configs");
				e.printStackTrace();
			}
			writePriceLists();
		}
	}
	
	public static void loadConfigs() {
		try {
			UCItemPricer.loadPricelists();
		} catch (IOException e) {
			FMLLog.warning("Universal Coins: Failed to load config files");
			e.printStackTrace();
		}
	}
	
	private static void loadDefaults() throws IOException {
		String configList[] = {"defaultConfigs/minecraft.cfg","defaultConfigs/BuildCraft.cfg","defaultConfigs/universalcoins.cfg"};
		InputStream priceResource;
		//load those files into hashmap(ucPriceMap)
		for (int i = 0; i < configList.length; i++) {
			priceResource = UCItemPricer.class.getResourceAsStream(configList[i]);
			if (priceResource == null){
				return;
			}
			String priceString = convertStreamToString(priceResource);
			updateInitialPricelistHashMap(priceString);
		}
	}
	
	private static String convertStreamToString(java.io.InputStream is) {
		//Thanks to Pavel Repin on StackOverflow.
		java.util.Scanner scanner = new java.util.Scanner(is);
		java.util.Scanner s = scanner.useDelimiter("\\A");
		String result =  s.hasNext() ? s.next() : "";
		scanner.close();
		return result;
	}
	
	private static void updateInitialPricelistHashMap(String priceString) {
		StringTokenizer tokenizer = new StringTokenizer(priceString, "\n\r", false);
		while (tokenizer.hasMoreElements()){
			String token = tokenizer.nextToken();
			String[] tempData = token.split("=");
			//FMLLog.info("Universal Coins: Updating UCPricelist: " + tempData[0] + "=" + Integer.valueOf(tempData[1]));
			//We'll update the prices of all the items and not add all the default prices to the config folder
			if (ucPriceMap.get(tempData[0]) != null) {
				ucPriceMap.put(tempData[0], Integer.valueOf(tempData[1]));
			}
		}
	}

	public static void buildInitialPricelistHashMap() {
		ArrayList<ItemStack> itemsDiscovered = new ArrayList<ItemStack>();

		for (String item : (Iterable<String>) Item.itemRegistry.getKeys()) {
			// pass the itemkey to a temp variable after splitting on
			// non-alphanumeric values
			String[] tempModName = item.split("\\W", 3);
			// pass the first value as modname
			String modName = tempModName[0];
			if (item != null) {
				Item test = (Item) Item.itemRegistry.getObject(item);
				// check for meta values so we catch all items
				// Iterate through damage values and add them if unique
				for (int i = 0; i < 16; i++) {
					ItemStack value = new ItemStack(test, 1, i);
					try {
						// IIcon icon = test.getIconIndex(value);
						String name = value.getUnlocalizedName();
						if (name != null && !itemsDiscovered.contains(name)) {
							itemsDiscovered.add(value);
							continue;
						}
					} catch (Throwable ex) {
						// fail quietly
					}
				}
				
			}
			// iterate through the items and update the hashmaps
			for (ItemStack itemstack : itemsDiscovered) {
				// update ucModnameMap with items found
				ucModnameMap.put(itemstack.getUnlocalizedName(), modName);
				// update ucPriceMap with initial values
				ucPriceMap.put(itemstack.getUnlocalizedName(), -1);
			}
			//clear this variable so we can use it next round
			itemsDiscovered.clear();
		}
	}
	
private static void loadPricelists() throws IOException {
	//search config file folder for files
	File folder = new File(configPath);
	File[] configList = folder.listFiles();
	//load those files into hashmap(UCPriceMap)
	for (int i = 0; i < configList.length; i++) {
	      if (configList[i].isFile()) {
	    	  //FMLLog.info("Universal Coins: Loading Pricelist " + configList[i]);
	    	  BufferedReader br = new BufferedReader(new FileReader(configList[i]));
	    	  String tempString = "";
	    	  String[] modName = configList[i].getName().split("\\.");
	    	  while ((tempString = br.readLine()) != null) {
	    		  String[] tempData = tempString.split("=");
	    		  ucPriceMap.put(tempData[0], Integer.valueOf(tempData[1]));
	    		  ucModnameMap.put(tempData[0], modName[0]);
	    	  }
	    	  br.close();
	      }  
		}
	}

	public static void writePriceLists() {
		//write config set from item hashmap
		Set set = ucPriceMap.entrySet();
		Iterator i = set.iterator();
		while (i.hasNext()) {
			Map.Entry me = (Map.Entry) i.next();
			String keyname = (String) me.getKey();
			String modname = ucModnameMap.get(keyname) + ".cfg";
			Path pathToFile = Paths.get(configPath + modname);
			try {
				Files.createDirectories(pathToFile.getParent());
			} catch (IOException e) {
				FMLLog.warning("Universal Coins: Failed to create config file folders");
			}
			File modconfigfile = new File(configPath + modname);
			if(!modconfigfile.exists()) {
				try {
					modconfigfile.createNewFile();
					} 
				catch (IOException e) {
					FMLLog.warning("Universal Coins: Failed to create config file");
					}
			}
			try {
				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(modconfigfile, true)));
				out.println(me.getKey() + "=" + me.getValue());
				out.close();
				}
			catch (IOException e) {
				FMLLog.warning("Universal Coins: Failed to append to config file");
			}
			
		}
	}
	
	public static int getItemPrice(ItemStack itemStack) {
		if (itemStack == null) {
			//FMLLog.warning("itemstack is null");
			return -1;
		}
		//return getItemPrice(itemStack.getItem());
		Integer ItemPrice = -1;
		String itemName = itemStack.getUnlocalizedName();
		if (ucPriceMap.get(itemName) != null) {
			ItemPrice = ucPriceMap.get(itemName);
		}
		return ItemPrice;
	}
	
	public static int getItemPrice(String string) {
		if (string.isEmpty()) {
			return -1;
		}
		Integer ItemPrice = -1;
		if (ucPriceMap.get(string) != null) {
			ItemPrice = ucPriceMap.get(string);
		}
		return ItemPrice;
	}
	
	public static boolean setItemPrice(ItemStack itemStack, int price) {
		if (itemStack == null) {
			return false;
		}
		String itemName = itemStack.getUnlocalizedName();
		if (ucPriceMap.containsKey(itemName)) {
			ucPriceMap.put(itemName, price);
			return true;
		}
		return false;
	}
	
	public static boolean setItemPrice(String string, int price) {
		if (string.isEmpty()) {
			return false;
		}
		if (ucPriceMap.containsKey(string)) {
			ucPriceMap.put(string, price);
			return true;
		}
		return false;
	}
	
	public static void updatePriceLists() {
		//delete old configs
		File folder = new File(configPath);
		if(folder.exists()){
	        File[] files = folder.listFiles();
	        if(null!=files){
	            for(int i=0; i<files.length; i++) {
	                    files[i].delete();
	            }
	        }
	    }
		//write new configs
		writePriceLists();
	}

	public static ItemStack getRevenueStack(int itemPrice) {
		if (itemPrice <= 64) {
			return new ItemStack(UniversalCoins.proxy.itemCoin, itemPrice);
		} else if (itemPrice <= 9 * 64) {
			return new ItemStack(UniversalCoins.proxy.itemSmallCoinStack,
					itemPrice / 9);
		} else if (itemPrice <= 81 * 64) {
			return new ItemStack(UniversalCoins.proxy.itemLargeCoinStack,
					itemPrice / 81);
		} else if (itemPrice <= 729 * 64) {
			return new ItemStack(UniversalCoins.proxy.itemSmallCoinBag, Math.min(
					itemPrice / 729, 64));
		}else {
			return new ItemStack(UniversalCoins.proxy.itemLargeCoinBag, Math.min(
					itemPrice / 6561, 64));
		}
	}

}
