package com.xiaoma.code.test;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * @Author Administrator
 * @Date 2022/5/10 09:33:49
 */
public class BufferedAndNormal {

    public static void main(String[] args) throws Exception {
        File bufferedInputStream = new File("E:\\ideaIU-2021.2.4.exe");
        File bufferedOutputStream = new File("D:\\FileUpload\\chunks\\ideaIU-2021.2.42.exe");
        long bufferedStartTime = System.currentTimeMillis();
        FileOperator.copyWithBufferedStream(bufferedInputStream, bufferedOutputStream);
        System.out.println(System.currentTimeMillis() - bufferedStartTime);
        System.out.println("========== 分割线 ==========");
        TimeUnit.SECONDS.sleep(5);
        File fileInputStream = new File("E:\\ideaIU-2021.2.4.exe");
        File fileOutputStream = new File("D:\\FileUpload\\chunks\\ideaIU-2021.2.41.exe");
        long fileStartTime = System.currentTimeMillis();
        FileOperator.copyWithFileStream(fileInputStream, fileOutputStream);
        System.out.println(System.currentTimeMillis() - fileStartTime);
    }
}
