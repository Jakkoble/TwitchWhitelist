package de.jakkoble

import java.io.File

class Config {
   init {
      if (File("plugins\\TwitchWhitelist\\config").exists()) {
         val config = TwitchWhitelist.instance.config
         config.set("token", "YourToken")
         config.set("channel", "Jakkoble")
         config.set("serverName", "Minecraft Community SMP")
         config.set("chanelRewardName", "Minecraft Server Access")
         config.set("sendResponseMessage", true)
         config.set("responseMessage", "%s you are now Whitelisted on the %s. // %s for twitchUserName and second %s for serverName")
         config.set("notWhitelistedMessage", "&aYou are not Whitelisted on this Server!")
         TwitchWhitelist.instance.saveConfig()
      }
   }
   fun getData(path: String): String = TwitchWhitelist.instance.config.getString(path) ?: "Not found"
   fun setData(path: String, value: Any) {
      TwitchWhitelist.instance.config.set(path, value)
      TwitchWhitelist.instance.saveConfig()
   }
}