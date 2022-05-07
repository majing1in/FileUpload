package com.xiaoma.code.aspect;

import com.xiaoma.code.entity.FileInfo;
import com.xiaoma.code.exception.BizException;
import com.xiaoma.code.utils.ResultData;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @Author Administrator
 * @Date 2022/5/7 14:42:04
 */
@Aspect
@Component
public class FileUploadAspect {

    @Pointcut("execution(* com.xiaoma.code.controller.FileUploadController..*(..))")
    public void pointcut1() {

    }

    @Pointcut("execution(* com.xiaoma.code.dao.FileInfoDao..*(..))")
    public void pointcut2() {

    }

    @Around("pointcut1()")
    public Object process1(ProceedingJoinPoint proceedingJoinPoint) {
        try {
            return proceedingJoinPoint.proceed();
        } catch (BizException bizException) {
            return new ResultData<>(bizException);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }

    @Around("pointcut2()")
    public Object process2(ProceedingJoinPoint proceedingJoinPoint) {
        try {
            Object[] args = proceedingJoinPoint.getArgs();
            String methodName = proceedingJoinPoint.getSignature().getName();
            for (Object arg : args) {
                if (arg instanceof FileInfo) {
                    FileInfo fileInfo = (FileInfo) arg;
                    fileInfo.setUpdateTime(new Date());
                    if ("addFileInfo".equals(methodName)) {
                        fileInfo.setCreateTime(new Date());
                    }
                }
            }
            return proceedingJoinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }
}
