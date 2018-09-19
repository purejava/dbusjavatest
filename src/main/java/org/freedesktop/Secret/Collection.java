package org.freedesktop.Secret;

import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.DBusSignal;
import org.freedesktop.dbus.Variant;
import org.freedesktop.dbus.exceptions.DBusException;

import java.util.List;
import java.util.Map;

public interface Collection extends DBusInterface {
    public static class ItemCreated extends DBusSignal {
        public final DBusInterface item;

        public ItemCreated(String path, DBusInterface item) throws DBusException {
            super(path, item);
            this.item = item;
        }
    }

    public static class ItemDeleted extends DBusSignal {
        public final DBusInterface item;

        public ItemDeleted(String path, DBusInterface item) throws DBusException {
            super(path, item);
            this.item = item;
        }
    }

    public static class ItemChanged extends DBusSignal {
        public final DBusInterface item;

        public ItemChanged(String path, DBusInterface item) throws DBusException {
            super(path, item);
            this.item = item;
        }
    }

    public DBusInterface Delete();

    public List<DBusInterface> SearchItems(Map<String, String> attributes);

    public Pair<DBusInterface, DBusInterface> CreateItem(Map<String, Variant> properties, Struct1 secret, boolean replace);

}
