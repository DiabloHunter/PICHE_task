package com.task.demo.service;

import com.task.demo.entity.Account;

@FunctionalInterface
public interface Operation {
    Account execute() throws Exception;
}
