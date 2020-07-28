package model.network;

import model.entity.*;

import java.sql.SQLException;

public interface NetworkCallback {
    void onRegisterUser(User user, DedicatedServer dServer);

    void onLoginUser(UserLogin userLogin, DedicatedServer dedicatedServer) throws SQLException;

    void onFriendInvitation(UserFriendInvitation object, DedicatedServer dedicatedServer) throws SQLException;

    void onDeleteFriend(UserFriendship object, DedicatedServer dedicatedServer) throws SQLException;

    void onJoinFriendInvitation(UserFriendInvitation friendInvitation, DedicatedServer dedicatedServer);

    void onGetFriends(UserFriendship object, DedicatedServer dedicatedServer);

    void onRefuseFriendInvitation(UserFriendInvitation friendInvitation, DedicatedServer dedicatedServer);

    void onGetFriendInvitations(UserFriendInvitation friendInvitation, DedicatedServer dedicatedServer);

    void onAddMatch(UserMatch userMatch, DedicatedServer dServer);


    void onAcceptMatchInvitation(UserMatchInvitation matchInvitation, DedicatedServer dServer);

    void onCreateTroop(UserGame userGame, DedicatedServer dedicatedServer);

    void onLocateTroop(UserGame troop, DedicatedServer dedicatedServer);

    void onGetTroops(String matchName, DedicatedServer dedicatedServer);

    void onGetPublicMatches(UserMatch userMatch, String type, DedicatedServer dedicatedServer);

    void onJoinMatch(UserMatch joinMatch, DedicatedServer dServer);

    void onViewMatch(UserMatch object, DedicatedServer dedicatedServer);

    void onCreateMatchInvitation(UserMatchInvitation userMatchInvitation, DedicatedServer dServer);

    void onGetUserInfo(User user, DedicatedServer dedicatedServer);

    void onRefuseMatchInvitation(UserMatchInvitation object, DedicatedServer dedicatedServer);

    void onCancelMatch(UserMatchInvitation object, DedicatedServer dedicatedServer);

    void onFriendConnected(Friend friend, DedicatedServer dedicatedServer);

    void onDisconnectClient(String userName, DedicatedServer dServer);

    void onResetServer();

    void onLogoutUser(User user, DedicatedServer dedicatedServer);

    void onGetGameInfo(Game object, DedicatedServer dedicatedServer);

    void onCreateGameRoom(UserMatch joinMatch, User user, Player playerGuest, DedicatedServer dServer);

    void onMoveTroop(UserGame userGame);

    void getUserTroopsById(String matchName, String username, String guestName, Server server);

    void onUpdateStatistics(String username, String username1, DedicatedServer winnerClient, DedicatedServer looserClient);

    void onGetPlayersInfo(String hostName, String guestName, String matchName);

    void onCancelMatchInvitation(UserMatchInvitation object, DedicatedServer dedicatedServer);

     void onWin(String username, String matchName, int matchTime);

    void onIncrementPlayersMoney(Player plHost, Player plGuest);
}
