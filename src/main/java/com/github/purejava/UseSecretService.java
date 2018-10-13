package com.github.purejava;

import org.freedesktop.DBus;
import org.freedesktop.Secret.Item;
import org.freedesktop.Secret.Pair;
import org.freedesktop.Secret.Secret;
import org.freedesktop.Secret.Service;
import org.freedesktop.dbus.DBusConnection;
import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.Variant;
import org.freedesktop.dbus.exceptions.DBusException;

import java.util.List;

public class UseSecretService {
    public static void main(String[] args) {
        DBusConnection conn = null;
        try {
            conn = DBusConnection.getConnection(DBusConnection.SESSION);
            Service service = conn.getRemoteObject("org.freedesktop.secrets",
                    "/org/freedesktop/secrets", Service.class);
            Pair<Variant, DBusInterface> pair = service.OpenSession("plain",
                    new Variant<>(""));
            DBusInterface dbusSession = pair.b;
            String objectPath = "/org/freedesktop/secrets/collection/login/22";
            DBus.Properties prop = (DBus.Properties) conn.getRemoteObject("org.freedesktop.secrets", objectPath, DBus.Properties.class);
            String label = (String) prop.Get("org.freedesktop.Secret.Item", "Label");
            System.out.println("Label = " + label);

            Item item = conn.getRemoteObject("org.freedesktop.secrets", objectPath, Item.class);
            Secret secret = item.GetSecret(dbusSession);
            List<Byte> passwordAsByteArray = secret.value;
            System.out.println("Password = " + byteListToString(passwordAsByteArray));

        } catch (DBusException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) conn.disconnect();
        }
    }
    private static String byteListToString(List<Byte> l) {
        if (l == null) {
            return "";
        }
        byte[] array = new byte[l.size()];
        int i = 0;
        for (Byte current : l) {
            array[i] = current;
            i++;
        }
        return new String(array);
    }
}
