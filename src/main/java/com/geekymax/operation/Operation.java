package com.geekymax.operation;

import com.geekymax.ot.Changes;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Max Huang
 */
public class Operation implements Serializable {
    private int clientIndex;
    private int version;
    private Changes textChange;

    public Operation(int clientIndex, int version, Changes textChange) {
        this.clientIndex = clientIndex;
        this.version = version;
        this.textChange = textChange;
    }


    public int getClientIndex() {
        return clientIndex;
    }

    public void setClientIndex(int clientIndex) {
        this.clientIndex = clientIndex;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Changes getTextChange() {
        return textChange;
    }

    public void setTextChange(Changes textChange) {
        this.textChange = textChange;
    }

    @Override
    public String toString() {
        return new SimpleDateFormat("mm:ss SSS   ").format(new Date()) + "" + clientIndex + ";" + version + ";" + textChange;
    }
}
