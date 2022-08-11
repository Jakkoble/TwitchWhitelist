package de.jakkoble

import org.bukkit.OfflinePlayer

object Whitelist {
   fun OfflinePlayer.whitelist(): Boolean {
      val whitelistedPlayer = TwitchWhitelist.instance.config.getStringList("whitelistedPlayer")
      whitelistedPlayer.add(this.uniqueId.toString())
      Config().setData("whitelistedPlayer", whitelistedPlayer)
      return true
   }
   fun OfflinePlayer.unwhitelist(): Boolean {
      if (!this.isListed()) return false
      val whitelistedPlayer = TwitchWhitelist.instance.config.getStringList("whitelistedPlayer")
      whitelistedPlayer.remove(this.uniqueId.toString())
      Config().setData("whitelistedPlayer", whitelistedPlayer)
      return true
   }
   fun OfflinePlayer.isListed(): Boolean = TwitchWhitelist.instance.config.getStringList("whitelistedPlayer").contains(this.uniqueId.toString())
}