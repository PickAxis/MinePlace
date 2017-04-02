package com.pickaxis.place;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MinePlaceCommand implements CommandExecutor
{
    public static final String MESSAGE_PREFIX = ChatColor.DARK_AQUA + "[MinePlace] " + ChatColor.AQUA;
    
    public void message( CommandSender sender, String message )
    {
        sender.sendMessage( MinePlaceCommand.MESSAGE_PREFIX + message );
    }
    
    @Override
    public boolean onCommand( CommandSender sender, Command command, String label, String[] args )
    {
        MinePlacePlugin mp = MinePlacePlugin.getInstance();
        
        this.message( sender, "MinePlace v" + mp.getDescription().getVersion() + " (" +
                              mp.getBuildInfo().getProperty( "git.branch" ) + "/" +
                              mp.getBuildInfo().getProperty( "git.commit.id.describe" ) + ")" );

        String builtBy = "";
        if( sender.hasPermission( "mineplace.cmd.info.extended" ) )
        {
            builtBy = "by " + mp.getBuildInfo().getProperty( "git.build.user.name" );
        }
        this.message( sender, "Built " + builtBy + " on " +
                              mp.getBuildInfo().getProperty( "git.build.time" ) );

        if( sender.hasPermission( "mineplace.cmd.info.extended" ) )
        {
            this.message( sender, "Full Commit Hash: " + mp.getBuildInfo().getProperty( "git.commit.id" ) );
            this.message( sender, "Commit Message: " + mp.getBuildInfo().getProperty( "git.commit.message.short" ) );
            this.message( sender, "Committed by " + mp.getBuildInfo().getProperty( "git.commit.user.name" ) + " <" +
                                  mp.getBuildInfo().getProperty( "git.commit.user.email" ) + "> on " +
                                  mp.getBuildInfo().getProperty( "git.commit.time" ) );
        }
        
        return true;
    }
}
