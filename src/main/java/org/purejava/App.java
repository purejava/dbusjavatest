package org.purejava;

import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.exceptions.DBusException;
import org.kde.KWallet;

import java.io.IOException;

public class App {
    public static void main(String[] args) {
        DBusConnection connection = null;

        try {
            connection = DBusConnection.getConnection(DBusConnection.DBusBusType.SESSION);

            KDEWallet service = new KDEWallet(connection);
            int handle = service.getSignalHandler().getLastHandledSignal(KWallet.walletAsyncOpened.class, "/modules/kwalletd5").handle;
        } catch (DBusException e) {
            System.out.println(e.toString() + e.getCause());
        }
        try {
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
