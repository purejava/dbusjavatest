package org.freedesktop.Secret;

import java.util.List;
import java.util.Map;

import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.DBusSignal;
import org.freedesktop.dbus.Path;
import org.freedesktop.dbus.Variant;
import org.freedesktop.dbus.exceptions.DBusException;

public interface Collection extends DBusInterface {
    public static class ItemCreated extends DBusSignal {
        public final Item item;

        public ItemCreated(String path, Item item) throws DBusException {
            super(path, item);
            this.item = item;
        }
    }

    public static class ItemDeleted extends DBusSignal {
        public final DBusInterface item;

        public ItemDeleted(String path, Item item) throws DBusException {
            super(path, item);
            this.item = item;
        }
    }

    public static class ItemChanged extends DBusSignal {
        public final Item item;

        public ItemChanged(String path, Item item) throws DBusException {
            super(path, item);
            this.item = item;
        }
    }

    public Path Delete();

    public List<Item> SearchItems(Map<String, String> attributes);

    public Pair<Path, Path> CreateItem(Map<String, Variant> properties, Secret secret, boolean replace);

}
