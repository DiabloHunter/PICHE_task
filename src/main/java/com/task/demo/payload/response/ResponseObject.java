package com.task.demo.payload.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class ResponseObject<T> {

    private T body;
    private HttpStatus status;

    private ResponseObject() {
    }

    private ResponseObject(T body, HttpStatus status) {
        this.body = body;
        this.status = status;
    }

    public T getBody() {
        return body;
    }

    public HttpStatusCode getStatus() {
        return status;
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<T> {

        private T body;
        private HttpStatus status;

        public Builder<T> withBody(T body) {
            this.body = body;
            return this;
        }

        public Builder<T> withStatus(HttpStatus status) {
            this.status = status;
            return this;
        }

        public ResponseObject<T> build() {
            return new ResponseObject<>(body, status);
        }

    }
}
