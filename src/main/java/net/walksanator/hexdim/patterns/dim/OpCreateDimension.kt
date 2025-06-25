package net.walksanator.hexdim.patterns.dim

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.misc.MediaConstants
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.walksanator.hexdim.HexxyDimensions
import net.walksanator.hexdim.iotas.PermissionStrings
import net.walksanator.hexdim.iotas.RoomIota

class OpCreateDimension : SpellAction {
    override val argc: Int = 3

    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        if (args[0] is DoubleIota) {
            if (args[1] is DoubleIota) {
                if (args[2] is DoubleIota) {
                    val cfg = HexxyDimensions.CONFIG
                    val x = (args[0] as DoubleIota).double.toInt().coerceIn(1,cfg.x_limit)
                    val y = (args[1] as DoubleIota).double.toInt().coerceIn(1,cfg.y_limit)
                    val z = (args[2] as DoubleIota).double.toInt().coerceIn(1,cfg.z_limit)
                    val cost = x*y*z*MediaConstants.QUENCHED_SHARD_UNIT/2
                    HexxyDimensions.logger.info("Allocating room %s %s %s by user".format(x,y,z, env.caster?.name))
                    return SpellAction.Result(
                        Spell(x,y,z),
                        cost,
                        listOf()
                    )
                }
                throw MishapInvalidIota(args[2],2, Text.literal("Excepted a double"))
            }
            throw MishapInvalidIota(args[1],1, Text.literal("Excepted a double"))
        }
        throw MishapInvalidIota(args[0],0, Text.literal("Excepted a double"))
    }

    class Spell(val x: Int, val y: Int, val z: Int) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            HexxyDimensions.logger.info("Reached unreachable!")
        }
        override fun cast(env: CastingEnvironment, image: CastingImage): CastingImage? {
            val stack = image.stack.toMutableList()
            val storage = HexxyDimensions.STORAGE.get()
            val room = storage.mallocRoom(Pair(x, z), y)
            val cfg = HexxyDimensions.CONFIG
            val caster = env.caster
            if (x == cfg.x_limit && y == cfg.y_limit && z == cfg.z_limit && caster != null) {
                val resloc = Identifier("hexdim", "how")
                caster.server.advancementLoader
                val adv = caster.server.advancementLoader.get(resloc)
                caster.advancementTracker.grantCriterion(adv, "requirement")
            }
            if (room != null) {
                val perms = List(PermissionStrings.field.size) { true }
                stack.add(RoomIota(
                    Pair(storage.all.size-1,room.key!!),
                    null,
                    perms)
                )
            }
            else {
                //TODO: make a mishap for failing to allocate room...
            }
            val result = image.copy(stack = stack)

            return result
        }

    }
}