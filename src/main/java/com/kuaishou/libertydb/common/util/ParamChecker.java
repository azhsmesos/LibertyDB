package com.kuaishou.libertydb.common.util;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhaozhenhang <zhaozhenhang@kuaishou.com>
 * Created on 2023-07-06
 */
@FunctionalInterface
public interface ParamChecker<T> {

    Logger logger = LoggerFactory.getLogger(ParamChecker.class);

    static ParamChecker<String> notBlankChecker(String errorMsg) {
        return param -> isBlank(param) ? errorMsg : null;
    }

    String validate(T data);

    default String validateWithLog(T data) {
        String errorInfo = validate(data);
        if (isNotBlank(errorInfo)) {
            logger.warn("invalid param: {}", data);
        }
        return errorInfo;
    }
}
