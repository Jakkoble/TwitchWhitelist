package de.jakkoble

import java.io.File

class Config {
   init {
      if (!File("plugins/TwitchWhitelist/config.yml").exists()) {
         val config = TwitchWhitelist.instance.config
         config.set("enabled", true)
         config.set("token", "YourToken")
         config.set("channelID", "YourChannelID")
         config.set("ticketsPerUser", 1)
         config.set("serverName", "Minecraft Community SMP")
         config.set("chanelRewardName", "Minecraft Whitelist Ticket")
         config.set("sendResponseMessage", true)
         config.set("successResponseMessage", "/me %s, you are now Whitelisted on the '%s'.")
         config.set("noPlayerFoundResponseMessage", "/me %s, there is no Player called '%s'.")
         config.set("alreadyWhitelistedResponseMessage", "/me %s, you are already Whitelisted on the '%s'.")
         config.set("alreadyWhitelistedOnePlayerResponseMessage", "/me %s, you have already Whitelisted %s to the '%s'.")
         config.set("notWhitelistedMessage", "§cYou are not Whitelisted on this Server! Purchase a Whitelist Slot via Channel Points Reward at '%s'.")
         TwitchWhitelist.instance.saveConfig()
      }
   }
   fun getData(path: String): String = TwitchWhitelist.instance.config.getString(path) ?: "Content not Found"
   fun setData(path: String, value: Any) {
      TwitchWhitelist.instance.config.set(path, value)
      TwitchWhitelist.instance.saveConfig()
   }
}