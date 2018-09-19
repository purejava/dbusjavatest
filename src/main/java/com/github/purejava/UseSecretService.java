package com.github.purejava;

import org.freedesktop.Secret.Pair;
import org.freedesktop.Secret.Service;
import org.freedesktop.dbus.DBusConnection;
import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.Variant;
import org.freedesktop.dbus.exceptions.DBusException;

public class UseSecretService {
    public static void main(String[] args) {
        try {
            DBusConnection conn = DBusConnection.getConnection(DBusConnection.SESSION);
            Service service = conn.getRemoteObject("org.freedesktop.secrets",
                    "/org/freedesktop/secrets", Service.class);
            Pair<Variant, DBusInterface> pair = null;
            pair = service.OpenSession("plain",
                    new Variant<>(""));
        } catch (DBusException e) {
            e.printStackTrace();
        }
    }
}
