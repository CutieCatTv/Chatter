package com.voxldavid.chatter;

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