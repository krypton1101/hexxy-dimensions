package net.walksanator.hexdim.patterns

import at.petrak.hexcasting.api.casting.ActionRegistryEntry
import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.castables.OperationAction
import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.math.HexPattern
import net.minecraft.util.Identifier
import net.walksanator.hexdim.patterns.dim.*
import net.walksanator.hexdim.HexxyDimensions.modLoc
import java.util.function.BiConsumer


object DimPatternRegistry {

    private val ACTIONS: MutableMap<Identifier, ActionRegistryEntry> = mutableMapOf()

    @JvmStatic
    fun registerPatterns(r: BiConsumer<ActionRegistryEntry, Identifier>) {
        for ((key, value) in ACTIONS.entries) {
            r.accept(value, key)
        }
    }

    val DIM_CREATE = make("dim/create", HexPattern.fromAngles("wawdwawawdwawawdwewdwqwdwqwdwqwdwqwdwqwdw", HexDir.SOUTH_WEST), OpCreateDimension()) //
    val DIM_KIDNAP = make("dim/kidnap", HexPattern.fromAngles("wawewawewawewawewawewawwwqwqwqwqwqwaeqqqqqaww", HexDir.SOUTH_WEST), OpKidnap())
    val DIM_BANISH = make("dim/kick", HexPattern.fromAngles("wwdeeeeeqdwewewewewewwwdwqwdwqwdwqwdwqwdwqwdw", HexDir.EAST), OpBanish()) //
    val DIM_KEY = make("dim/pos/set", HexPattern.fromAngles("awqwawqdqawwwaq", HexDir.SOUTH_EAST), OpDimSetPos()) //
    val DIM_POSKEY = make("dim/perm/remove", HexPattern.fromAngles("dewedaewdwewd", HexDir.EAST), OpDimStripPermission()) //
    val DIM_TORELATIVE = make("dim/rel/to", HexPattern.fromAngles("adeeda", HexDir.EAST), OpDimRelative(true)) //
    val DIM_FROMRELATIVE = make("dim/rel/from", HexPattern.fromAngles("daqqad", HexDir.NORTH_EAST), OpDimRelative(false)) //
    val DIM_ACTIVATE = make("dim/cast/activate", HexPattern.fromAngles("deaqqeweeeeewdqdqdqdqdq", HexDir.SOUTH_EAST), OpDimExecute(true)) //
    val DIM_DEACTIVATE = make("dim/cast/deactivate", HexPattern.fromAngles("aqdeeqeaeaeaeaeae", HexDir.SOUTH_WEST), OpDimExecute(false)) //
    val DIM_CARVED = make("dim/carved", HexPattern.fromAngles("qqqqqwaeaeaeaeaeadwaqaeaq", HexDir.NORTH_WEST), OpDimCarved()) //
    val DIM_ESTIMATE_TIME = make("dim/time", HexPattern.fromAngles("qqqqqwaeaeaeaeaeadqwdwqwdwdwqw", HexDir.NORTH_EAST), OpEstimateTime())


    fun make(name: String, pattern: HexPattern, action: Action): ActionRegistryEntry = make(name, ActionRegistryEntry(pattern, action))

    fun make(name: String, are: ActionRegistryEntry): ActionRegistryEntry {
        return if (ACTIONS.put(modLoc(name), are) != null) {
            throw IllegalArgumentException("Typo? Duplicate id $name")
        } else {
            are
        }
    }

    fun make(name: String, oa: OperationAction): ActionRegistryEntry {
        val are = ActionRegistryEntry(oa.pattern, oa)
        return if (ACTIONS.put(modLoc(name), are) != null) {
            throw IllegalArgumentException("Typo? Duplicate id $name")
        } else {
            are
        }
    }
}