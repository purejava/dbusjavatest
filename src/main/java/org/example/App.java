package org.example;

import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.exceptions.DBusException;
import org.purejava.KDEWallet;

import java.io.IOException;

import static java.lang.System.exit;

/**
 * Hello world!
 *
 */
public class App 
{
    public static DBusConnection connection = null;
    public static void main( String[] args )
    {
        try {
            connection = DBusConnection.getConnection(DBusConnection.DBusBusType.SESSION);
            System.out.println((connection.toString()));
        } catch (DBusException e) {
            System.out.println(e.toString() + e.getCause());
            exit(-1);
        }
        KDEWallet kwallet = new KDEWallet(connection);
        Object[] response = kwallet.send("wallets");
        for(Object o : response) {
            System.out.println(o.toString());
        }
        try {
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
