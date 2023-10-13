package error;

import java.util.HashMap;

public enum ErrorType {
    ILLEGAL_SYMBOL, //a,非法符号
    REDEFINED_SYMBOL, //b,名字重定义
    UNDEFINED_SYMBOL, //c,未定义的名字
    MIS_MATCH_PARAM_NUM, //d,函数参数个数不匹配
    MIS_MATCH_PARAM_TYPE,//e,函数参数类型不匹配
    ERROR_USED_RETURN, //f,无返回值的函数存在不匹配的return语句
    LACK_OF_RETURN, //g,有返回值的函数缺少return语句
    ASSIGN_TO_CONST, //h,不能改变常量的值
    LACK_OF_SEMICN, //i,缺少分号
    LACK_OF_RPARENT, //j,缺少右小括号’)’
    LACK_OF_RBARCK, //k,缺少右中括号’]’
    MISS_MATCH_IN_STRING, //l,printf中格式字符与表达式个数不匹配
    ERROR_USED_BREAK_OR_CONTINUE; //m,在非循环块中使用break和continue语句

    public static final HashMap<ErrorType,String> ErrorType2Id = new HashMap<>();

    static {
        ErrorType2Id.put(ILLEGAL_SYMBOL,"a");
        ErrorType2Id.put(REDEFINED_SYMBOL,"b");
        ErrorType2Id.put(UNDEFINED_SYMBOL,"c");
        ErrorType2Id.put(MIS_MATCH_PARAM_NUM,"d");
        ErrorType2Id.put(MIS_MATCH_PARAM_TYPE,"e");
        ErrorType2Id.put(ERROR_USED_RETURN,"f");
        ErrorType2Id.put(LACK_OF_RETURN,"g");
        ErrorType2Id.put(ASSIGN_TO_CONST,"h");
        ErrorType2Id.put(LACK_OF_SEMICN,"i");
        ErrorType2Id.put(LACK_OF_RPARENT,"j");
        ErrorType2Id.put(LACK_OF_RBARCK,"k");
        ErrorType2Id.put(MISS_MATCH_IN_STRING,"l");
        ErrorType2Id.put(ERROR_USED_BREAK_OR_CONTINUE,"m");
    }
}
