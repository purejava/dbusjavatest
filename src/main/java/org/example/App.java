package org.example;

import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.interfaces.DBusSigHandler;
import org.freedesktop.dbus.messages.DBusSignal;
import org.kde.KWallet;

import java.io.IOException;

import static java.lang.System.exit;
import static org.kde.KWallet.*;

public class App
{
    public static void main( String[] args ) {
        DBusConnection connection = null;
        DBusSigHandler interfacesAddedSignalHandler = null;
        {
            try {
                connection = DBusConnection.getConnection(DBusConnection.DBusBusType.SESSION);
                System.out.println((connection.toString()));
                interfacesAddedSignalHandler = new DBusSigHandler() {

                    @Override
                    public void handle(DBusSignal s) {
                        System.out.println("Signal received !!!");
                    }
                };
                KWallet service = connection.getRemoteObject("org.kde.kwalletd5",
                        "/modules/kwalletd5", KWallet.class);
                connection.addSigHandler(FolderListUpdated.class, interfacesAddedSignalHandler);
                String wallet = "kdewallet";
                int wId = 0;
                String appid = "Tester";
                int handle = service.open(wallet, 0, appid);
                System.out.println("Handle = " + handle);
                String folder = "Test-Folder";
                boolean created = service.createFolder(handle, folder, appid);
                System.out.println(Boolean.toString(created));
                Thread.sleep(3000);
                service.removeFolder(handle, folder, appid);
                service.close(handle, false, appid);
            } catch (DBusException | InterruptedException e) {
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
