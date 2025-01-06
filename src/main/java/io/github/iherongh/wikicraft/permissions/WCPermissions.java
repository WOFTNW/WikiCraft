package io.github.iherongh.wikicraft.permissions;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.types.PermissionNode;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * The class for handling permissions.
 *
 * @author iHeronGH
 *
 * @version 0.1.0
 *
 * @since 0.1.0
 */
public class WCPermissions {

    /**
     * Constructs a new {@code WCPermissions} object.
     */
    public WCPermissions() {}

    /**
     * Gets the LuckPerms instance.
     *
     * @return The LuckPerms instance.
     */
    public static @NonNull LuckPerms getLuckPerms() {
        return LuckPermsProvider.get();

    }

    /**
     * Gets the LuckPerms user manager.
     *
     * @return The LuckPerms user manager.
     */
    public static @NotNull UserManager getUserManager() {
        return getLuckPerms().getUserManager();

    }

    /**
     * Grants a permission to a player.
     *
     * @param uuid The UUID of the player.
     * @param permission The permission to grant.
     */
    public static void grantPermission( UUID uuid, String permission ) {
        getUserManager().modifyUser( uuid, user -> {
            // Add the permission
            user.data().add( PermissionNode.builder( permission ).build() );

        });

    }

    /**
     * Revokes a permission from a player.
     *
     * @param uuid The UUID of the player.
     * @param permission The permission to revoke.
     */
    public static void revokePermission( UUID uuid, String permission ) {
        getUserManager().modifyUser( uuid, user -> {
            // Add the permission
            user.data().remove( PermissionNode.builder( permission ).build() );

        });

    }

}
