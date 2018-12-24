package com.geekymax.operation;

import com.geekymax.ot.Changes;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 一个操作的基本单位,包括客户端编号,版本信息与变更信息
 *
 * @author Max Huang
 */
public class Operation implements Serializable {
    /**
     * 客户端编号
     */
    private int clientIndex;
    /**
     * 操作基于的文档版本
     */
    private int version;
    /**
     * 文档变更
     */
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
