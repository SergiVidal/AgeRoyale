package model.entity;

import java.io.Serializable;
import java.util.HashMap;

public class TroopRequest implements Serializable {
    private Object object;
    private String troopAction;
    private HashMap<String, Object> troopParameters;
    private String username;

    public TroopRequest() {
        this.troopParameters=new HashMap<String, Object>();
    }

    public TroopRequest (int troopId, int attackedTroopId, String userName){
        this.username=userName;
        this.troopParameters=new HashMap<String, Object>();
        troopParameters.put("troopId",troopId);
        troopParameters.put("attackedTroopId",attackedTroopId);
    }

    public TroopRequest(Object object, String troopAction, String userName) {
        this.object = object;
        this.troopAction = troopAction;
        this.username = userName;
    }

    public TroopRequest(Object object, String troopAction, HashMap<String, Object> params, String userName) {
        this.object = object;
        this.troopAction = troopAction;
        this.troopParameters = params;
        this.username = userName;
    }

    public String getUserName() {
        return username;
    }

    public void setUserName(String userName) {
        this.username = userName;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public String getTroopAction() {
        return troopAction;
    }

    public void setTroopAction(String troopAction) {
        this.troopAction = troopAction;
    }

    public HashMap<String, Object> getTroopParameters() {
        return troopParameters;
    }

    public void setTroopParameters(HashMap<String, Object> params) {
        this.troopParameters = params;
    }

    @Override
    public String toString() {
        return "TroopRequest{" +
                "object=" + object +
                ", action='" + troopAction + '\'' +
                ", params=" + troopParameters +
                ", username='" + username + '\'' +
                '}';
    }
}