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
package eu.carrade.amaury.BallsOfSteel.generation.postProcessing;

import com.sk89q.jnbt.CompoundTag;
import com.sk89q.jnbt.CompoundTagBuilder;
import com.sk89q.jnbt.ListTag;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.function.RegionFunction;
import com.sk89q.worldedit.function.RegionMaskingFilter;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.visitor.RegionVisitor;
import com.sk89q.worldedit.world.block.BaseBlock;
import eu.carrade.amaury.BallsOfSteel.generation.utils.WorldEditUtils;
import fr.zcraft.quartzlib.components.i18n.I;
import fr.zcraft.quartzlib.tools.PluginLogger;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RandomSpawnerPostProcessor extends PostProcessor
{
    private final List<EntityType> spawnerEntityTypes = new ArrayList<>();

    private final Short spawnCount;
    private final Short spawnRange;
    private final Short minSpawnDelay;
    private final Short maxSpawnDelay;
    private final Short maxNearbyEntities;
    private final Short requiredPlayerRange;


    public RandomSpawnerPostProcessor(Map<?, ?> parameters)
    {
        super(parameters);

        spawnCount = getValue(parameters, "spawn_count", Short.class, null);
        spawnRange = getValue(parameters, "spawn_range", Short.class, null);
        minSpawnDelay = getValue(parameters, "min_spawn_delay", Short.class, null);
        maxSpawnDelay = getValue(parameters, "max_spawn_delay", Short.class, null);
        maxNearbyEntities = getValue(parameters, "max_nearby_entities", Short.class, null);
        requiredPlayerRange = getValue(parameters, "required_player_range", Short.class, null);


        final List<String> rawEntityTypes = getValue(parameters, "entities", List.class, Collections.emptyList());

        for (final String rawType : rawEntityTypes)
        {
            EntityType entity;
            final String type = rawType.replace(' ', '_');

            try
            {
                entity = EntityType.valueOf(type.toUpperCase());
            }
            catch (IllegalArgumentException e)
            {
                entity = EntityType.fromName(type.toLowerCase());
            }

            if (entity != null && entity.getName() != null) spawnerEntityTypes.add(entity);
        }
    }

    @Override
    protected void doProcess() throws WorldEditException
    {
        final RegionFunction randomizeSpawners = position -> {
            final EntityType spawnerEntityType = spawnerEntityTypes.get(random.nextInt(spawnerEntityTypes.size()));

            final BaseBlock block = session.getFullBlock(position);
            final CompoundTagBuilder nbt = (block.hasNbtData() ? block.getNbtData() : new CompoundTag(new HashMap<>())).createBuilder();

            nbt.put("SpawnData", new CompoundTag(new HashMap<>()).createBuilder()
                            .putString("id", spawnerEntityType.getKey().toString())
                            .build()
            );

            if (spawnCount != null) nbt.putShort("SpawnCount", spawnCount);
            if (spawnRange != null) nbt.putShort("SpawnRange", spawnRange);
            if (minSpawnDelay != null) nbt.putShort("MinSpawnDelay", (short) (minSpawnDelay * 20));
            if (maxSpawnDelay != null) nbt.putShort("MaxSpawnDelay", (short) (maxSpawnDelay * 20));
            if (maxNearbyEntities != null) nbt.putShort("MaxNearbyEntities", maxNearbyEntities);
            if (requiredPlayerRange != null) nbt.putShort("RequiredPlayerRange", requiredPlayerRange);

            // Ensure the spawner is properly randomized
            nbt.putShort("Delay", (short) -1);

            // Erases the SpawnPotentials key if present from a schematic or something,
            // as it will modifies the spawned entity.
            nbt.put("SpawnPotentials", new ListTag(CompoundTag.class, Collections.emptyList()));

            block.setNbtData(nbt.build());
            session.setBlock(position, block);

            return true;
        };

        final Mask blocksMask = WorldEditUtils.parseMask(session.getWorld(), "mob_spawner", session);
        final RegionVisitor blocksVisitor = new RegionVisitor(region, new RegionMaskingFilter(blocksMask, randomizeSpawners));

        try
        {
            Operations.complete(blocksVisitor);
        }
        catch (WorldEditException e)
        {
            PluginLogger.info("Unable to randomize monster spawners", e);
        }
    }

    @Override
    public String doName()
    {
        return I.t("Spawners randomization");
    }

    @Override
    public List<String> doSettingsDescription()
    {
        final List<String> entitiesNames = new ArrayList<>(spawnerEntityTypes.size());
        final List<String> settings = new ArrayList<>(7);

        for (final EntityType type : spawnerEntityTypes) entitiesNames.add(type.getName());

        settings.add(I.t("Entities: {0}", StringUtils.join(entitiesNames, ", ")));

        if (spawnCount != null) settings.add(I.t("Spawn count: {0}", spawnCount));
        if (spawnRange != null) settings.add(I.t("Spawn range: {0} blocks", spawnRange));
        if (minSpawnDelay != null) settings.add(I.t("Minimum spawn delay: {0} seconds", minSpawnDelay));
        if (maxSpawnDelay != null) settings.add(I.t("Maximum spawn delay: {0} seconds", maxSpawnDelay));
        if (maxNearbyEntities != null) settings.add(I.t("Max nearby entities: {0}", maxNearbyEntities));
        if (requiredPlayerRange != null) settings.add(I.t("Required player range: {0} blocks", requiredPlayerRange));

        return settings;
    }
}
