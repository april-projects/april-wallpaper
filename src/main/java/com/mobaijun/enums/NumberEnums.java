package com.mobaijun.enums;

/**
 * software：IntelliJ IDEA 2022.1
 * enum name: NumberEnums
 * enum description： 常用数字
 *
 * @author MoBaiJun 2022/8/8 14:51
 */
public enum NumberEnums {

    /**
     *
     */
    ZERO(0),
    /**
     * 1
     */
    ONE(1),

    /**
     * 3
     */
    THREE(3);

    private final int code;

    NumberEnums(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
