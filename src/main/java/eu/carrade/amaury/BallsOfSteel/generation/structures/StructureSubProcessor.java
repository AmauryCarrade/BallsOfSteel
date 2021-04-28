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
package eu.carrade.amaury.BallsOfSteel.generation.structures;

import eu.carrade.amaury.BallsOfSteel.generation.generators.Generator;
import eu.carrade.amaury.BallsOfSteel.generation.postProcessing.PostProcessor;
import eu.carrade.amaury.BallsOfSteel.generation.utils.AbstractGenerationTool;
import org.apache.commons.lang.StringUtils;

import java.util.List;


public abstract class StructureSubProcessor extends AbstractGenerationTool
{
    /**
     * A name for the processor.
     *
     * @return The name.
     */
    public abstract String getName();

    /**
     * @return A list describing each setting of the processor.
     */
    public abstract List<String> getSettingsDescription();


    /**
     * @return The generator or post-processor identifier used in map.yml
     */
    public String getIdentifier()
    {
        String identifier;

        final Class processorType = this instanceof Generator ? Generator.class : (this instanceof PostProcessor ? PostProcessor.class : null);

        if (processorType == null)
        {
            identifier = getClass().getName();
        }
        else
        {
            if (getClass().getPackage().equals(processorType.getPackage()))
            {
                identifier = getClass().getSimpleName();
                if (identifier.endsWith(processorType.getSimpleName()))
                    identifier = identifier.substring(0, identifier.length() - processorType.getSimpleName().length());
            }
            else
            {
                identifier = getClass().getName();
            }
        }

        return StringUtils.uncapitalize(identifier);
    }
}