package de.jakkoble

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential
import com.github.twitch4j.TwitchClient
import com.github.twitch4j.TwitchClientBuilder
import com.github.twitch4j.pubsub.events.RewardRedeemedEvent
import de.jakkoble.Whitelist.usedWhitelist
import de.jakkoble.Whitelist.whitelist
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import java.util.logging.Level

class TwitchBot {
   private lateinit var twitchClient: TwitchClient
   private var available = true
   private val token = Config().getData("token")
   private val credential = OAuth2Credential("twitch", token)
   fun connect() {
      if (token.length != 30 || Config().getData("channel") == "YourChannelID") {
         TwitchWhitelist.instance.server.consoleSender.sendMessage("")
         TwitchWhitelist.instance.server.consoleSender.sendMessage("${ChatColor.DARK_RED}You have not set the 'Bot Chat Token' or 'ChannelID' in the Config yet! Without these it will not work.")
         TwitchWhitelist.instance.server.consoleSender.sendMessage("${ChatColor.DARK_RED}To get the Access Token, visit: https://twitchtokengenerator.com/")
         TwitchWhitelist.instance.server.consoleSender.sendMessage("${ChatColor.DARK_RED}To get the ChannelID aka TwitchID, visit: https://www.streamweasels.com/tools/convert-twitch-username-to-user-id/")
         TwitchWhitelist.instance.server.consoleSender.sendMessage("${ChatColor.DARK_RED}Stop the Server, Setup the Config for your needs and Start the Server again.")
         TwitchWhitelist.instance.server.consoleSender.sendMessage("")
         available = false
         return
      }
      twitchClient = TwitchClientBuilder.builder()
         .withEnableChat(true)
         .withEnablePubSub(true)
         .withEnableHelix(true)
         .withChatAccount(credential)
         .build()
      val userName = getChannelofID(Config().getData("channel"))
      if (!twitchClient.chat.isChannelJoined(userName)) twitchClient.chat.joinChannel(userName)
      registerEvent()
   }
   fun disconnect() {
      if (!available) return
      twitchClient.close()
   }
   private fun registerEvent() {
      twitchClient.pubSub.listenForChannelPointsRedemptionEvents(credential, Config().getData("channel"))
      twitchClient.eventManager.onEvent(RewardRedeemedEvent::class.java) { event: RewardRedeemedEvent ->
         if (event.redemption.reward.title.equals(Config().getData("chanelRewardName"))) {
            val userName = event.redemption.userInput
            val player = Bukkit.getOfflinePlayer(userName)
            if (player.usedWhitelist()) {
               twitchClient.chat.sendMessage(getChannelofID(Config().getData("channel")), String.format(Config().getData("alreadyWhitelistedOnePlayerResponseMessage"), userName, Config().getData("serverName")))
               TwitchWhitelist.instance.server.consoleSender.sendMessage("${ChatColor.YELLOW}User ${event.redemption.user.displayName} alreay Whitelisted one Player.")
               return@onEvent
            }
            if (!player.whitelist()) {
               twitchClient.chat.sendMessage(getChannelofID(Config().getData("channel")), String.format(Config().getData("alreadyWhitelistedResponseMessage"), userName, Config().getData("serverName")))
               TwitchWhitelist.instance.server.consoleSender.sendMessage("${ChatColor.YELLOW}Player $userName is already Whitelisted.")
               return@onEvent
            }
            Config().setData("alreadyWhitelisted.${event.redemption.user.displayName}", player.uniqueId.toString())
            TwitchWhitelist.instance.server.consoleSender.sendMessage("${ChatColor.GREEN}Added Player $userName to the Whitelist.")
            if (Config().getData("sendResponseMessage").toBoolean())
               twitchClient.chat.sendMessage(getChannelofID(Config().getData("channel")), String.format(Config().getData("successResponseMessage"), userName, Config().getData("serverName")))
         }
      }
   }
   fun getChannelofID(id: String): String = twitchClient.helix.getUsers(token, mutableListOf(id), null).execute().users.first().displayName
}
