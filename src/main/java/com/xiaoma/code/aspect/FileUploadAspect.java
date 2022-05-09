package com.xiaoma.code.aspect;

import com.xiaoma.code.exception.BizException;
import com.xiaoma.code.utils.ResultData;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

import static com.xiaoma.code.enums.ResultEnum.*;

/**
 * @Author Administrator
 * @Date 2022/5/7 14:42:04
 */
@Aspect
@Component
public class FileUploadAspect {

    @Value("${file.root.path}")
    private String fileRootPath;

    @Pointcut("execution(* com.xiaoma.code.controller.FileUploadController..*(..))")
    public void pointcut() {

    }

    @Around("pointcut()")
    public Object process(ProceedingJoinPoint proceedingJoinPoint) {
        try {
            String methodName = proceedingJoinPoint.getSignature().getName();
            if ("normalFileUpload".equals(methodName) || "blockFileUpload".equals(methodName)) {
                MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
                String[] parameterNames = signature.getParameterNames();
                Object[] args = proceedingJoinPoint.getArgs();
                int filePathIndex = 0, fileNameIndex = 0, fileIndex = 0;
                for (int i = 0; i < parameterNames.length; i++) {
                    if ("filePath".equals(parameterNames[i])) {
                        filePathIndex = i;
                    } else if ("fileName".equals(parameterNames[i])) {
                        fileNameIndex = i;
                    } else if ("file".equals(parameterNames[i])) {
                        fileIndex = i;
                    }
                }
                String filePath = (String) args[filePathIndex];
                String fileName = (String) args[fileNameIndex];
                MultipartFile multipartFile = (MultipartFile) args[fileIndex];
                String fileCompletePath = fileRootPath + File.separator + filePath +File.separator + fileName;
                File file = new File(fileCompletePath);
                if (file.exists() && multipartFile.getSize() == file.length()) {
                    return new ResultData<>(SAME_FILE);
                }
            }
            return proceedingJoinPoint.proceed();
        } catch (BizException bizException) {
            return new ResultData<>(bizException);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return new ResultData<>(FAIL, throwable.getMessage());
        }
    }

}
