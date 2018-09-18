package com.tangula.android.mvp.module;

import java.util.List;

public class DefaultPaginationModule<T> implements PaginationModule<T> {

    private int pageIndex;

    private int pageSize;

    private int total;

    private List<T> items;

    public DefaultPaginationModule(){}

    @Override
    public int getPageIndex() {
        return pageIndex;
    }

    @Override
    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

    @Override
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public int getTotal() {
        return total;
    }

    @Override
    public void setTotal(int total) {
        this.total = total;
    }

    @Override
    public List<T> getItems() {
        return items;
    }

    @Override
    public void setItems(List<T> list) {
        this.items = list;
    }
}
