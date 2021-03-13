package com.mi.fusheng.framework.builder;

import com.mi.fusheng.framework.sqlnode.IfSqlNode;
import com.mi.fusheng.framework.sqlnode.StaticTextSqlNode;
import com.mi.fusheng.framework.sqlnode.TextSqlNode;
import com.mi.fusheng.framework.sqlnode.iface.MixedSqlNode;
import com.mi.fusheng.framework.sqlnode.iface.SqlNode;
import com.mi.fusheng.framework.sqlsource.DynamicSqlSource;
import com.mi.fusheng.framework.sqlsource.RawSqlSource;
import com.mi.fusheng.framework.sqlsource.SqlSource;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * 专门用来解析SQL文本节点和各自动态标签的元素节点
 */
public class XMLScriptBuilder {


    private boolean isDynamic;

    public SqlSource parseScriptNode(Element selectElement) {
        //解析select标签下的所有SQL脚本信息，比如sql文本片段，动态标签等，最终封装成SqlNode集合

        MixedSqlNode rootSqlNode = parseDynamicTags(selectElement);
        //在解析SqlNode的时候，我们需要知道这sql文本是否包含${}，或者sql脚本中是否包含动态标签


        SqlSource sqlSource = null;
        //包含${}和动态标签的这种SQL信息，封装到DynamicSqlSource
        if (isDynamic) {
            sqlSource = new DynamicSqlSource(rootSqlNode);
        } else {
            sqlSource = new RawSqlSource(rootSqlNode);
        }

        //如果整个SQL信息中只包含#{}或无特殊的字符，则将SQL信息封装到RawSqlSource

        return sqlSource;
    }

    private MixedSqlNode parseDynamicTags(Element selectElement) {
        List<SqlNode> sqlNodes = new ArrayList<>();

        //获取select标签的子节点
        int nodeCount = selectElement.nodeCount();

        //遍历所有节点
        for (int i = 0; i < nodeCount; i++) {
            Node node = selectElement.node(i);

            // 判断节点是文本节点还是Element节点
            if (node instanceof Text) {
                //获取SQL文本
                String sqlText = node.getText().trim();
                if (sqlText == null || "".equals(sqlText)) {
                    continue;
                }

                // 如果是文本节点，判断文本节点是否包含${}
                TextSqlNode textSqlNode = new TextSqlNode(sqlText);
                // 如果包含${}，那么将文本节点的SQL文本封装TextSqlNode
                if (textSqlNode.isDynamic()) {
                    sqlNodes.add(textSqlNode);
                    isDynamic = true;
                } else {
                    // 如果不包含${}，那么将文本节点的SQL文本封装到StaticTextSqlNode
                    sqlNodes.add(new StaticTextSqlNode(sqlText));
                }

            } else if (node instanceof Element) {
                //如果是Element节点，则需要获取节点名称
                Element element = (Element) node;
                String name = element.getName();
                //此处使用策略模式去优化
                //根据名称创建对应Element的SqlNode对象，封装到Element的节点数据，比如if标签IfSqlNode
                if ("if".equals(name)) {
                    String test = element.attributeValue("test");
                    //递归给if标签动态解析，获取它的子标签SqlNode节点集合
                    MixedSqlNode mixedSqlNode = parseDynamicTags(element);

                    IfSqlNode ifSqlNode = new IfSqlNode(test, mixedSqlNode);
                    sqlNodes.add(ifSqlNode);
                } else if ("where".equals(name)) {
                    //，，，，，，，，，
                }

            }
        }
        return new MixedSqlNode(sqlNodes);
    }
}
