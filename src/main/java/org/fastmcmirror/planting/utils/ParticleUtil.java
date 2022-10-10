package org.fastmcmirror.planting.utils;

import org.bukkit.Particle;
import org.bukkit.block.Block;

public class ParticleUtil {
    public static void playParticle(Particle particle, Block block) {
        block.getWorld().spawnParticle(particle, block.getLocation().clone().add(0.5, 0.8, 0.5), 6);
    }
}
