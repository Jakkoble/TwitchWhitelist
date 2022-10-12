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
   private lateinit var userName: String
   private val credential = OAuth2Credential("twitch", chatToken)
   private lateinit var subscription: PubSubSubscription
   fun connect() {
      if (chatToken.length != 30 || channelID == "YourChannelID") {
         TwitchWhitelist.INSTANCE.server.consoleSender.sendMessage("")
         TwitchWhitelist.INSTANCE.server.consoleSender.sendMessage("${ChatColor.DARK_RED}You have not set the 'Bot Chat Token' or 'ChannelID' in the Config yet! Without these it will not work.")
         TwitchWhitelist.INSTANCE.server.consoleSender.sendMessage("${ChatColor.DARK_RED}To get the Access Token, visit: https://twitchtokengenerator.com/")
         TwitchWhitelist.INSTANCE.server.consoleSender.sendMessage("${ChatColor.DARK_RED}To get the ChannelID aka TwitchID, visit: https://www.streamweasels.com/tools/convert-twitch-username-to-user-id/")
         TwitchWhitelist.INSTANCE.server.consoleSender.sendMessage("${ChatColor.DARK_RED}Stop the Server, Setup the Config for your needs and Start the Server again.")
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
      userName = getChannelofID(channelID)
      if (!twitchClient.chat.isChannelJoined(userName)) twitchClient.chat.joinChannel(userName)
      registerEvent()
   }
   fun disconnect() {
      if (!available) return
      twitchClient.chat.leaveChannel(userName)
      twitchClient.pubSub.unsubscribeFromTopic(subscription)
      twitchClient.pubSub.disconnect()
      twitchClient.close()
   }
   private fun registerEvent() {
      subscription = twitchClient.pubSub.listenForChannelPointsRedemptionEvents(credential, channelID)
      twitchClient.eventManager.onEvent(RewardRedeemedEvent::class.java) { event: RewardRedeemedEvent ->
         if (event.redemption.reward.title.equals(channelRewardName)) {
            val playerName = (event.redemption.userInput ?: return@onEvent).replace(" ", "")
            val userID = event.redemption.user.id ?: return@onEvent
            val withSpecialCharacters = Pattern.compile("[^A-Za-z0-9_]").matcher(playerName).find()
            val userData = if (!withSpecialCharacters) playerName.getUserDataFromName() else null
            @Suppress("KotlinConstantConditions")
            if (userData == null || playerName.length > 25 || withSpecialCharacters) {
               if (sendMessage) twitchClient.chat.sendMessage(getChannelofID(channelID), String.format(
                  playerNotFoundMessage,
                  event.redemption.user.displayName,
                  playerName))
               TwitchWhitelist.INSTANCE.server.consoleSender.sendMessage("${ChatColor.YELLOW}There is no Player called $playerName.")
               return@onEvent
            }
            val uuid = userData.id
            if (Whitelist().usedWhitelist(userID)) {
               if (sendMessage) twitchClient.chat.sendMessage(getChannelofID(channelID), String.format(
                     alreadyWhitelistedOnePlayerMessage,
                     event.redemption.user.displayName,
                     if(ticketPerUser == 1) "one Player" else "$ticketPerUser Players",
                     serverName))
               TwitchWhitelist.INSTANCE.server.consoleSender.sendMessage("${ChatColor.YELLOW}User ${event.redemption.user.displayName} already Whitelisted one Player.")
               return@onEvent
            }
            if (!Whitelist().whitelist(UserData(playerName, uuid, userID))) {
               if (sendMessage) twitchClient.chat.sendMessage(
                  getChannelofID(channelID),
                  String.format(alreadyWhitelistedMessage, event.redemption.user.displayName, serverName))
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
   fun getChannelofID(id: String): String = twitchClient.helix.getUsers(chatToken, mutableListOf(id), null).execute().users.first().displayName
}