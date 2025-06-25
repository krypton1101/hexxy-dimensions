package net.walksanator.hexdim.patterns.dim

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.EntityIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.misc.MediaConstants
import com.mojang.datafixers.util.Either
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions
import net.minecraft.entity.Entity
import net.minecraft.text.Text
import net.minecraft.util.math.Vec3d
import net.minecraft.world.TeleportTarget
import net.walksanator.hexdim.HexxyDimensions
import net.walksanator.hexdim.iotas.RoomIota
import net.walksanator.hexdim.casting.mishap.MishapInvalidRoom

class OpKidnap : SpellAction {
    override val argc: Int
        get() = 2

    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val mediacost: Long
        val room = args[0]
        if (room !is RoomIota) {throw MishapInvalidIota(room,1,Text.translatable("hexdim.iota.roomlike"))}
        if (!(room).permissions[0]) {throw MishapInvalidIota(room,1, Text.translatable("hexdim.iota.permissions.read"))}
        if (!room.getRoomValue().isDone) {throw MishapInvalidRoom(room.getRoomValue(), false)}
        val iota = args[1]

        val target: Either<EntityIota,ListIota> = when (iota.type) {
            ListIota.TYPE -> {
                val iotas = (iota as ListIota).list.filter { value -> value.type == EntityIota.TYPE }
                if (iotas.isEmpty()) {
                    throw MishapInvalidIota(iota, 1, Text.literal("List contains no entities"))
                }
                mediacost = MediaConstants.SHARD_UNIT * iota.list.size()
                Either.right(iota)
            }
            EntityIota.TYPE -> {
                mediacost = MediaConstants.SHARD_UNIT
                Either.left(iota as EntityIota)
            }
            else -> {
                throw MishapInvalidIota(iota, 1, Text.literal("Iota is not a list of entities or entity"))
            }
        }

        return SpellAction.Result(
            Spell(room,target),
            mediacost,
            listOf()
        )
    }

    private data class Spell(val room: RoomIota, val targets: Either<EntityIota, ListIota>) : RenderedSpell{
        override fun cast(env: CastingEnvironment) {
            targets.ifRight { iota ->
                val iotas = iota.list.filter { value -> value.type == EntityIota.TYPE }
                for (entity in iotas) {
                    val target = (entity as EntityIota).entity
                    env.assertEntityInRange(target)
                    kidnap(room.getTeleportPosition(), target)
                }
            }
            targets.ifLeft { iota ->
                val target = (iota as EntityIota).entity
                env.assertEntityInRange(target)
                kidnap(room.getTeleportPosition(), target)
            }
        }
    }
}

fun kidnap(pos: Vec3d, entity: Entity) {
    val storage = HexxyDimensions.STORAGE.get()
    FabricDimensions.teleport(
        entity,
        storage.world,
        TeleportTarget(
            pos,
            Vec3d.ZERO,
            0F,0F
        )
    )
}
