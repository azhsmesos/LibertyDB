package com.kuaishou.libertydb.common.util;

import java.util.List;
import java.util.Objects;

import com.google.common.collect.Lists;
import com.kuaishou.libertydb.common.Tuple;
import com.kuaishou.libertydb.common.TwoTuple;

/**
 * @author zhaozhenhang <zhaozhenhang@kuaishou.com>
 * Created on 2023-07-06
 */
public class CheckerChain {

    private List<TwoTuple<ParamChecker, ?>> paramCheckChain;

    private CheckerChain(List<TwoTuple<ParamChecker, ?>> paramCheckChain) {
        this.paramCheckChain = paramCheckChain;
    }

    public static CheckerChain newCheckerChain() {
        return new CheckerChain(Lists.newArrayList());
    }

    public <T> CheckerChain put(ParamChecker<? super T> checker, T param) {
        paramCheckChain.add(Tuple.tuple(checker, param));
        return this;
    }

    public String validate() {
        return paramCheckChain.stream()
                .map(it -> it.getFirst().validateWithLog(it.getSecond()))
                .filter(Objects::nonNull)
                .findAny()
                .orElse(null);
    }
}
