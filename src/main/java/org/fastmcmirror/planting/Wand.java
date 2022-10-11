package org.fastmcmirror.planting;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.fastmcmirror.planting.utils.MessageType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Wand {

    public Map<UUID, Long> cooldowns;
    public int range;
    public Material plant;
    public String permission;
    public String message;
    public MessageType messageType;
    public long cooldown;
    public boolean disposable;
    public boolean particle;
    public Particle particleType;
    public boolean model;
    public String modelid;

    public Wand(int range, Material plant, String permission, String message, MessageType messageType, long cooldown, boolean disposable, boolean particle, Particle particleType
            , boolean model, String modelid) {
        this.range = range;
        this.plant = plant;
        this.permission = permission;
        this.message = message;
        this.messageType = messageType;
        this.cooldown = cooldown;
        cooldowns = new HashMap<>();
        this.disposable = disposable;
        this.particle = particle;
        this.particleType = particleType;
        this.model = model;
        this.modelid = modelid;
    }
}
