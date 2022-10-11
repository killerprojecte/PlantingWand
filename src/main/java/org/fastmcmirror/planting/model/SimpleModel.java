package org.fastmcmirror.planting.model;


import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class SimpleModel {
    public abstract void placeModelBlock(Location location, String model, Player player);
}
