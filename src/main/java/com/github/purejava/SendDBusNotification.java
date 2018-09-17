package com.github.purejava;

import org.freedesktop.Notifications;
import org.freedesktop.dbus.DBusConnection;
import org.freedesktop.dbus.UInt32;
import org.freedesktop.dbus.Variant;
import org.freedesktop.dbus.exceptions.DBusException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class SendDBusNotification {
    public static void main(String[] args) {
        try {
            DBusConnection conn = DBusConnection.getConnection(DBusConnection.SESSION);
            Notifications notify = conn.getRemoteObject("org.freedesktop.Notifications",
                    "/org/freedesktop/Notifications", Notifications.class);
            Map<String, Variant<Byte>> hints = new HashMap<>();
            hints.put("urgency", new Variant<>((byte) 2));
            notify.Notify("",
                    new UInt32(0),
                    "",
                    "This is a test",
                    "Again, this is only a test",
                    new LinkedList<String>(),
                    hints,
                    -1);
        } catch (DBusException e) {
            e.printStackTrace();
        }
    }
}
