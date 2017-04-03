package com.pickaxis.place;

import lombok.Getter;
import org.bukkit.DyeColor;

@Getter
public enum PlaceColor
{
    WHITE( 0, DyeColor.WHITE, 0 ),
    LIGHT_GRAY( 1, DyeColor.SILVER, 8 ),
    GRAY( 2, DyeColor.GRAY, 7 ),
    BLACK( 3, DyeColor.BLACK, 15 ),
    PINK( 4, DyeColor.PINK, 6 ),
    RED( 5, DyeColor.RED, 14 ),
    ORANGE( 6, DyeColor.ORANGE, 1 ),
    BROWN( 7, DyeColor.BROWN, 12 ),
    YELLOW( 8, DyeColor.YELLOW, 4 ),
    LIME( 9, DyeColor.LIME, 5 ),
    GREEN( 10, DyeColor.GREEN, 13 ),
    LIGHT_BLUE( 11, DyeColor.LIGHT_BLUE, 3 ),
    TEAL( 12, DyeColor.CYAN, 9 ),
    BLUE( 13, DyeColor.BLUE, 11 ),
    MAGENTA( 14, DyeColor.MAGENTA, 2 ),
    PURPLE( 15, DyeColor.PURPLE, 10 );
    
    int redditColor;
    
    DyeColor dye;
    
    byte woolColor;
    
    PlaceColor( int redditColor, DyeColor dye, int woolColor )
    {
        this.redditColor = redditColor;
        this.dye = dye;
        this.woolColor = (byte) woolColor;
    }
    
    public static PlaceColor getColorById( int id )
    {
        return PlaceColor.values()[ id ];
    }
}
