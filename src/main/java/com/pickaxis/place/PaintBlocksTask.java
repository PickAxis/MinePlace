package com.pickaxis.place;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * A near-perfect example of how *not* to write software. 
 */
@Getter( value = AccessLevel.PRIVATE )
@Setter( value = AccessLevel.PRIVATE )
public class PaintBlocksTask extends BukkitRunnable
{
    private InputStream is;
    
    private final int blocksPerTick;
    
    private final World world;
    
    private final int height;
    
    private final int fallingBlockHeight;
    
    private int blocksDoneTotal;
    
    public PaintBlocksTask()
    {
        this.blocksPerTick = MinePlacePlugin.getInstance().getConfig().getInt( "blocks-per-tick", 100 );
        this.world = Bukkit.getWorld( MinePlacePlugin.getInstance().getConfig().getString( "world", "mineplace" ) );
        this.height = MinePlacePlugin.getInstance().getConfig().getInt( "height", 60 );
        this.fallingBlockHeight = MinePlacePlugin.getInstance().getConfig().getInt( "falling-block-height", 90 );
        this.blocksDoneTotal = 0;
    }
    
    private void init()
    {
        try
        {
            this.setIs( new BufferedInputStream( new URL( "https://www.reddit.com/api/place/board-bitmap" ).openStream(), 5000004 ) );
            // Skip first 4 bytes (timstamp).
            for( int i = 0; i < 4; i++ )
            {
                this.getIs().read();
            }
        }
        catch( IOException ex )
        {
            Logger.getLogger( MinePlacePlugin.class.getName() ).log( Level.SEVERE, null, ex );
            this.cancel();
            new PaintBlocksTask().runTaskTimer( MinePlacePlugin.getInstance(), 400, 1 );
        }
    }
    
    @SuppressWarnings( value = "deprecation" )
    private void placeBlock( int seq, int data )
    {
        int x = seq % 1000;
        int z = seq / 1000;
        
        Block block = this.getWorld().getBlockAt( x, this.getHeight(), z );
        
        if( block.getType() != Material.WOOL )
        {
            block.setType( Material.WOOL );
        }
        
        byte woolColor = PlaceColor.getColorById( data ).getWoolColor();
        if( block.getData() != woolColor )
        {
            block.setData( woolColor );
            
            FallingBlock fb = this.getWorld().spawnFallingBlock( new Location( this.getWorld(), x, this.getFallingBlockHeight(), z ), Material.WOOL, woolColor );
            fb.setDropItem( false );
            fb.setHurtEntities( false );
        }
    }
    
    @Override
    public void run()
    {
        if( blocksDoneTotal == 0 )
        {
            this.init();
        }
        
        int blocksDoneTick = 0;
        while( blocksDoneTick < this.getBlocksPerTick() )
        {
            int data = -1;
            try
            {
                data = this.getIs().read();
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
            Logger.getLogger( MinePlacePlugin.class.getName() ).log( Level.INFO, "Done painting initial bitmap." );
            this.cancel();
            if( MinePlacePlugin.getInstance().getConfig().getBoolean( "rerender-from-initial", false ) )
            {
                new PaintBlocksTask().runTaskTimer( MinePlacePlugin.getInstance(), MinePlacePlugin.getInstance().getConfig().getInt( "time-between-renders", 1 ), 1 );
            }
        }
    }
}
