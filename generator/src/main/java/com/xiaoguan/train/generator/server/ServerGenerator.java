package com.xiaoguan.train.generator.server;


import com.xiaoguan.train.generator.util.FreemarkerUtil;

import java.io.File;
import java.util.HashMap;

public class ServerGenerator {
    static String toPath = "generator/src/main/java/com/xiaoguan/train/generator/test/";

    static {
        new File(toPath).mkdirs();
    }

    public static void main(String[] args) throws Exception {
        FreemarkerUtil.initConfig("test.ftl");
        HashMap<String, Object> param = new HashMap<>();
        param.put("domain", "Test");
        FreemarkerUtil.generator(toPath + "Test.java", param);
    }
}
