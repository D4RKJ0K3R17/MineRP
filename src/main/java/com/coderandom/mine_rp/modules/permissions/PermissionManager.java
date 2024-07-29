package com.coderandom.mine_rp.modules.permissions;

import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static com.coderandom.mine_rp.MineRP.MINE_RP;

public class PermissionManager {
    private static final Map<UUID, PermissionAttachment> playerPermissions = new ConcurrentHashMap<>();

    public void addPermission(Player player, String permission) {
        getAttachment(player).setPermission(permission, true);
    }

    public void addPermission(Player player, Permission permission) {
        getAttachment(player).setPermission(permission, true);
    }

    public void removePermission(Player player, String permission) {
        getAttachment(player).unsetPermission(permission);
    }

    public void removePermission(Player player, Permission permission) {
        getAttachment(player).unsetPermission(permission);
    }

    private PermissionAttachment getAttachment(Player player) {
        return playerPermissions.computeIfAbsent(player.getUniqueId(), k -> player.addAttachment(MINE_RP));
    }

    public void removePlayerPermissions(Player player) {
        PermissionAttachment attachment = playerPermissions.remove(player.getUniqueId());
        if (attachment != null) {
            player.removeAttachment(attachment);
        }
    }
}
