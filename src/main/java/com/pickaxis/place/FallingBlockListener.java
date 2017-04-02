package com.pickaxis.place;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

public class FallingBlockListener implements Listener
{
    @EventHandler
    public void onEntityChangeBlock( EntityChangeBlockEvent e )
    {
        e.setCancelled( true );
    }
}
