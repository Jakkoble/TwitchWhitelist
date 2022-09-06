package de.jakkoble

import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class WhitelistCommand : CommandExecutor, TabCompleter {
   override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
      if (args.isEmpty()) {
         sender.sendUsage()
         return false
      }
      when(args[0]) {
         "on" -> {
            if (Config().getData("enabled").toBoolean()) {
               sender.sendMessage("${prefix}TwitchWhitelist is already turned on.")
               return false
            }
            Config().setData("enabled", true)
            sender.sendMessage("${prefix}You have enabled the TwitchWhitelist.")
            return false
         }
         "off" -> {
            if (!Config().getData("enabled").toBoolean()) {
               sender.sendMessage("${prefix}TwitchWhitelist is already turned off.")
               return false
            }
            Config().setData("enabled", false)
            sender.sendMessage("${prefix}You have disabled the TwitchWhitelist.")
            return false
         }
         "list" -> {
            sender.sendMessage("")
            if (Whitelist().playerNames().isEmpty()) sender.sendMessage("No Player is Whitelisted")
            else {
               val playerNames = Whitelist().playerNames()
               sender.sendMessage("${ChatColor.GOLD}There ${if(playerNames.size == 1) "is one Player" else "are ${playerNames.size} Players"} whitelisted.")
               val message = StringBuilder()
               playerNames.forEach { message.append(if (playerNames.indexOf(it) != playerNames.size - 1) "$it, " else it) }
               sender.sendMessage(message.substring(0))
            }
            sender.sendMessage("")
            return false
         }
      }
      if (args.size != 2) {
         sender.sendUsage()
         return false
      }
      when(args[0]) {
         "add" -> {
            val userData = args[1].getUserDataFromName()
            if (userData == null || args[1].length > 25) {
               sender.sendMessage("${prefix}There is no Player called ${args[1]}")
               TwitchWhitelist.instance.server.consoleSender.sendMessage("${ChatColor.YELLOW}There is no Player called ${args[1]}.")
               return false
            }
            val uuid = userData.id
            if (Whitelist().whitelist(UserData(
                  name = args[1],
                  uuid = uuid,
                  twitchUserID = Config().getData("channelID")))) {
               sender.sendMessage("${prefix}You have added ${args[1]} to the Whitelist.")
               TwitchWhitelist.instance.server.consoleSender.sendMessage("${ChatColor.GREEN}Added Player ${args[1]} to the Whitelist.")
            } else {
               sender.sendMessage("${prefix}The Player ${args[1]} is already Whitelisted.")
               TwitchWhitelist.instance.server.consoleSender.sendMessage("${ChatColor.YELLOW}Player ${args[1]} is already Whitelisted.")
            }
         }
         "remove" -> {
            val userData = args[1].getUserDataFromName()
            if (userData == null || args[1].length > 25) {
               TwitchWhitelist.instance.server.consoleSender.sendMessage("${ChatColor.YELLOW}There is no Player called ${args[1]}.")
               sender.sendMessage("${prefix}There is no Player called ${args[1]}")
               return false
            }
            val uuid = userData.id
            if (Whitelist().unwhitelist(uuid)) {
               sender.sendMessage("${prefix}You have removed ${args[1]} from the Whitelist.")
               TwitchWhitelist.instance.server.consoleSender.sendMessage("${ChatColor.RED}Player ${args[1]} removed from Whitelist.")
            } else {
               sender.sendMessage("${prefix}The Player ${args[1]} is not Whitelisted.")
               TwitchWhitelist.instance.server.consoleSender.sendMessage("${ChatColor.YELLOW}Player ${args[1]} is not Whitelisted.")
            }
         }
      }
      return false
   }

   private fun CommandSender.sendUsage() {
      sendMessage("")
      sendMessage("${ChatColor.GOLD}Usage")
      sendMessage("${ChatColor.WHITE}whitelist on")
      sendMessage("${ChatColor.WHITE}whitelist off")
      sendMessage("${ChatColor.WHITE}whitelist list")
      sendMessage("${ChatColor.WHITE}whitelist add playerName")
      sendMessage("${ChatColor.WHITE}whitelist remove playerName")
      sendMessage("")
   }

   override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
      val tabList = mutableListOf<String>()
      if (args.size == 1) {
         tabList.addAll(listOf("on", "off", "list", "add", "remove"))
         return tabList
      } else if (args.size == 2 && (args[0] == "remove"))
         tabList.addAll(Whitelist().playerNames())
      return tabList
   }
}