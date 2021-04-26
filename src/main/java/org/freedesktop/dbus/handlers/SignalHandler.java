package org.freedesktop.dbus.handlers;

import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.interfaces.DBusSigHandler;
import org.freedesktop.dbus.messages.DBusSignal;
import org.kde.KWallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class SignalHandler implements DBusSigHandler {

    private Logger log = LoggerFactory.getLogger(SignalHandler.class);

    private DBusConnection connection = null;
    private List<Class<? extends DBusSignal>> registered = new ArrayList();
    private DBusSignal[] handled = new DBusSignal[250];
    private int count = 0;

    private SignalHandler() {
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
                disconnect()
        ));
    }

    public static SignalHandler getInstance() {
        return SingletonHelper.INSTANCE;
    }

    public void connect(DBusConnection connection, List<Class<? extends DBusSignal>> signals) {
        if (this.connection == null) {
            this.connection = connection;
        }
        if (signals != null) {
            try {
                for (Class sc : signals) {
                    if (!registered.contains(sc)) {
                        connection.addSigHandler(sc, this);
                        log.info("Connecting Signal " + sc.toString());
                        this.registered.add(sc);
                    }
                }
            } catch (DBusException e) {
                log.error(e.toString(), e.getCause());
            }
        }
    }

    public void disconnect() {
        if (connection != null) {
            try {
                log.debug("remove signal handlers");
                for (Class sc : registered) {
                    if (connection.isConnected()) {
                        log.trace("remove signal handler: " + sc.getName());
                        connection.removeSigHandler(sc, this);
                    }
                }
            } catch (DBusException e) {
                log.error(e.toString(), e.getCause());
            }
        }
    }

    @Override
    public void handle(DBusSignal s) {
        Collections.rotate(Arrays.asList(handled), 1);
        handled[0] = s;
        count += 1;

        if (s instanceof KWallet.walletOpened) {
            KWallet.walletOpened wo = (KWallet.walletOpened) s;
            log.info("KWallet.WalletOpened: " + wo.wallet);
        } else if (s instanceof KWallet.walletAsyncOpened) {
            KWallet.walletAsyncOpened wo = (KWallet.walletAsyncOpened) s;
            log.info("KWallet.WalletAsyncOpened: " + wo.tId + " / " + wo.handle);
        } else if (s instanceof KWallet.walletDeleted) {
            KWallet.walletDeleted wd = (KWallet.walletDeleted) s;
            log.info("KWallet.WalletDeleted: " + wd.wallet);
        } else if (s instanceof KWallet.walletClosedId) {
            log.info("Id-Interface --> "+s.getInterface());
            KWallet.walletClosedId wc = (KWallet.walletClosedId) s;
            log.info("KWallet.walletClosedId: " + wc.handle);
        } else if (s instanceof KWallet.walletClosed) {
            log.info("wC-Interface --> "+s.getInterface());
            KWallet.walletClosed wc = (KWallet.walletClosed) s;
            log.info("KWallet.WalletClosed: " + wc.wallet);
        } else if (s instanceof KWallet.allWalletsClosed) {
            log.info("KWallet.AllWalletsClosed: " + s.getPath());
        } else if (s instanceof KWallet.folderListUpdated) {
            KWallet.folderListUpdated flu = (KWallet.folderListUpdated) s;
            log.info("KWallet.FolderListUpdated: " + flu.wallet);
        } else if (s instanceof KWallet.folderUpdated) {
            KWallet.folderUpdated fu = (KWallet.folderUpdated) s;
            log.info("KWallet.FolderUpdated: " + fu.a + " / " + fu.b);
        } else if (s instanceof KWallet.applicationDisconnected) {
            KWallet.applicationDisconnected ad = (KWallet.applicationDisconnected) s;
            log.info("KWallet.ApplicationDisconnected: " + ad.application + " / "+ ad.wallet);
        } else if (s instanceof KWallet.walletListDirty) {
            log.info("KWallet.WalletListDirty: " + s.getPath());
        } else if (s instanceof KWallet.walletCreated) {
            KWallet.walletCreated wc = (KWallet.walletCreated) s;
            log.info("KWallet.WalletCreated: " + wc.wallet);
        } else {
            log.warn("Handled unknown signal: " + s.getClass().toString() + " {" + s.toString() + "}");
        }
    }

    public DBusSignal[] getHandledSignals() {
        return handled;
    }

    public <S extends DBusSignal> List<S> getHandledSignals(Class<S> s) {
        return Arrays.stream(handled)
                .filter(signal -> signal != null)
                .filter(signal -> signal.getClass().equals(s))
                .map(signal -> (S) signal)
                .collect(Collectors.toList());
    }

    public <S extends DBusSignal> List<S> getHandledSignals(Class<S> s, String path) {
        return Arrays.stream(handled)
                .filter(signal -> signal != null)
                .filter(signal -> signal.getClass().equals(s))
                .filter(signal -> signal.getPath().equals(path))
                .map(signal -> (S) signal)
                .collect(Collectors.toList());
    }

    public int getCount() {
        return count;
    }

    public DBusSignal getLastHandledSignal() {
        return handled[0];
    }

    public <S extends DBusSignal> S getLastHandledSignal(Class<S> s) {
        return getHandledSignals(s).get(0);
    }

    public <S extends DBusSignal> S getLastHandledSignal(Class<S> s, String path) {
        return getHandledSignals(s, path).get(0);
    }

    public <S extends DBusSignal> S await(Class<S> s, String path, Callable action) {
        final Duration timeout = Duration.ofSeconds(3);
        return await(s, path, action, timeout);
    }

    public <S extends DBusSignal> S await(Class<S> s, String path, Callable action, Duration timeout) {

        try {
            action.call();
        } catch (Exception e) {
            log.error(e.toString(), e.getCause());
        }

        int init = getHandledSignals(s, path).size();
        ExecutorService executor = Executors.newSingleThreadExecutor();

        log.info("await signal " + s.getName() + "(" + path + ") within " + timeout.getSeconds() + " seconds.");

        final Future<S> handler = executor.submit((Callable) () -> {
            int await = init;
            List<S> signals = null;
            while (await == init) {
                Thread.sleep(50L);
                signals = getHandledSignals(s, path);
                await = signals.size();
            }
            if (!signals.isEmpty()) {
                return signals.get(0);
            } else {
                return null;
            }
        });

        try {
            return handler.get(timeout.toMillis(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException | InterruptedException | ExecutionException e) {
            handler.cancel(true);
            log.error(e.toString(), e.getCause());
        } finally {
            executor.shutdownNow();
        }

        return null;
    }

    private static class SingletonHelper {
        private static final SignalHandler INSTANCE = new SignalHandler();
    }
}
