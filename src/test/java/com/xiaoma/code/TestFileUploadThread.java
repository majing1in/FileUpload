package com.xiaoma.code;

import cn.hutool.http.HttpUtil;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

/**
 * @Author Administrator
 * @Date 2022/5/9 09:30:54
 */
public class TestFileUploadThread {

    public static void main(String[] args) throws IOException {
        //源文件
        File sourceFile = new File("E:\\AdobeAcrobatProDC_setup.rar");
        //块文件目录
        String chunkFileFolder = "D:\\FileUpload\\chunks\\";

        //先定义块文件大小
        long chunkFileSize = 64 * 1024 * 1024;

        //块数
        long chunkFileNum = (long) Math.ceil(sourceFile.length() * 1.0 / chunkFileSize);

        //创建读文件的对象
        RandomAccessFile raf_read = new RandomAccessFile(sourceFile, "r");

        //缓冲区
        byte[] b = new byte[1024];
        for (int i = 0; i < chunkFileNum; i++) {
            //块文件
            File chunkFile = new File(chunkFileFolder + i);
            //创建向块文件的写对象
            RandomAccessFile raf_write = new RandomAccessFile(chunkFile, "rw");
            int len = -1;

            while ((len = raf_read.read(b)) != -1) {

                raf_write.write(b, 0, len);
                //如果块文件的大小达到 1M开始写下一块儿
                if (chunkFile.length() >= chunkFileSize) {
                    break;
                }
            }
            raf_write.close();
        }
        raf_read.close();

        File file = new File(chunkFileFolder);
        File[] files = file.listFiles();
        if (files == null) {
            return;
        }
        int i1 = files.length - 1;
        for (File fileTemp : files) {
            new Thread(() -> {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("chunks", i1);
                hashMap.put("chunk", fileTemp.getName());
                hashMap.put("filePath", "/Test/02");
                hashMap.put("file", fileTemp);
                hashMap.put("fileName", "AdobeAcrobatProDC_setup.rar");
                String post = HttpUtil.post("http://localhost:12345/api/upload/block", hashMap);
                System.out.println(post);
            }).start();
        }
    }
}
