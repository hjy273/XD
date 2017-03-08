package com.tyb.xd.interfacelistener;

import java.util.List;

/**
 * 数据加载的接口
 */
public interface ServiecePoolDataLoadListener {
    /**
     * data数据是新增加的数据
     * 所以应该在实现该接口的对象的listdata数组中新增这些数据
     * 根据refreshType刷新的类型进行将数据添加到结尾或者开头
     * @param data
     * @param refreshType
     * @param contentType
     */
    public void setData(List<Object> data, int refreshType,int contentType);
}
