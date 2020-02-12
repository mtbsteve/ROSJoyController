package com.ROSJoyController;

public interface ClientStatus {
    void onDisconnected();

    void onConnected();

    void onConnecting();
}
