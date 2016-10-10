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
package eu.carrade.amaury.BallsOfSteel.generation.generation;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.EllipsoidRegion;
import eu.carrade.amaury.BallsOfSteel.BallsOfSteel;
import eu.carrade.amaury.BallsOfSteel.MapConfig;
import eu.carrade.amaury.BallsOfSteel.generation.GenerationManager;
import eu.carrade.amaury.BallsOfSteel.generation.GenerationProcess;
import eu.carrade.amaury.BallsOfSteel.generation.utils.GeometryUtils;
import fr.zcraft.zlib.tools.PluginLogger;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

import java.util.Random;


public class BallPopulator extends BlockPopulator
{
    private final GenerationManager generationManager = BallsOfSteel.get().getGenerationManager();

    @Override
    public void populate(World world, Random random, Chunk chunk)
    {
        final long time = System.currentTimeMillis();

        final GenerationManager generationManager = BallsOfSteel.get().getGenerationManager();

        final int yMin = generationManager.getLowestCorner().getBlockY();
        final int yMax = generationManager.getHighestCorner().getBlockY();

        final Location base = new Location(world, (chunk.getX() << 4) + random.nextInt(16), random.nextInt(yMax - yMin) + yMin, (chunk.getZ() << 4) + random.nextInt(16));
        if (!this.generationManager.isInsideBoundaries(base))
            return;

        final Vector baseVector = BukkitUtil.toVector(base);
        final com.sk89q.worldedit.world.World worldEditWorld = BukkitUtil.getLocalWorld(world);

        final Integer spheresFreeDistance = MapConfig.GENERATION.MAP.DISTANCE_BETWEEN_SPHERES.get();
        final Integer buildingsFreeDistance = MapConfig.GENERATION.MAP.DISTANCE_BETWEEN_SPHERES_AND_STATIC_BUILDINGS.get();

        final EditSession session = WorldEdit.getInstance().getEditSessionFactory().getEditSession(worldEditWorld, -1);

        final EllipsoidRegion noSpheresRegion = new EllipsoidRegion(worldEditWorld, baseVector, new Vector(spheresFreeDistance, spheresFreeDistance, spheresFreeDistance));
        final EllipsoidRegion noBuildingsRegion = new EllipsoidRegion(worldEditWorld, baseVector, new Vector(buildingsFreeDistance, buildingsFreeDistance, buildingsFreeDistance));

        session.enableQueue();
        session.setFastMode(false);


        // We check if any sphere is too close
        for (final Vector nearChunk : noSpheresRegion.getChunkCubes())
        {
            final ChunkSnapshot snapshot = world.getChunkAt(nearChunk.getBlockX(), nearChunk.getBlockZ()).getChunkSnapshot(false, false, false);
            if (!snapshot.isSectionEmpty(Math.min(Math.max(nearChunk.getBlockY(), 0), 15)))
                return;
        }

        // We also check if this position is correct regarding the static buildings private zones
        for (CuboidRegion privateBuildingRegion : generationManager.getBuildingsPrivateRegions())
            if (GeometryUtils.intersects(privateBuildingRegion, noBuildingsRegion))
                return;


        final GenerationProcess process = this.generationManager.getRandomGenerationProcess(random);

        process.applyAt(base, random, session);

        // Ensures all the blocks are wrote, as the populator checks for other spheres existence to
        // generate (or not).
        session.flushQueue();


        if (this.generationManager.isLogged())
            PluginLogger.info("Sphere {0} generated at {1} in {2} ms", process.getName(), base.toVector(), System.currentTimeMillis() - time);
    }
}
