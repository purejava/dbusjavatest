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
                KWallet.applicationDisconnected.class,
                KWallet.folderUpdated.class,
                KWallet.folderListUpdated.class,
                KWallet.walletClosedInt.class,
                KWallet.walletClosed.class,
                KWallet.allWalletsClosed.class,
                KWallet.walletDeleted.class,
                KWallet.walletAsyncOpened.class,
                KWallet.walletOpened.class,
                KWallet.walletCreated.class,
                KWallet.walletListDirty.class);

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
            service.removeFolder(handle, folder, appid);
            service.close(handle, true, appid);
            sh.await(KWallet.walletClosed.class, "/modules/kwalletd5", () -> {
                return null;
            });
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
