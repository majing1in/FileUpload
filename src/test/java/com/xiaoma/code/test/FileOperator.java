package com.xiaoma.code.test;

import java.io.*;

public class FileOperator {

    /** buffer size in bytes */
    final static int BUFFER_SIZE = 8192;

    /**
     * copy file using FileInputStream & FileOutputStream

     * @param src copy from
     * @param dest copy to

     * @return;
     */
    public static void copyWithFileStream(File src, File dest){
        FileInputStream input = null;
        FileOutputStream output = null;

        try {
            input = new FileInputStream(src);
            output = new FileOutputStream(dest);

            byte[] buffer = new byte[BUFFER_SIZE];
            int copySize;

            while ((copySize = input.read(buffer)) > 0){
                output.write(buffer, 0, copySize);
                output.flush();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                input.close();
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * copy file using BufferedInputStream & BufferedOutputStream

     * @param src copy from file
     * @param dest copy to file

     * @return;
     */
    public static void copyWithBufferedStream(File src, File dest){
        BufferedInputStream bufferedInput = null;
        BufferedOutputStream bufferedOutput = null;
        try {
            bufferedInput = new BufferedInputStream(new FileInputStream(src));
            bufferedOutput = new BufferedOutputStream(new FileOutputStream(dest));

            byte[] buffer = new byte[BUFFER_SIZE];
            int copySize;

            while ((copySize = bufferedInput.read(buffer)) > 0){
                bufferedOutput.write(buffer, 0, copySize);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedInput.close();
                bufferedOutput.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}