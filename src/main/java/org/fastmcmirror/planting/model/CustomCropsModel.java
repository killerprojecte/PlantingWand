package org.fastmcmirror.planting.model;

import net.momirealms.customcrops.CustomCrops;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class CustomCropsModel extends SimpleModel {
    @Override
    public void placeModelBlock(Location location, String model, Player player) {
        CustomCrops.plugin.getCropManager().getCustomWorld(location.getWorld()).addCrop(location, model);
    }
}
