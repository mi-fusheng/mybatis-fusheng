package com.mi.fusheng.framework.sqlnode;

import com.mi.fusheng.framework.sqlnode.iface.SqlNode;
import com.mi.fusheng.framework.utils.GenericTokenParser;
import com.mi.fusheng.framework.utils.OgnlUtils;
import com.mi.fusheng.framework.utils.TokenHandler;
import com.mi.fusheng.util.SimpleTypeRegistry;

public class TextSqlNode implements SqlNode {

    private String sqlText;

    public TextSqlNode(String sqlText) {
        this.sqlText = sqlText;
    }

    //封装SqlText的对象最了解如何对它进行判断
    public boolean isDynamic() {
        if (sqlText.indexOf("${") > -1) {
            return true;
        }

        return false;
    }

    @Override
    public void apply(DynamicContext context) {
        //处理${}中被分词出来的名称，比如user.username
        BindingTokenHandler tokenHandler = new BindingTokenHandler(context);

        GenericTokenParser tokenParser = new GenericTokenParser("${", "}", tokenHandler);
        String sql = tokenParser.parse(sqlText);
        context.appendSql(sql);
    }

    private static class BindingTokenHandler implements TokenHandler {
        private DynamicContext context;

        public BindingTokenHandler(DynamicContext context) {
            this.context = context;
        }

        /**
         * content：就是${}中的名字，比如${user.username},那么content就是user.username
         */
        @Override
        public String handleToken(String content) {
            Object paramObject = context.getBindings().get("_parameter");
            //如果入参对象为null，则返回“”
            if (paramObject==null){
                return "";
            }else if (SimpleTypeRegistry.isSimpleType(paramObject.getClass())){
                //如果入参对象是简单类型或者Sring，则直接根据字符串转换成指定的类型
                return paramObject.toString();
            }


            //如果是自定义的Java对象，则需要使用OGNL去入参对象中获取值，然后返回
            Object value = OgnlUtils.getValue(content, paramObject);
            return value.toString();
        }
    }
}
