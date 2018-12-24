package com.geekymax.ot;


/**
 * 抽象类,表示一个文档变更
 * @author Max Huang
 * @author Stas
 */
public abstract class AbstractChange {

    /**
     * 将该变更应用于文档Text
     * @param pos 变更应用位置
     * @param text 变更应用文档对象
     * @return 返回变更后的文档对象
     * @throws ValidationException
     */
    abstract Text apply(int pos, Text text) throws ValidationException;

    /**
     * 得到该变更,应用后的offset
     * @return
     */
    abstract int offset();

    int revision() {
        return 0;
    }
}
