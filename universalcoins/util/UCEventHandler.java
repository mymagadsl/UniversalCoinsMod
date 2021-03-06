package universalcoins.util;

import universalcoins.UniversalCoins;
import net.minecraft.util.ChatComponentText;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

public class UCEventHandler {

	@SubscribeEvent
	public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
		if (UniversalCoins.updateCheck) {
			if (UpdateCheck.isUpdateAvailable()) {
				event.player.addChatComponentMessage(new ChatComponentText(
				"Universal Coins: An update is available " + UpdateCheck.onlineVersion + " is the latest. See http://goo.gl/Fot7wW for details."));
			}
		}
	}
}
