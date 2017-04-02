package com.pickaxis.place;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

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
        
        this.paintInitialImage();
    }
    
    @Override
    public void onDisable()
    {
        HandlerList.unregisterAll( this );
    }
    
    /**
     * A near-perfect example of how *not* to write software.
     */
    public void paintInitialImage()
    {
        try
        {
            URL url = new URL( "https://www.reddit.com/api/place/board-bitmap" );
            final InputStream is = new BufferedInputStream( url.openStream() );
            
            // Skip first 4 bytes (timstamp).
            for( int i = 0; i < 4; i++ )
            {
                is.read();
            }
            
            new BukkitRunnable()
            {
                @Getter( AccessLevel.PRIVATE )
                private final int blocksPerTick = MinePlacePlugin.getInstance().getConfig().getInt( "blocks-per-tick", 100 );
                
                @Getter( AccessLevel.PRIVATE )
                private final World world = Bukkit.getWorld( MinePlacePlugin.getInstance().getConfig().getString( "world", "mineplace" ) );
                
                @Getter( AccessLevel.PRIVATE )
                private final int height = MinePlacePlugin.getInstance().getConfig().getInt( "height", 60 );
                
                @Getter( AccessLevel.PRIVATE )
                @Setter( AccessLevel.PRIVATE )
                private int blocksDoneTotal = 0;
                
                private void placeBlock( int seq, int data )
                {
                    int x = seq % 1000;
                    int z = seq / 1000;
                    
                    Block block = this.getWorld().getBlockAt( x, this.getHeight(), z );
                    block.setType( Material.WOOL );
                    block.setData( PlaceColor.getColorById( data ).getWoolColor() );
                }
                
                @Override
                public void run()
                {
                    int blocksDoneTick = 0;
                    
                    while( blocksDoneTick < this.getBlocksPerTick() )
                    {
                        int data = -1;
                        try
                        {
                            data = is.read();
                        }
                        catch( IOException ex )
                        {
                            Logger.getLogger( MinePlacePlugin.class.getName() ).log( Level.SEVERE, null, ex );
                            this.cancel();
                        }
                        
                        if( data == -1 )
                        {
                            Logger.getLogger( MinePlacePlugin.class.getName() ).log( Level.SEVERE, "Got -1" );
                            this.cancel();
                        }
                        
                        this.placeBlock( this.blocksDoneTotal++, ( ( (byte) data ) >> 4 ) & 0x0f );
                        this.placeBlock( this.blocksDoneTotal++, ( (byte) data ) & 0x0f );
                        
                        blocksDoneTick += 2;
                    }
                    
                    if( this.getBlocksDoneTotal() == 1000000 )
                    {
                        this.cancel();
                    }
                }
            }.runTaskTimer( this, 1, 1 );
        }
        catch( IOException ex )
        {
            Logger.getLogger( MinePlacePlugin.class.getName() ).log( Level.SEVERE, null, ex );
        }
    }
}
