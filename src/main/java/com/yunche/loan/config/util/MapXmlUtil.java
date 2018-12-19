package com.yunche.loan.config.util;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;
import java.util.Map.Entry;

import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class MapXmlUtil {
    @SuppressWarnings("unchecked")
    public static String createXmlByMap(Map<String, Object> map,
                                        String parentName) {
        //获取map的key对应的value
        Map<String, Object> rootMap=(Map<String, Object>)map.get(parentName);
        if (rootMap==null) {
            rootMap=map;
        }
        Document doc = DocumentHelper.createDocument();
        //设置根节点
        doc.addElement(parentName);
        String xml = iteratorXml(doc.getRootElement(), parentName, rootMap);
        return formatXML(xml);
    }

    /**
     * 循环遍历params创建xml节点
     * @param element 根节点
     * @param parentName 子节点名字
     * @param params map数据
     * @return String-->Xml
     */
    @SuppressWarnings("unchecked")
    public static String iteratorXml(Element element, String parentName,
                                     Map<String, Object> params) {
        Element e = element.addElement(parentName);
        Set<String> set = params.keySet();
        for (Iterator<String> it = set.iterator(); it.hasNext();) {
            String key = (String) it.next();
            if (params.get(key) instanceof Map) {
                iteratorXml(e, key, (Map<String, Object>) params.get(key));
            } else if (params.get(key) instanceof List) {
                List<Object> list = (ArrayList<Object>) params.get(key);
                for (int i = 0; i < list.size(); i++) {
                    iteratorXml(e, key, (Map<String, Object>) list.get(i));
                }
            } else {
                String value = params.get(key) == null ? "" : params.get(key)
                        .toString();
                e.addElement(key).addText(value);
                // e.addElement(key).addCDATA(value);
            }
        }
        return e.asXML();
    }

    /**
     * 格式化xml
     * @param xml
     * @return
     */
    public static String formatXML(String xml) {
        String requestXML = null;
        XMLWriter writer = null;
        Document document = null;
        try {
            SAXReader reader = new SAXReader();
            document = reader.read(new StringReader(xml));
            if (document != null) {
                StringWriter stringWriter = new StringWriter();
                OutputFormat format = new OutputFormat(" ", true);// 格式化，每一级前的空格
                format.setNewLineAfterDeclaration(false); // xml声明与内容是否添加空行
                format.setSuppressDeclaration(false); // 是否设置xml声明头部 false：添加
                format.setNewlines(true); // 设置分行
                format.setEncoding("GB2312");
                writer = new XMLWriter(stringWriter, format);
                writer.write(document);
                writer.flush();
                requestXML = stringWriter.getBuffer().toString();
            }
            return requestXML;
        } catch (Exception e1) {
            e1.printStackTrace();
            return null;
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {

                }
            }
        }
    }



    @SuppressWarnings("rawtypes")
    public static Map<String, Object> Xml2Map(String str1) throws Exception {
        SAXReader saxReader = new SAXReader();
        Document doc = saxReader.read(new ByteArrayInputStream(str1.getBytes("GBK")));
        Map<String, Object> map = new HashMap<String, Object>();
        if(doc == null)
            return map;
        Element root = doc.getRootElement();
        for (Iterator iterator = root.elementIterator(); iterator.hasNext();) {
            Element e = (Element) iterator.next();
            //System.out.println(e.getName());
            List list = e.elements();
            if(list.size() > 0){
                map.put(e.getName(), Dom2Map(e));
            }else
                map.put(e.getName(), e.getText());
        }
        return map;
    }


    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Map Dom2Map(Element e){
        Map map = new HashMap();
        List list = e.elements();
        if(list.size() > 0){
            for (int i = 0;i < list.size(); i++) {
                Element iter = (Element) list.get(i);
                List mapList = new ArrayList();
                if(iter.elements().size() > 0){
                    Map m = Dom2Map(iter);
                    if(map.get(iter.getName()) != null){
                        Object obj = map.get(iter.getName());
                        if(!obj.getClass().getName().equals("java.util.ArrayList")){
                            mapList = new ArrayList();
                            mapList.add(obj);
                            mapList.add(m);
                        }
                        if(obj.getClass().getName().equals("java.util.ArrayList")){
                            mapList = (List) obj;
                            mapList.add(m);
                        }
                        map.put(iter.getName(), mapList);
                    }else
                        map.put(iter.getName(), m);
                }
                else{
                    if(map.get(iter.getName()) != null){
                        Object obj = map.get(iter.getName());
                        if(!obj.getClass().getName().equals("java.util.ArrayList")){
                            mapList = new ArrayList();
                            mapList.add(obj);
                            mapList.add(iter.getText());
                        }
                        if(obj.getClass().getName().equals("java.util.ArrayList")){
                            mapList = (List) obj;
                            mapList.add(iter.getText());
                        }
                        map.put(iter.getName(), mapList);
                    }else
                        map.put(iter.getName(), iter.getText());//公共map resultCode=0
                }
            }
        }else
            map.put(e.getName(), e.getText());
        return map;
    }

}
