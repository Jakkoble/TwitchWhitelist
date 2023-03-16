package de.jakkoble

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential
import com.github.twitch4j.TwitchClient
import com.github.twitch4j.TwitchClientBuilder
import com.github.twitch4j.pubsub.PubSubSubscription
import com.github.twitch4j.pubsub.events.RewardRedeemedEvent
import org.bukkit.ChatColor
import java.util.regex.Pattern

class TwitchBot {
   private lateinit var twitchClient: TwitchClient
   var available = true
   private val credential = OAuth2Credential("twitch", chatToken)
   private lateinit var subscription: PubSubSubscription
   fun connect() {
      if (chatToken.length != 30 || channelID == "YourChannelID") {
         TwitchWhitelist.INSTANCE.server.consoleSender.sendMessage("")
         TwitchWhitelist.INSTANCE.server.consoleSender.sendMessage("${ChatColor.DARK_RED}You have not set the 'Bot Chat Token' or 'ChannelID' in the Config yet! Without these it will not work.")
         TwitchWhitelist.INSTANCE.server.consoleSender.sendMessage("${ChatColor.DARK_RED}To get the Access Token, visit: https://twitchtokengenerator.com/")
         TwitchWhitelist.INSTANCE.server.consoleSender.sendMessage("${ChatColor.DARK_RED}To get the ChannelID aka TwitchID, visit: https://www.streamweasels.com/tools/convert-twitch-username-to-user-id/")
         TwitchWhitelist.INSTANCE.server.consoleSender.sendMessage("${ChatColor.DARK_RED}Stop the Server, Setup the Config for your needs and Start the Server again. For more Information or Help visit the Plugin on GitHub: https://github.com/Jakkoble/TwitchWhitelist")
         TwitchWhitelist.INSTANCE.server.consoleSender.sendMessage("")
         available = false
         return
      }
      if (TwitchWhitelist.INSTANCE.server.onlineMode == offlineServer) {
         TwitchWhitelist.INSTANCE.server.consoleSender.sendMessage("")
         TwitchWhitelist.INSTANCE.server.consoleSender.sendMessage("${ChatColor.DARK_RED}Config Conflict Detected!")
         TwitchWhitelist.INSTANCE.server.consoleSender.sendMessage("${ChatColor.DARK_RED}The Server offlineServer (in config.yml) must be set to '${!offlineServer}'")
         TwitchWhitelist.INSTANCE.server.consoleSender.sendMessage("${ChatColor.DARK_RED}Reason: Property online-mode (in server.properties) is set to '${TwitchWhitelist.INSTANCE.server.onlineMode}'")
         TwitchWhitelist.INSTANCE.server.consoleSender.sendMessage("")
         available = false
         return
      }
      twitchClient = TwitchClientBuilder.builder()
         .withEnableChat(true)
         .withEnablePubSub(true)
         .withEnableHelix(true)
         .withChatAccount(credential)
         .build()
      ownerChannelName = getChannelofID(channelID)
      if (!twitchClient.chat.isChannelJoined(ownerChannelName)) twitchClient.chat.joinChannel(ownerChannelName)
      registerEvent()
      TwitchWhitelist.INSTANCE.server.consoleSender.sendMessage("")
      TwitchWhitelist.INSTANCE.server.consoleSender.sendMessage("${ChatColor.GREEN}Successfully connected to Twitch Channel ${ownerChannelName}!")
      TwitchWhitelist.INSTANCE.server.consoleSender.sendMessage("")
   }
   fun disconnect() {
      if (!available) return
      twitchClient.chat.leaveChannel(ownerChannelName)
      twitchClient.pubSub.unsubscribeFromTopic(subscription)
      twitchClient.pubSub.disconnect()
      twitchClient.close()
   }
   private fun registerEvent() {
      subscription = twitchClient.pubSub.listenForChannelPointsRedemptionEvents(credential, channelID)
      twitchClient.eventManager.onEvent(RewardRedeemedEvent::class.java) { event: RewardRedeemedEvent ->
         if (event.redemption.reward.title.equals(channelRewardName)) {
            val inputName = (event.redemption.userInput ?: return@onEvent).replace(" ", "")
            val userID = event.redemption.user.id ?: return@onEvent
            if (offlineServer) {
               handleOfflineServer(inputName, userID, event.redemption.user.displayName)
               return@onEvent
            }
            val withSpecialCharacters = Pattern.compile("[^A-Za-z0-9_]").matcher(inputName).find()
            val userData = if (!withSpecialCharacters) inputName.getUserDataFromName() else null
            @Suppress("KotlinConstantConditions")
            if (userData == null || inputName.length > 25 || withSpecialCharacters) {
               if (sendMessage) twitchClient.chat.sendMessage(getChannelofID(channelID), String.format(
                  playerNotFoundMessage,
                  event.redemption.user.displayName,
                  inputName))
               TwitchWhitelist.INSTANCE.server.consoleSender.sendMessage("${ChatColor.YELLOW}There is no Player called $inputName.")
               return@onEvent
            }
            val playerName = userData.name
            val uuid = userData.id
            if (Whitelist().usedWhitelist(userID)) {
               if (sendMessage) twitchClient.chat.sendMessage(getChannelofID(channelID), String.format(
                     tooManyPlayersWhitelistedMessage,
                     event.redemption.user.displayName,
                     serverName))
               TwitchWhitelist.INSTANCE.server.consoleSender.sendMessage("${ChatColor.YELLOW}User ${event.redemption.user.displayName} already Whitelisted too many Players.")
               return@onEvent
            }
            if (!Whitelist().whitelist(UserData(playerName, uuid, userID))) {
               if (sendMessage) twitchClient.chat.sendMessage(getChannelofID(channelID), String.format(alreadyWhitelistedMessage, event.redemption.user.displayName, serverName))
               TwitchWhitelist.INSTANCE.server.consoleSender.sendMessage("${ChatColor.YELLOW}Player $playerName is already Whitelisted.")
               return@onEvent
            }
            TwitchWhitelist.INSTANCE.server.consoleSender.sendMessage("${ChatColor.GREEN}Added Player $playerName to the Whitelist.")
            if (sendMessage) twitchClient.chat.sendMessage(
               getChannelofID(channelID),
               String.format(successMessage, event.redemption.user.displayName, serverName))
         }
      }
   }
   private fun handleOfflineServer(playerName: String, userID: String, twitchUserName: String) {
      if (Whitelist().usedWhitelist(userID)) {
         if (sendMessage) twitchClient.chat.sendMessage(getChannelofID(channelID), String.format(
            tooManyPlayersWhitelistedMessage,
            twitchUserName,
            serverName))
         TwitchWhitelist.INSTANCE.server.consoleSender.sendMessage("${ChatColor.YELLOW}User $twitchUserName already Whitelisted to many Players. (Offline Server)")
         return
      }
      if (!Whitelist().whitelist(UserData(
            name = playerName,
            uuid = "OFFLINEPLAYER",
            twitchUserID = userID
         ))) {
         if (sendMessage) twitchClient.chat.sendMessage(getChannelofID(channelID), String.format(alreadyWhitelistedMessage, twitchUserName, serverName))
         TwitchWhitelist.INSTANCE.server.consoleSender.sendMessage("${ChatColor.YELLOW}Player $playerName is already Whitelisted. (Offline Server)")
         return
      }
      TwitchWhitelist.INSTANCE.server.consoleSender.sendMessage("${ChatColor.GREEN}Added Player $playerName to the Whitelist. (Offline Server)")
      if (sendMessage) twitchClient.chat.sendMessage(getChannelofID(channelID), String.format(successMessage, twitchUserName, serverName))
      return
   }
   private fun getChannelofID(id: String): String = twitchClient.helix.getUsers(chatToken, mutableListOf(id), null).execute().users.first().displayName
}
