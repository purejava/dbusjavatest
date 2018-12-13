package com.github.purejava;

import org.freedesktop.DBus;
import org.freedesktop.Secret.*;
import org.freedesktop.dbus.*;
import org.freedesktop.dbus.exceptions.DBusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UseSecretService {
    public static void main(String[] args) throws InterruptedException, DBusException {
        DBusConnection conn = null;
        DBusSigHandler<Prompt.Completed> interfacesAddedSignalHandler = null;
        try {
            conn = DBusConnection.getConnection(DBusConnection.SESSION);
            interfacesAddedSignalHandler = new DBusSigHandler<Prompt.Completed>() {
                @Override
                public void handle(Prompt.Completed signal) {
                    if (signal.dismissed) System.out.println("Prompt dismissed"); else System.out.println("Password was entered");
                }
            };
            conn.addSigHandler(Prompt.Completed.class, interfacesAddedSignalHandler);
            Service service = conn.getRemoteObject("org.freedesktop.secrets",
                    "/org/freedesktop/secrets", Service.class);
            Pair<Variant, Path> pair = service.OpenSession("plain",
                    new Variant<>(""));
            Path dbusSession = pair.b;
            String objectPath = "/org/freedesktop/secrets/collection/login";
            DBus.Properties collectionProperties = conn.getRemoteObject("org.freedesktop.secrets", objectPath, DBus.Properties.class);
            Boolean locked = collectionProperties.Get("org.freedesktop.Secret.Collection", "Locked");
            if (locked) {
                Collection collection = conn.getRemoteObject("org.freedesktop.secrets", objectPath, Collection.class);
                System.out.println("Keyring is locked.");
                List<Path> list = new ArrayList<>();
                list.add(new Path(objectPath));
                Pair<List<Path>, Path> unlockResult = service.Unlock(list);
                String path = unlockResult.b.getPath();
                if (!"/".equals(path)) {
                    Prompt prompt = conn.getRemoteObject("org.freedesktop.secrets", path, Prompt.class);
                    prompt.Prompt("");
                    Thread.sleep(10000);
                }
            }
            Collection collection = conn.getRemoteObject("org.freedesktop.secrets", objectPath, Collection.class);
            List<DBus.Properties> objectList = collection.SearchItems(new HashMap());
            for (DBus.Properties p : objectList) {
                objectPath = p.toString().split(":")[2];
                DBus.Properties prop = conn.getRemoteObject("org.freedesktop.secrets", objectPath, DBus.Properties.class);
                String label = prop.Get("org.freedesktop.Secret.Item", "Label");
                System.out.println("Label = " + label);

                Item item = conn.getRemoteObject("org.freedesktop.secrets", objectPath, Item.class);
                Secret secret = item.GetSecret(dbusSession);
                List<Byte> passwordAsByteArray = secret.value;
                System.out.println("Password = " + byteListToString(passwordAsByteArray));
                System.out.println();
            }
        } catch (DBusException e) {
            e.printStackTrace();
        } finally {
            if (conn != null && interfacesAddedSignalHandler != null) {
                conn.disconnect();
                conn.removeSigHandler(Prompt.Completed.class, interfacesAddedSignalHandler);
            }
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
