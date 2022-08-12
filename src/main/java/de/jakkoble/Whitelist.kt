package de.jakkoble

import org.bukkit.OfflinePlayer

object Whitelist {
   fun OfflinePlayer.whitelist(userID: String): Boolean {
      if (this.isListed()) return false
      val id = if (userID == Config().getData("channelID"))
         TwitchWhitelist.instance.config.getConfigurationSection("whitelistedPlayer")?.getKeys(false)?.maxOfOrNull { it.toInt() }?.plus(1) ?: 0
      else userID
      Config().setData("whitelistedPlayer.$id", this.uniqueId.toString())
      return true
   }
   fun OfflinePlayer.isListed(): Boolean = TwitchWhitelist.instance.config.getConfigurationSection("whitelistedPlayer")?.getValues(false)?.values?.contains(this.uniqueId.toString()) ?: false
   fun String.usedWhitelist(): Boolean {
      TwitchWhitelist.instance.config.getConfigurationSection("whitelistedPlayer")?.getKeys(false)?.forEach {
         if (it.equals(this)) return true
      }
      return false
   }
}