package org.free.chat.common;

/**
 * Created by Administrator on 2016/12/29.
 * 页面交互接口
 */
public interface Interactable {

    /**
     * 请求处理成功
     */
    void success();

    /**
     * 请求失败
     */
    void failed();

    /**
     * 返回的数据为空
     */
    void empty();

    /**
     * 设置请求失败的图片
     * @param resId 图片id
     */
    void setFailImage(int resId);

    /**
     * 设置请求失败的文字提示
     * @param resId 请求文字id
     */
    void setFailText(int resId);

    /**
     * 失败后从新加载数据
     */
    void reload();

    /**
     * 加载数据
     */
    void load();

    /**
     * 加载数据完成
     */
    void loadFinished();
}
