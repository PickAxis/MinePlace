package com.pickaxis.place;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
@Setter( AccessLevel.PRIVATE )
public class MinePlacePlugin extends JavaPlugin
{
    @Getter
    @Setter( AccessLevel.PRIVATE )
    private static MinePlacePlugin instance;
    
    private Properties buildInfo;
    
    private boolean debug;
    
    public MinePlacePlugin()
    {
        MinePlacePlugin.setInstance( this );
    }
    
    @Override
    public void onEnable()
    {
        this.setBuildInfo( new Properties() );
        try
        {
            this.getBuildInfo().load( this.getClass().getClassLoader().getResourceAsStream( "git.properties" ) );
        }
        catch( IOException ex )
        {
            this.getLogger().log( Level.WARNING, "Couldn't load build info.", ex );
        }
        
        this.setDebug( this.getConfig().getBoolean( "debug", false ) );
        
        this.getCommand( "mineplace" ).setExecutor( new MinePlaceCommand() );
        
        new PaintBlocksTask().runTaskTimer( this, 1, 1 );
    }
    
    @Override
    public void onDisable()
    {
        HandlerList.unregisterAll( this );
    }    
}
