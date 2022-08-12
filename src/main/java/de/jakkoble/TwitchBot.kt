package de.jakkoble

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential
import com.github.twitch4j.TwitchClient
import com.github.twitch4j.TwitchClientBuilder
import com.github.twitch4j.pubsub.events.RewardRedeemedEvent
import de.jakkoble.Whitelist.usedWhitelist
import de.jakkoble.Whitelist.whitelist
import org.bukkit.Bukkit
import org.bukkit.ChatColor

class TwitchBot {
   private lateinit var twitchClient: TwitchClient
   private var available = true
   private val token = Config().getData("token")
   private val credential = OAuth2Credential("twitch", token)
   fun connect() {
      if (token.length != 30 || Config().getData("channelID") == "YourChannelID") {
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
      val userName = getChannelofID(Config().getData("channelID"))
      if (!twitchClient.chat.isChannelJoined(userName)) twitchClient.chat.joinChannel(userName)
      registerEvent()
   }
   fun disconnect() {
      if (!available) return
      twitchClient.close()
   }
   private fun registerEvent() {
      twitchClient.pubSub.listenForChannelPointsRedemptionEvents(credential, Config().getData("channelID"))
      twitchClient.eventManager.onEvent(RewardRedeemedEvent::class.java) { event: RewardRedeemedEvent ->
         if (event.redemption.reward.title.equals(Config().getData("chanelRewardName"))) {
            val playerName = event.redemption.userInput

            val userID = event.redemption.user.id
            val player = Bukkit.getOfflinePlayer(playerName)
            val respond = Config().getData("sendResponseMessage").toBoolean()

            if (event.redemption.user.id.usedWhitelist()) {
               if (respond) twitchClient.chat.sendMessage(getChannelofID(Config().getData("channelID")), String.format(Config().getData("alreadyWhitelistedOnePlayerResponseMessage"), event.redemption.user.displayName, Config().getData("serverName")))
               TwitchWhitelist.instance.server.consoleSender.sendMessage("${ChatColor.YELLOW}User ${event.redemption.user.displayName} already Whitelisted one Player.")
               return@onEvent
            }

            if (!player.whitelist(userID)) {
               if (respond) twitchClient.chat.sendMessage(getChannelofID(Config().getData("channelID")), String.format(Config().getData("alreadyWhitelistedResponseMessage"), event.redemption.user.displayName, Config().getData("serverName")))
               TwitchWhitelist.instance.server.consoleSender.sendMessage("${ChatColor.YELLOW}Player $playerName is already Whitelisted.")
               return@onEvent
            }

            TwitchWhitelist.instance.server.consoleSender.sendMessage("${ChatColor.GREEN}Added Player $playerName to the Whitelist.")
            if (respond) twitchClient.chat.sendMessage(getChannelofID(Config().getData("channelID")), String.format(Config().getData("successResponseMessage"), event.redemption.user.displayName, Config().getData("serverName")))
         }
      }
   }
   fun getChannelofID(id: String): String = twitchClient.helix.getUsers(token, mutableListOf(id), null).execute().users.first().displayName
}
