package com.task.demo.payload.response;

import org.springframework.http.HttpStatusCode;

public class ResponseEntity<T> {

    private T body;
    private HttpStatusCode status;

    private ResponseEntity() {
    }

    private ResponseEntity(T body, HttpStatusCode status) {
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
        private HttpStatusCode status;

        public Builder<T> withBody(T body) {
            this.body = body;
            return this;
        }

        public Builder<T> withStatus(HttpStatusCode status) {
            this.status = status;
            return this;
        }

        public ResponseEntity<T> build() {
            return new ResponseEntity<>(body, status);
        }

    }
}
