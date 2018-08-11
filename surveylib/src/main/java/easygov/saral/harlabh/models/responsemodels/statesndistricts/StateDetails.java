package easygov.saral.harlabh.models.responsemodels.statesndistricts;

/**
 * Created by apoorv on 17/08/17.
 */


public class StateDetails {
    public String state_code;
    public String state_name;
    public int states_id;

    public String getState_code() {
        return state_code;
    }

    public void setState_code(String state_code) {
        this.state_code = state_code;
    }

    public String getState_name() {
        return state_name;
    }

    public void setState_name(String state_name) {
        this.state_name = state_name;
    }

    public int getStates_id() {
        return states_id;
    }

    public void setStates_id(int states_id) {
        this.states_id = states_id;
    }
}
