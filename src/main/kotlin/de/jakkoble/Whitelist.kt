package de.jakkoble

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

private val whitelist = mutableListOf<UserData>()
class Whitelist {
   private val whitelistFile = File("plugins/TwitchWhitelist/whitelist.json")
   init {
      if (!whitelistFile.exists()) {
         whitelistFile.createNewFile()
         whitelistFile.writeText(GsonBuilder().setPrettyPrinting().create().toJson(whitelist))
      }
   }
   fun load() = whitelist.addAll(GsonBuilder().setPrettyPrinting().create().fromJson(whitelistFile.readText(), object : TypeToken<List<UserData>>() {}.type))
   fun whitelist(userData: UserData): Boolean {
      if (isWhitelisted(userData.uuid, userData.name)) return false
      whitelist.add(userData)
      whitelistFile.writeText(GsonBuilder().setPrettyPrinting().create().toJson(whitelist))
      return true
   }
   fun unwhitelist(uuid: String, name: String): Boolean {
      if (!isWhitelisted(uuid, name)) return false
      whitelist.removeIf { it.uuid == uuid && it.name.equals(name, ignoreCase = true)}
      whitelistFile.writeText(GsonBuilder().setPrettyPrinting().create().toJson(whitelist))
      return true
   }
   fun isWhitelisted(uuid: String, name: String): Boolean = if (offlineServer) whitelist.any { it.name.equals(name, ignoreCase = true)}
      else whitelist.any { it.uuid == uuid.replace("-", "") }
   fun playerNames(): List<String> = whitelist.map { it.name }
   fun usedWhitelist(twitchUserID: String): Boolean {
      if (twitchUserID == channelID) return false
      return whitelist.count { it.twitchUserID == twitchUserID } >= ticketPerUser
   }
   fun userDataByName(name: String): UserData? = whitelist.firstOrNull { it.name.equals(name, ignoreCase = true) }
}
data class UserData(val name: String, val uuid: String, val twitchUserID: String)

fun String.getUserDataFromName(): Response? {
   val result = getRequest("https://api.mojang.com/users/profiles/minecraft/$this")
   if (result == null) TwitchWhitelist.INSTANCE.logger.warning("I am absolutely null")
   return result
}
data class Response(val name: String?, val id: String?)
fun getRequest(url: String): Response? {
   val client = HttpClient.newBuilder().build()
   val request = HttpRequest.newBuilder()
      .uri(URI.create(url))
      .build()
   val data = client.send(request, HttpResponse.BodyHandlers.ofString()).body() ?: null
   return Gson().fromJson(data, Response::class.java)
}