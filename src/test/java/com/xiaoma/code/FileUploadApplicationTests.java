package com.xiaoma.code;

import cn.hutool.http.HttpUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.util.HashMap;

@SpringBootTest
class FileUploadApplicationTests {

    @Test
    void contextLoads() {
        File file1 = new File("E:\\node-v16.14.2-x64.msi");
        System.out.println(file1.exists());
        System.out.println(file1.length());
        File file2 = new File("D:\\FileUpload\\root\\test\\01\\ideaIU-2021.2.4.exe");
        System.out.println(file2.exists());
        System.out.println(file2.length());
    }

    @Test
    void test1() throws IOException {
        File file = new File("E:\\ideaIU-2021.2.4.exe");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("fileMd5", "Test009");
        hashMap.put("fileName", "ideaIU-2021.2.4.exe");
        hashMap.put("filePath", "/Test/01");
        hashMap.put("file", file);
        hashMap.put("flag", false);
        String post = HttpUtil.post("http://localhost:12345/api/upload/normal", hashMap);
        System.out.println(post);
    }

    @Test
    void test2() throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile("E:\\ideaIU-2021.2.4.exe", "rw");

    }
}
