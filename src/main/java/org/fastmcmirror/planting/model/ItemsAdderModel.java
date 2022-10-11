package org.fastmcmirror.planting.model;

import dev.lone.itemsadder.api.CustomCrop;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.fastmcmirror.planting.PlantingWand;


public class ItemsAdderModel extends SimpleModel {
    @Override
    public void placeModelBlock(Location location, String model, Player player) {
        try {
            CustomCrop.place(model, location);
        } catch (Exception e) {
            PlantingWand.instance.getLogger().severe("[ItemsAdder] CustomCrop " + model + " not exist");
        }
    }
}
