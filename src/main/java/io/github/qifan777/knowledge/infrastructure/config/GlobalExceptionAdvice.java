package io.github.qifan777.knowledge.infrastructure.config;

import cn.dev33.satoken.exception.DisableServiceException;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotRoleException;
import io.qifan.infrastructure.common.constants.ResultCode;
import io.qifan.infrastructure.common.exception.BusinessException;
import io.qifan.infrastructure.common.exception.SystemException;
import io.qifan.infrastructure.common.model.R;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionAdvice {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<R<String>> handleBusinessException(BusinessException e) {
        log.error("业务异常", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(R.fail(e.getResultCode(), e.getMessage()));
    }

    @ExceptionHandler(SystemException.class)
    public ResponseEntity<R<String>> handleSystemException(SystemException e) {
        log.error("系统异常", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(R.fail(ResultCode.SystemError));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<R<String>> handleException(Exception e) {
        log.error("系统异常", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(R.fail(ResultCode.SystemError));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<R<String>> handleValidateException(ConstraintViolationException e) {
        log.warn("校验异常", e);
        // 不合格的字段，可能有多个，只需要返回其中一个提示用户就行
        // 比如密码为空
        ArrayList<ConstraintViolation<?>> constraintViolations = new ArrayList<>(
            e.getConstraintViolations());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(R.fail(ResultCode.ValidateError,
                constraintViolations.get(0).getMessage()));

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<R<String>> handleValidateExceptionForSpring(
        MethodArgumentNotValidException e) {
        log.warn("校验异常", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(R.fail(ResultCode.ValidateError,
                e.getBindingResult().getAllErrors().get(0)
                    .getDefaultMessage()));
    }

    @ExceptionHandler(NotLoginException.class)
    public ResponseEntity<R<String>> handleNotLogin(NotLoginException e) {
        log.error("未登录", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(R.fail(ResultCode.Unauthorized));
    }

    @ExceptionHandler(NotRoleException.class)
    public ResponseEntity<R<String>> handleNotRole(NotRoleException e) {
        log.error("角色校验异常", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(R.fail(ResultCode.NotGrant, e.getMessage()));
    }

    @ExceptionHandler(DisableServiceException.class)
    public ResponseEntity<R<String>> handleDisabledException(DisableServiceException e) {
        log.error("账号封禁", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(R.fail(ResultCode.StatusHasInvalid, "账号已被封禁"));
    }


}
