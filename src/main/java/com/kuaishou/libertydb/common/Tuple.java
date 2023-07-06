package com.kuaishou.libertydb.common;

/**
 * @author zhaozhenhang <zhaozhenhang@kuaishou.com>
 * Created on 2023-07-06
 */
public class Tuple {

    private Tuple() {
        throw new UnsupportedOperationException();
    }

    public static <F, S> TwoTuple<F, S> tuple(final F a, final S b) {
        return new TwoTuple<>(a, b);
    }
}
