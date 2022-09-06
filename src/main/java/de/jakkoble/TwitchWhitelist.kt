package de.jakkoble

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.plugin.java.JavaPlugin

val prefix = "${ChatColor.GOLD}Whitelist ${ChatColor.GRAY}> "
class TwitchWhitelist : JavaPlugin(), Listener {
   companion object {
      lateinit var instance: TwitchWhitelist
   }
   private lateinit var twitchBot: TwitchBot
   override fun onEnable() {
      instance = this
      twitchBot = TwitchBot()
      twitchBot.connect()

      Config()
      Whitelist().load()

      server.pluginManager.registerEvents(this, this)
      getCommand("whitelist").executor = WhitelistCommand()
      getCommand("whitelist").tabCompleter = WhitelistCommand()

      if (Config().getData("enabled").toBoolean() && Bukkit.hasWhitelist()) Bukkit.setWhitelist(false)
   }
   override fun onDisable() {
      twitchBot.disconnect()
   }
   override fun onLoad() {
      server.consoleSender.sendMessage("")
      server.consoleSender.sendMessage("")
      server.consoleSender.sendMessage("${ChatColor.LIGHT_PURPLE}TwitchWhitelist")
      server.consoleSender.sendMessage("")
      server.consoleSender.sendMessage("${ChatColor.DARK_PURPLE}Version: ${ChatColor.GRAY}${description.version}")
      server.consoleSender.sendMessage("${ChatColor.DARK_PURPLE}Website: ${ChatColor.GRAY}${description.website}")
      server.consoleSender.sendMessage("${ChatColor.DARK_PURPLE}Author:  ${ChatColor.GRAY}${description.authors.first()}")
      server.consoleSender.sendMessage("")
      server.consoleSender.sendMessage("")
   }
   @EventHandler
   fun onPlayerJoin(event: AsyncPlayerPreLoginEvent) {
      val isWhitelisted = event.name.getUserDataFromName()?.id?.let { Whitelist().isWhitelisted(it) }
      if (!config.getBoolean("enabled") || isWhitelisted == true) return
      event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_FULL, String.format(Config().getData("notWhitelistedMessage"),
         "https://twitch.tv/${twitchBot.getChannelofID(Config().getData("channelID")).lowercase()}"))
   }
}