package com.portfolio.demo.project.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Result<T> {
    private int count;
    private T data;

    public Result(int count, T data) {
        this.count = count;
        this.data = data;
    }

    public Result(T data) {
        this.data = data;

        if (data instanceof List<?> || data instanceof ArrayList<?>) {
            this.count = ((List<?>) data).size();
        } else if (data.getClass().isArray() || data instanceof Object[]) {
            this.count = ((Object[]) data).length;
        } else {
            this.count = 1;
        }
    }
}
