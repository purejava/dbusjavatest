package org.kde;

import org.freedesktop.dbus.annotations.DBusInterfaceName;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.interfaces.DBusInterface;
import org.freedesktop.dbus.messages.DBusSignal;

@DBusInterfaceName(value = "org.kde.KWallet")
public interface AbstractInterface extends DBusInterface {

    public static class walletClosed extends DBusSignal {
        public final int handle;

        public walletClosed(String path, int handle) throws DBusException {
            super(path, handle);
            this.handle = handle;
        }
    }
}
