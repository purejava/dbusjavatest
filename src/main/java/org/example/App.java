package org.example;

import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.interfaces.DBusSigHandler;
import org.kde.KWallet;

import java.io.IOException;

import static java.lang.System.exit;

public class App
{
    public static void main( String[] args ) {
        DBusConnection connection = null;
        DBusSigHandler<KWallet.FolderListUpdated> interfacesAddedSignalHandler = null;
        {
            try {
                connection = DBusConnection.getConnection(DBusConnection.DBusBusType.SESSION);
                System.out.println((connection.toString()));
                interfacesAddedSignalHandler = new DBusSigHandler<KWallet.FolderListUpdated>() {
                    @Override
                    public void handle(KWallet.FolderListUpdated signal) {
                        System.out.println(signal.wallet + "xxxxxxxxxxxxxxxxxxxxxxx");
                    }
                };
                KWallet service = connection.getRemoteObject("org.kde.kwalletd5",
                        "/modules/kwalletd5", KWallet.class);
                String wallet = "kdewallet";
                int wId = 0;
                String appid = "Tester";
                Object[] response = service.send("open", "sxs", wallet, wId, appid);
                int handle = (int) response[0];
                String folder = "Testfolder";
                response = service.send("createFolder", "iss", handle, folder, appid);
                boolean folderCreated = (boolean) response[0];
                if (folderCreated) {
                    System.out.println("Folder created!");
                }
                response = service.send("close", "ibs", handle, false, appid);
            } catch (DBusException e) {
                System.out.println(e.toString() + e.getCause());
                exit(-1);
            }
            try {
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
