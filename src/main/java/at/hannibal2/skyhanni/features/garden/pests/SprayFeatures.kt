package at.hannibal2.skyhanni.features.garden.pests

import at.hannibal2.skyhanni.events.GuiRenderEvent
import at.hannibal2.skyhanni.events.LorenzChatEvent
import at.hannibal2.skyhanni.features.garden.pests.PestAPI.getPests
import at.hannibal2.skyhanni.utils.RenderUtils.renderString
import at.hannibal2.skyhanni.utils.SimpleTimeMark
import at.hannibal2.skyhanni.utils.StringUtils.matchMatcher
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.time.Duration.Companion.seconds

class SprayFeatures {
    private val config get() = PestAPI.config.spray

    private var display: String? = null
    private var lastChangeTime = SimpleTimeMark.farPast()

    @SubscribeEvent
    fun onChat(event: LorenzChatEvent) {
        if (!config.pestWhenSelector) return

        val pattern = "§a§lSPRAYONATOR! §r§7Your selected material is now §r§a(?<spray>.*)§r§7!".toPattern()

        val type = pattern.matchMatcher(event.message) {
            val sprayName = group("spray")
            SprayType.getByName(sprayName) ?: error("unknown spray: '$sprayName'")
        } ?: return

        val pests = type.getPests().joinToString("§7, ", prefix = "§6") { it.displayName }
        display = "§a${type.displayName} §7($pests§7)"

        lastChangeTime = SimpleTimeMark.now()

    }

    @SubscribeEvent
    fun onRenderOverlay(event: GuiRenderEvent.GuiOverlayRenderEvent) {
        if (!config.pestWhenSelector) return

        val display = display ?: return

        if (lastChangeTime.passedSince() > 5.seconds) {
            this.display = null
            return
        }

        config.position.renderString(display, posLabel = "Pest Spray Selector")
    }
}