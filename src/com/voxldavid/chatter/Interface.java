package com.voxldavid.chatter;

/**
 * Verbindet den Client mit dem Benutzer vor dem Bildschirm und macht die
 * richtigen Sachen (ruft die richtigen Methoden des Clients auf) wenn sie
 * gemacht werden sollen
 */
interface Interface {
    public void loginSuccess(String roomName);

    public void logoutSuccess();

    public void receiveMessage(String msg, String source, String time);

    public void userLoginSuccess(String userName);

    public void requestLoginRoom();

    public void alreadyLoggedInRoom(String roomName);

    public void alreadyLoggedInUser(String roomName);

    public void updateRoomList(String[] rooms);
}