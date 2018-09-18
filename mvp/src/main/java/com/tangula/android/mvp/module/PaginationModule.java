package com.tangula.android.mvp.module;

import java.util.List;

public interface PaginationModule<T> extends Module {

    int getPageIndex();

    void setPageIndex(int index);

    int getPageSize();

    void setPageSize(int pageSize);

    int getTotal();

    void setTotal(int total);

    List<T> getItems();

    void setItems(List<T> list);

}
