package org.fastmcmirror.planting.model;

import dev.linwood.itemmods.ItemMods;
import dev.linwood.itemmods.pack.PackObject;
import dev.linwood.itemmods.pack.asset.BlockAsset;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.fastmcmirror.planting.PlantingWand;

public class ItemModsModel extends SimpleModel {
    @Override
    public void placeModelBlock(Location location, String model, Player player) {
        BlockAsset asset = ItemMods.getCustomBlockManager().getAssetByKey(model);
        if (asset == null) {
            PlantingWand.instance.getLogger().severe("[ItemMods] CustomBlock " + model + " not exist");
            return;
        }
        PackObject packObject = asset.getModelObject();
        ItemMods.getCustomBlockManager().create(location, packObject, player);
    }
}
