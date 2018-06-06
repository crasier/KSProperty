package com.kswy.property.server.Interface;

import com.kswy.property.server.Entity.Error;


public interface OnResult<T> {
    public void onSuccess(T t);
    public void onFailed(Error error);
}
