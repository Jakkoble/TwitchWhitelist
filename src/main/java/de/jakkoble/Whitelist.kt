package de.jakkoble

import org.bukkit.OfflinePlayer

object Whitelist {
   fun OfflinePlayer.whitelist(): Boolean {
      if (this.isListed()) return false
      val whitelistedPlayer = TwitchWhitelist.instance.config.getStringList("whitelistedPlayer")
      whitelistedPlayer.add(this.uniqueId.toString())
      return true
   }
   fun OfflinePlayer.isListed(): Boolean = TwitchWhitelist.instance.config.getStringList("whitelistedPlayer").contains(this.uniqueId.toString())
   fun String.usedWhitelist(): Boolean {
      TwitchWhitelist.instance.config.getConfigurationSection("alreadyWhitelisted")?.getKeys(false)?.forEach {
         if (it.equals(this)) return true
      }
      return false
   }
}