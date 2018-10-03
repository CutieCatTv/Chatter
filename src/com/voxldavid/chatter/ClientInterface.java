package com.voxldavid.chatter;

/**
 * Klasse ohne wirklichen Nutzen, außer die Basis Funktionalität des 'Interface' Interfaces zu testen
 */
class ClientInterface implements Interface {

    @Override
    public void loginSuccess(String roomName) {

    }

    @Override
    public void logoutSuccess() {

    }

    @Override
    public void receiveMessage(String msg, String source, String time) {

    }

    @Override
    public void userLoginSuccess(String userName) {

    }

    @Override
    public void requestLoginRoom() {

    }

    @Override
    public void alreadyLoggedInRoom(String roomName) {

    }

    @Override
    public void alreadyLoggedInUser(String roomName) {

    }

    @Override
    public void updateRoomList(String[] rooms) {

    }

}