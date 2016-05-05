package com.leocai.detecdtion;

/**
 * Created by leocai on 15-12-27.
 */
public class AuthenticationState {

    public enum AuState {
        TRAIN, RETURN_PARAMETER, TRANSFORMATION, CORSS_LEVEL, WAITE_PARAMETER, RECONCILATION, WAITE_FOR_RECONCIATION;
    }

    AuState auState = AuState.TRAIN;

    public AuState getAuState() {
        return auState;
    }

    public void setAuState(AuState auState) {
        this.auState = auState;
    }
}
