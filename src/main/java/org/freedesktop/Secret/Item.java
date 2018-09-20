package org.freedesktop.Secret;

import org.freedesktop.dbus.DBusInterface;

public interface Item extends DBusInterface {

    public DBusInterface Delete();

    public Struct3 GetSecret(DBusInterface session);

    public void SetSecret(Struct4 secret);

}
