package de.jakkoble

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential
import com.github.twitch4j.TwitchClient
import com.github.twitch4j.TwitchClientBuilder
import com.github.twitch4j.pubsub.events.RewardRedeemedEvent
import de.jakkoble.Whitelist.whitelist
import kotlinx.coroutines.runBlocking
import org.bukkit.Bukkit
import java.util.logging.Level

class TwitchBot {
   private lateinit var twitchClient: TwitchClient
   private val credential = OAuth2Credential("twitch", TwitchWhitelist.config.getData("token"))
   fun connect(): Unit = runBlocking {
      twitchClient = TwitchClientBuilder.builder()
         .withEnableChat(true)
         .withEnablePubSub(true)
         .withEnableHelix(true)
         .withChatAccount(credential)
         .build()
      twitchClient.chat.joinChannel("Jakkoble")
      registerEvent()
   }
   fun disconnect() {
      twitchClient.close()
   }
   private fun registerEvent() = runBlocking {
      twitchClient.pubSub.listenForChannelPointsRedemptionEvents(credential, getChannelID(TwitchWhitelist.config.getData("Channel")))
      twitchClient.eventManager.onEvent(RewardRedeemedEvent::class.java) { event: RewardRedeemedEvent ->
         if (event.redemption.reward.title.equals("Spotify Songwunsch")) {
            val userName = event.redemption.userInput
            if (Bukkit.getOfflinePlayer(userName).whitelist()) {
               TwitchWhitelist.instance.logger.log(Level.WARNING, "Added Player $userName to the Whitelist.")
               if (TwitchWhitelist.config.getData("sendResponseMessage").toBoolean())
                  twitchClient.chat.sendMessage(getChannelID(TwitchWhitelist.config.getData("Channel")), TwitchWhitelist.config.getData("responseMessage"))
            }
         }
      }
   }
   private fun getChannelID(channelName: String): String = twitchClient.helix.getUsers(null, null, listOf(channelName)).queue().get().users.first().id ?: ""
}