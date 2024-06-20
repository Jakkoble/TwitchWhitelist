package de.jakkoble

import java.io.File
enum class ConfigEntry(val path: String) {
   ENABLED("enabled"),
   TOKEN("token"),
   CHANNEL_ID("channelID"),
   OFFLINE_SERVER("offlineServer"),
   TICKETS_PER_USER("ticketsPerUser"),
   SERVER_NAME("serverName"),
   CHANNEL_REWARD_NAME("channelRewardName"),
   SEND_MESSAGE("sendMessage"),
   SUCCESS_MESSAGE("successMessage"),
   PLAYER_NOT_FOUND_MESSAGE("playerNotFoundMessage"),
   ALREADY_WHITELISTED_MESSAGE("alreadyWhitelistedMessage"),
   TOO_MANY_PLAYERS_WHITELISTED_MESSAGE("tooManyPlayersWhitelisted"),
   NOT_WHITELISTED_TEXT("notWhitelistedMessage")
}

class Config {
   init {
      if (!File("plugins/TwitchWhitelist/config.yml").exists()) {
         val config = TwitchWhitelist.INSTANCE.config
         config.set(ConfigEntry.ENABLED.path, true)
         config.set(ConfigEntry.TOKEN.path, "YourToken")
         config.set(ConfigEntry.CHANNEL_ID.path, "YourChannelID")
         config.set(ConfigEntry.OFFLINE_SERVER.path, false)
         config.set(ConfigEntry.TICKETS_PER_USER.path, 1)
         config.set(ConfigEntry.SERVER_NAME.path, "Minecraft Community SMP")
         config.set(ConfigEntry.CHANNEL_REWARD_NAME.path, "Minecraft Whitelist Ticket")
         config.set(ConfigEntry.SEND_MESSAGE.path, true)
         config.set(ConfigEntry.SUCCESS_MESSAGE.path, "/me %s, you are now Whitelisted on the %s.")
         config.set(ConfigEntry.PLAYER_NOT_FOUND_MESSAGE.path, "/me %s, there is no Player called %s.")
         config.set(ConfigEntry.ALREADY_WHITELISTED_MESSAGE.path, "/me %s, you are already Whitelisted on the %s.")
         config.set(ConfigEntry.TOO_MANY_PLAYERS_WHITELISTED_MESSAGE.path, "/me %s, you have already Whitelisted to many Players to the %s.")
         config.set(ConfigEntry.NOT_WHITELISTED_TEXT.path, "§cYou are not Whitelisted on this Server! Purchase a Whitelist Slot via Channel Points Reward at %s.")
         TwitchWhitelist.INSTANCE.saveConfig()
      }
   }
   fun load() {
      enabled = TwitchWhitelist.INSTANCE.config.getBoolean(ConfigEntry.ENABLED.path)
      chatToken = getString(ConfigEntry.TOKEN)
      channelID = getString(ConfigEntry.CHANNEL_ID)
      offlineServer = TwitchWhitelist.INSTANCE.config.getBoolean(ConfigEntry.OFFLINE_SERVER.path)
      ticketPerUser = TwitchWhitelist.INSTANCE.config.getInt(ConfigEntry.TICKETS_PER_USER.path)
      serverName = getString(ConfigEntry.SERVER_NAME)
      channelRewardName = getString(ConfigEntry.CHANNEL_REWARD_NAME)
      sendMessage = TwitchWhitelist.INSTANCE.config.getBoolean(ConfigEntry.SEND_MESSAGE.path)
      successMessage = getString(ConfigEntry.SUCCESS_MESSAGE)
      playerNotFoundMessage = getString(ConfigEntry.PLAYER_NOT_FOUND_MESSAGE)
      alreadyWhitelistedMessage = getString(ConfigEntry.ALREADY_WHITELISTED_MESSAGE)
      tooManyPlayersWhitelistedMessage = getString(ConfigEntry.TOO_MANY_PLAYERS_WHITELISTED_MESSAGE)
      notWhitelistedText = getString(ConfigEntry.NOT_WHITELISTED_TEXT)
   }
   private fun getString(entry: ConfigEntry): String = TwitchWhitelist.INSTANCE.config.getString(entry.path) ?: "Content not Found"
   fun setData(entry: ConfigEntry, value: Any) {
      TwitchWhitelist.INSTANCE.config.set(entry.path, value)
      TwitchWhitelist.INSTANCE.saveConfig()
   }
}