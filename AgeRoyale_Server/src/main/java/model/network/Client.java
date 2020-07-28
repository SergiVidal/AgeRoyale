package model.network;

public class Client {
    private Integer userId;
    private DedicatedServer dedicatedServer;

    public Client(Integer userId, DedicatedServer dedicatedServer) {
        this.userId = userId;
        this.dedicatedServer = dedicatedServer;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public DedicatedServer getDedicatedServer() {
        return dedicatedServer;
    }

    public void setDedicatedServer(DedicatedServer dedicatedServer) {
        this.dedicatedServer = dedicatedServer;
    }
}
