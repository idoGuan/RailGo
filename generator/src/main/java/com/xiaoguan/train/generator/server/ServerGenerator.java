package com.xiaoguan.train.generator.server;


import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ServerGenerator {
    static String toPath = "generator/src/main/java/com/xiaoguan/train/generator/test/";
    static String pomPath = "generator/pom.xml";

    static {
        new File(toPath).mkdirs();
    }

    public static void main(String[] args) throws Exception {

        getGeneratorPath();
//        FreemarkerUtil.initConfig("test.ftl");
//        HashMap<String, Object> param = new HashMap<>();
//        param.put("domain", "Test");
//        FreemarkerUtil.generator(toPath + "Test.java", param);
    }


    private static String getGeneratorPath() throws DocumentException {
        SAXReader saxReader = new SAXReader();
        Map<String, String> map = new HashMap<String, String>();
        map.put("pom", "http://maven.apache.org/POM/4.0.0");
        saxReader.getDocumentFactory().setXPathNamespaceURIs(map);
        Document document = saxReader.read(pomPath);
        Node node = document.selectSingleNode("//pom:configurationFile");
        System.out.println(node.getText());
        return node.getText();
    }
}
