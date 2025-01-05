package io.github.iherongh.wikicraft.permissions;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.types.PermissionNode;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class WCPermissions {

    public static @NonNull LuckPerms getLuckPerms() {
        return LuckPermsProvider.get();

    }

    public static @NotNull UserManager getUserManager() {
        return getLuckPerms().getUserManager();

    }

    public static void grantPermission( UUID uuid, String permission ) {
        getUserManager().modifyUser( uuid, user -> {
            // Add the permission
            user.data().add( PermissionNode.builder( permission ).build() );

        });

    }

    public static void revokePermission( UUID uuid, String permission ) {
        getUserManager().modifyUser( uuid, user -> {
            // Add the permission
            user.data().remove( PermissionNode.builder( permission ).build() );

        });

    }

}
