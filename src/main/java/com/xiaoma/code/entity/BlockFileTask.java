package com.xiaoma.code.entity;

import lombok.Data;

import java.util.Date;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author Administrator
 * @Date 2022/5/9 13:25:34
 */
@Data
public class BlockFileTask {

    private Integer tempFileCount;

    private Future<Boolean> taskFuture;

    private Date createTime;

    private AtomicInteger fileSteams = new AtomicInteger(0);
}
