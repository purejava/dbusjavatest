package org.example;

import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.handlers.SignalHandler;
import org.freedesktop.dbus.messages.DBusSignal;
import org.kde.KWallet;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static java.lang.System.exit;

public class App {
    public static void main(String[] args) {
        DBusConnection connection = null;
        SignalHandler sh = SignalHandler.getInstance();

        final List<Class<? extends DBusSignal>> signals = Arrays.asList(
                KWallet.ApplicationDisconnected.class,
                KWallet.FolderUpdated.class,
                KWallet.FolderListUpdated.class,
                KWallet.AllWalletsClosed.class,
                KWallet.WalletClosed.class,
                KWallet.WalletDeleted.class,
                KWallet.WalletAsyncOpened.class,
                KWallet.WalletOpened.class,
                KWallet.WalletCreated.class,
                KWallet.WalletListDirty.class);

        try {
            connection = DBusConnection.getConnection(DBusConnection.DBusBusType.SESSION);
            System.out.println((connection.toString()));

            if (signals != null) {
                sh.connect(connection, signals);
            }

            KWallet service = connection.getRemoteObject("org.kde.kwalletd5",
                    "/modules/kwalletd5", KWallet.class);

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
