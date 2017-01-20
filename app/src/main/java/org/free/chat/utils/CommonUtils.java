package org.free.chat.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * Created by Administrator on 2017/1/6.
 */
public class CommonUtils {

    public static String converterToFirstSpell(String src){
        if (StringUtils.isBlank(src)) {
            return "#";
        } else {
            char[] nameChar = src.toCharArray();
            HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
            defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
            defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
            char firstChar = nameChar[0];
            if (firstChar > 128) {
                try {
                    return PinyinHelper.toHanyuPinyinStringArray(firstChar, defaultFormat)[0].charAt(0) + "";
                } catch (Exception e) {
                    return "#";
                }

            } else {
                return (firstChar + "").toUpperCase();
            }
        }
    }

}
