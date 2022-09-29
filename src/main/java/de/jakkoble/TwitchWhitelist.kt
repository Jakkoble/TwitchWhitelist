package de.jakkoble

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.plugin.java.JavaPlugin

class TwitchWhitelist : JavaPlugin(), Listener {
   companion object {
      lateinit var INSTANCE: TwitchWhitelist
   }
   private lateinit var twitchBot: TwitchBot
   override fun onEnable() {
      INSTANCE = this
      Config().load()
      Whitelist().load()

      twitchBot = TwitchBot()
      twitchBot.connect()

      server.pluginManager.registerEvents(this, this)
      getCommand("whitelist").executor = WhitelistCommand()
      getCommand("whitelist").tabCompleter = WhitelistCommand()

      if (enabled && Bukkit.hasWhitelist()) Bukkit.setWhitelist(false)
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
      println(event.uniqueId.toString())
      if (!config.getBoolean("enabled") || Whitelist().isWhitelisted(event.uniqueId.toString())) return
      event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_FULL, String.format(notWhitelistedText,
         "https://twitch.tv/${twitchBot.getChannelofID(channelID).lowercase()}"))
   }
}