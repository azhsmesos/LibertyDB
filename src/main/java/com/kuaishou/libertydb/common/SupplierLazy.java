package com.kuaishou.libertydb.common;

import java.util.function.Supplier;

/**
 * @author zhaozhenhang <zhaozhenhang@kuaishou.com>
 * Created on 2023-07-06
 */
public class SupplierLazy<T>  implements Supplier<T> {

    private final Supplier<? extends T> supplier;

    private T value;

    private SupplierLazy(Supplier<? extends T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public T get() {
        if (value == null) {
            synchronized (this) {
                T newValue = supplier.get();
                if (newValue == null) {
                    throw new RuntimeException("lazy value not be null");
                }
                value = newValue;
            }
        }
        return value;
    }

    public static <T> SupplierLazy<T> lazy(Supplier<? extends T> supplier) {
        return new SupplierLazy<>(supplier);
    }
}
