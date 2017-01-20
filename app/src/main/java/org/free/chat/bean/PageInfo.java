package org.free.chat.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/1/12.
 * 分页数据
 */
public class PageInfo<T> {

    public static final int DEFAULT_PAGE_SIZE = 20;
    private int current;
    private int total;
    private int totalPage;
    private int pageSize = DEFAULT_PAGE_SIZE;
    private List<T> mDatas;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getTotal() {
        return total;
    }

    public int getTotalPage() {
        return total % pageSize == 0 ? total / pageSize : (total / pageSize) + 1;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<T> getDatas() {
        return mDatas;
    }

    public void setDatas(List<T> mDatas) {
        this.mDatas = mDatas;
    }

    /**
     * 是否有下一页
     * @return
     */
    public boolean hasNextPage() {
        return current * pageSize < total;
    }

    /**
     * 是否有上一页
     * @return
     */
    public boolean hasPreviousPage() {
        return current > 1;
    }

    public int nextPage() {
        return  current + 1;
    }
}
