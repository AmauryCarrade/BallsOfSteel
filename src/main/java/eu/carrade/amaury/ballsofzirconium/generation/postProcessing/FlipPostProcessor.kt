/*
 * Copyright or © or Copr. AmauryCarrade (2015)
 * 
 * http://amaury.carrade.eu
 * 
 * This software is governed by the CeCILL-B license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
 * modify and/ or redistribute the software under the terms of the CeCILL-B
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info". 
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability. 
 * 
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or 
 * data to be ensured and,  more generally, to use and operate it in the 
 * same conditions as regards security. 
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-B license and that you accept its terms.
 */
package eu.carrade.amaury.ballsofzirconium.generation.postProcessing

import com.sk89q.worldedit.WorldEditException
import com.sk89q.worldedit.math.transform.AffineTransform
import com.sk89q.worldedit.util.Direction
import eu.carrade.amaury.ballsofzirconium.generation.utils.GenerationConfig
import eu.carrade.amaury.ballsofzirconium.generation.utils.WorldEditUtils.applyTransform
import fr.zcraft.quartzlib.components.i18n.I

class FlipPostProcessor(parameters: Map<*, *>) : PostProcessor(parameters) {
    private val direction: FlipDirection
    @Throws(WorldEditException::class)
    override fun doProcess() {
        val weDirection = getDirection(direction) ?: return
        applyTransform(session!!, region!!, AffineTransform().scale(weDirection.toVector().abs().multiply(-2.0).add(1.0, 1.0, 1.0)))
    }

    override fun doName(): String {
        return I.t("Flip")
    }

    override fun doSettingsDescription(): List<String> {
        return listOf(
                I.t("Direction: {0}", direction.toString().toLowerCase().replace('_', ' '))
        )
    }

    /**
     * Returns a [Direction] from our internal [FlipDirection], generating a random
     * direction if needed.
     *
     * @param direction The direction.
     * @return A [Direction], or `null` if no transformation should be applied.
     */
    private fun getDirection(direction: FlipDirection): Direction? {
        val availableDirections: Array<Direction?>
        availableDirections = when (direction) {
            FlipDirection.UP, FlipDirection.DOWN, FlipDirection.EAST, FlipDirection.WEST, FlipDirection.NORTH, FlipDirection.SOUTH -> return Direction.valueOf(direction.name)
            FlipDirection.RANDOM -> arrayOf(null, Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH)
            FlipDirection.RANDOM_HORIZONTAL -> arrayOf(null, Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH)
            FlipDirection.RANDOM_VERTICAL -> arrayOf(null, Direction.UP, Direction.DOWN)
            else -> arrayOf(null)
        }
        return availableDirections[random!!.nextInt(availableDirections.size)]
    }

    /**
     * The flip direction to use
     */
    enum class FlipDirection {
        UP, DOWN, EAST, WEST, NORTH, SOUTH, RANDOM, RANDOM_HORIZONTAL, RANDOM_VERTICAL
    }

    init {
        direction = GenerationConfig.getValue<FlipDirection>(parameters, "direction", FlipDirection::class.java, FlipDirection.RANDOM)
    }
}