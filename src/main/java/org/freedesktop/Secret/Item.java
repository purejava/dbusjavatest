package org.freedesktop.Secret;

import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.Path;

public interface Item extends DBusInterface {

    public Path Delete();

    public Secret GetSecret(Path session);

    public void SetSecret(Secret secret);

}
