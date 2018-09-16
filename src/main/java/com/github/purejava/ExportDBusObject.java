package com.github.purejava;

import org.freedesktop.dbus.DBusConnection;
import org.freedesktop.dbus.exceptions.DBusException;

/**
 * Hello world!
 */
public class ExportDBusObject {
    public static void main(String[] args) {
        try {
            DBusConnection conn = DBusConnection.getConnection(DBusConnection.SESSION);
            conn.requestBusName("com.rm5248");
            conn.exportObject("/", new JavaReceive());
        } catch (DBusException e) {
            e.printStackTrace();
        }
    }
}
