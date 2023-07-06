package com.kuaishou.libertydb.common.util;

import static com.kuaishou.libertydb.common.util.ParamChecker.notBlankChecker;

import java.util.Arrays;

/**
 * @author zhaozhenhang <zhaozhenhang@kuaishou.com>
 * Created on 2023-07-06
 */
public class CheckParamUtil {

    public static void checkParamIsNotBlank(String... param) {
        CheckerChain checkerChain = CheckerChain.newCheckerChain();
        boolean checker = Arrays.stream(param).allMatch(it ->
                checkerChain.put(notBlankChecker("参数不可为null"), it).validate() == null);
        if (checker) {

        }
    }
}
