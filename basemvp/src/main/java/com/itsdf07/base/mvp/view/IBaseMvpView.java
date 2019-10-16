package com.itsdf07.base.mvp.view;

/**
 * @Description:
 * @Author itsdf07
 * @Date 2019/10/16
 */
public interface IBaseMvpView<V> {
    /**
     * 显示默认内容的加载框
     */
    void showLoading();

    /**
     * 显示自定义内容的加载框
     *
     * @param content
     */
    void showLoading(String content);

    /**
     * 隐藏加载框
     */
    void hideLoading();

    /**
     * 显示Toast
     *
     * @param content
     */
    void showToast(String content);

    /**
     * 此方法是为了当Presenter中需要获取上下文对象时，传递上下文对象，而不是让Presenter直接持有上下  文对象
     *
     * @return
     */
    V getSelfActivity();
}
