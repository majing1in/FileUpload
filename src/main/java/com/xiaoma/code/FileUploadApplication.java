package com.xiaoma.code;

import com.xiaoma.code.enums.BizEnum;
import com.xiaoma.code.exception.BizException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.File;

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableScheduling
public class FileUploadApplication implements CommandLineRunner {

    @Value("${file.root.path}")
    private String fileRootPath;

    public static void main(String[] args) {
        SpringApplication.run(FileUploadApplication.class, args);
    }

    public void run(String... args) throws Exception {
        // 创建保存文件的根目录
        File file = new File(fileRootPath);
        if (!file.exists() && !file.mkdir()) {
            throw new BizException(BizEnum.ROOT_PATH_EXCEPTION);
        }
    }
}
