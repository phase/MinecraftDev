/*
 * Minecraft Dev for IntelliJ
 *
 * https://minecraftdev.org
 *
 * Copyright (c) 2017 minecraft-dev
 *
 * MIT License
 */

package com.demonwav.mcdev.creator

import com.demonwav.mcdev.platform.PlatformType
import com.demonwav.mcdev.util.fromJson
import com.demonwav.mcdev.util.gson
import org.jetbrains.concurrency.runAsync
import java.net.URL
import java.util.Arrays
import java.util.Objects
import javax.swing.JComboBox

private const val baseUrl = "https://minecraftdev.org/versions"
private const val bukkitUrl = "$baseUrl/bukkit.json"
private const val spigotUrl = "$baseUrl/spigot.json"
private const val paperUrl = "$baseUrl/paper.json"
private const val bungeecordUrl = "$baseUrl/bungeecord.json"
private const val waterfallUrl = "$baseUrl/waterfall.json"
private const val canaryUrl = "$baseUrl/canary.json"
private const val neptuneUrl = "$baseUrl/neptune.json"

fun getVersionSelector(type: PlatformType) = runAsync {
    val urlString = when (type) {
        PlatformType.BUKKIT -> bukkitUrl
        PlatformType.SPIGOT -> spigotUrl
        PlatformType.PAPER -> paperUrl
        PlatformType.BUNGEECORD -> bungeecordUrl
        PlatformType.WATERFALL -> waterfallUrl
        PlatformType.CANARY -> canaryUrl
        PlatformType.NEPTUNE -> neptuneUrl
        else -> throw UnsupportedOperationException("Incorrect platform type: $type")
    }

    val url = URL(urlString)
    val connection = url.openConnection()
    connection.setRequestProperty(
        "User-Agent",
        "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2"
    )
    val text = connection.getInputStream().use { it.reader().use { it.readText() } }
    gson.fromJson<PlatformVersion>(text)
}

data class PlatformVersion(var versions: Array<String>, var selectedIndex: Int) {

    fun set(combo: JComboBox<String>) {
        combo.removeAllItems()
        for (version in this.versions) {
            combo.addItem(version)
        }
        combo.selectedIndex = this.selectedIndex
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (other !is PlatformVersion) {
            return false
        }

        return Arrays.equals(this.versions, other.versions) && this.selectedIndex == other.selectedIndex
    }

    override fun hashCode(): Int {
        return Objects.hash(*versions, selectedIndex)
    }
}
