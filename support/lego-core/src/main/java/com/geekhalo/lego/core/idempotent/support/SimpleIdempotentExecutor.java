package com.geekhalo.lego.core.idempotent.support;

import com.geekhalo.lego.annotation.idempotent.IdempotentHandleType;
import com.geekhalo.lego.common.idempotent.ConcurrentRequestException;
import com.geekhalo.lego.common.idempotent.RepeatedSubmitException;
import com.geekhalo.lego.core.idempotent.IdempotentExecutor;
import com.geekhalo.lego.core.idempotent.IdempotentMeta;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInvocation;

/**
 * Created by taoli on 2022/11/4.
 * gitee : https://gitee.com/litao851025/lego
 * 编程就像玩 Lego
 */
@Slf4j
public class SimpleIdempotentExecutor
    implements IdempotentExecutor {
    private final IdempotentMeta meta;
    private final IdempotentKeyParser idempotentKeyParser;
    private final ExecutionResultSerializer serializer;
    private final ExecutionRecordRepository executionRecordRepository;

    protected SimpleIdempotentExecutor(IdempotentMeta meta,
                                       IdempotentKeyParser idempotentKeyParser,
                                       ExecutionResultSerializer serializer,
                                       ExecutionRecordRepository executionRecordRepository) {
        this.meta = meta;
        this.idempotentKeyParser = idempotentKeyParser;
        this.serializer = serializer;
        this.executionRecordRepository = executionRecordRepository;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        String key = this.idempotentKeyParser.parse(this.meta.paramNames(), invocation.getArguments(), this.meta.keyEl());
        ExecutionRecord executionRecord = getOrCreateExecutionRecord(this.meta.group(), key);
        if (executionRecord.isDone()){
            // 已完成，直接使用上次执行结果

            // 异常中断逻辑
            if (this.meta.handleType() == IdempotentHandleType.ERROR){
                log.info("In Error Mode, throw RepeatedSubmitException");
                throw new RepeatedSubmitException();
            }

            // 返回上次结果
            if (executionRecord.isSuccess()){
                log.info("result is success for {}-{}, return last result {}",this.meta.group(), key, executionRecord.getResult());
                String result = executionRecord.getResult();
                return this.serializer.deserializeResult(result, this.meta.returnType());
            }else {
                log.info("result is error for {}-{}, return exception {}", this.meta.group(), key, executionRecord.getResult());
                String result = executionRecord.getResult();
                Exception exception = this.serializer.deserializeException(result);
                throw exception;
            }
        }

        if (executionRecord.isIng()){
            log.info("Method is Running for key {}-{}", this.meta.group(), key);
            // 进行中，抛出异常
            throw new ConcurrentRequestException();
        }

        try {
            log.info("Method is no Run for key {}-{}", this.meta.group(), key);
            // 执行成功，保存执行结果
            Object result = invocation.proceed();
            String resultJson = this.serializer.serializeResult(result);
            executionRecord.success(resultJson);
            updateExecutionRecord(executionRecord);
            log.info("Method is success for key {}-{}, result is {}", this.meta.group(), key, resultJson);
            return result;
        }catch (Exception e){
            // 执行异常，保存异常结果
            String errorJson = this.serializer.serializeException(e);
            executionRecord.error(errorJson);
            updateExecutionRecord(executionRecord);
            log.info("Method is error for key {}-{}, result is {}", this.meta.group(), key, errorJson);
            throw e;
        }
    }

    protected void updateExecutionRecord(ExecutionRecord executionRecord){
        this.executionRecordRepository.update(executionRecord);
    }

    protected ExecutionRecord getOrCreateExecutionRecord(int group, String key){
        return this.executionRecordRepository.getOrCreate(group, key);
    }

}
