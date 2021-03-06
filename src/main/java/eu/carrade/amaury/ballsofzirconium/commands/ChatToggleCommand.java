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
package eu.carrade.amaury.ballsofzirconium.commands;

import eu.carrade.amaury.ballsofzirconium.BallsOfZirconium;
import eu.carrade.amaury.ballsofzirconium.teams.BoSTeam;
import eu.carrade.amaury.ballsofzirconium.utils.BoSUtils;
import fr.zcraft.quartzlib.components.commands.Command;
import fr.zcraft.quartzlib.components.commands.CommandException;
import fr.zcraft.quartzlib.components.commands.CommandInfo;
import fr.zcraft.quartzlib.components.i18n.I;
import org.bukkit.entity.Player;


@CommandInfo (name = "togglechat", usageParameters = "[team]")
public class ChatToggleCommand extends Command
{

    @Override
    protected void run() throws CommandException
    {
        if (!(sender instanceof Player))
        {
            sender.sendMessage(I.t("{ce}You can't send a team-message from the console."));
            return;
        }

        // /togglechat
        if (args.length == 0)
        {
            if (BallsOfZirconium.get().getTeamChatManager().toggleChatForPlayer((Player) sender))
            {
                sender.sendMessage(I.t("{cs}You are now chatting with your team only."));
            }
            else
            {
                sender.sendMessage(I.t("{cs}You are now chatting with everyone."));
            }
        }

        // /togglechat <another team>
        else
        {
            String teamName = BoSUtils.getStringFromCommandArguments(args, 0);
            BoSTeam team = BallsOfZirconium.get().getTeamsManager().getTeam(teamName);

            if (team != null)
            {
                if (BallsOfZirconium.get().getTeamChatManager().toggleChatForPlayer((Player) sender, team))
                {
                    sender.sendMessage(I.t("{cs}You are now chatting with the team {0}{cs}.", team.getDisplayName()));
                }
            }
            else
            {
                sender.sendMessage(I.t("{ce}This team does not exists."));
            }
        }
    }
}
