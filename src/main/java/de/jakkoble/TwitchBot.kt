package de.jakkoble

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential
import com.github.twitch4j.TwitchClient
import com.github.twitch4j.TwitchClientBuilder
import com.github.twitch4j.pubsub.events.RewardRedeemedEvent
import org.bukkit.ChatColor

class TwitchBot {
   private lateinit var twitchClient: TwitchClient
   private var available = true
   private lateinit var userName: String
   private val token = chatToken
   private val credential = OAuth2Credential("twitch", token)
   fun connect() {
      if (token.length != 30 || channelID == "YourChannelID") {
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
      twitchClient.close()
   }
   private fun registerEvent() {
      twitchClient.pubSub.listenForChannelPointsRedemptionEvents(credential, channelID)
      twitchClient.eventManager.onEvent(RewardRedeemedEvent::class.java) { event: RewardRedeemedEvent ->
         if (event.redemption.reward.title.equals(channelRewardName)) {
            val playerName = event.redemption.userInput ?: return@onEvent
            val userID = event.redemption.user.id ?: return@onEvent
            val userData = playerName.getUserDataFromName()
            if (userData == null || playerName.length > 25) {
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
   fun getChannelofID(id: String): String = twitchClient.helix.getUsers(token, mutableListOf(id), null).execute().users.first().displayName
}