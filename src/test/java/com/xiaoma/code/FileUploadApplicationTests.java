package com.xiaoma.code;

import cn.hutool.http.HttpUtil;

import com.xiaoma.code.constants.Constant;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@SpringBootTest
class FileUploadApplicationTests {

    @Test
    void contextLoads() {
        File file1 = new File("D:\\Code Warehouse\\FileUpload\\chunks\\");
        File[] files = file1.listFiles();
        System.out.println(files.length);
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

    //测试文件分块
    @Test
    public void testChunk() throws IOException {
        //源文件
        File sourceFile = new File("F:\\ChromeDownload\\PCQQ2021.exe");
        //块文件目录
        String chunkFileFolder = "D:\\Code Warehouse\\FileUpload\\chunks\\";

        //先定义块文件大小
        long chunkFileSize = 10 * 1024 * 1024;

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
                hashMap.put("fileName", "PCQQ2021.exe");
                String post = HttpUtil.post("http://localhost:12345/api/upload/block", hashMap);
                System.out.println(post);
            }).start();
        }

    }


    //测试文件合并
    @Test
    public void testMergeFile() throws IOException {
        long currentTimeMillis = System.currentTimeMillis();
        //块文件目录
        String chunkFileFolderPath = "D:\\FileUpload\\chunks\\";
        //块文件目录对象
        File chunkFileFolder = new File(chunkFileFolderPath);
        //块文件列表
        File[] files = chunkFileFolder.listFiles();
        //将块文件排序，按名称升序
        List<File> fileList = Arrays.asList(files);
        Collections.sort(fileList, (o1, o2) -> {
            if (Integer.parseInt(o1.getName()) > Integer.parseInt(o2.getName())) {
                return 1;
            }
            return -1;

        });

        //合并文件
        File mergeFile = new File("D:\\FileUpload\\datas\\VSCodeUserSetup-x64-1.62.3.exe");
        //创建新文件
        boolean newFile = mergeFile.createNewFile();

        //创建写对象
        RandomAccessFile raf_write = new RandomAccessFile(mergeFile, "rw");

        byte[] b = new byte[1024];
        for (File chunkFile : fileList) {
            //创建一个读块文件的对象
            RandomAccessFile raf_read = new RandomAccessFile(chunkFile, "r");
            int len = -1;
            while ((len = raf_read.read(b)) != -1) {
                raf_write.write(b, 0, len);
            }
            raf_read.close();
        }
        raf_write.close();
        System.out.println(System.currentTimeMillis() - currentTimeMillis);
    }

    @Test
    void test2() {
        File file = new File("D:\\FileUpload\\root");
        findAllTempFiles(file);
    }

    public void findAllTempFiles(File file) {
        File[] files = file.listFiles();
        for (File value : files) {
            if (!value.isDirectory()) {
                continue;
            }
            if (value.getName().startsWith(Constant.TEMP_FILE_PREFIX_1)) {
                File[] listFiles = value.listFiles();
                for (File tempFile : listFiles) {
                    if (tempFile.exists()) {
                        tempFile.delete();
                    }
                }
                value.delete();
                return;
            }
            findAllTempFiles(value);
        }
    }
}
