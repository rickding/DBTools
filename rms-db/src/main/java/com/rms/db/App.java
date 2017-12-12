package com.rms.db;

import javax.annotation.Resource;

/**
 * Hello world!
 */
public class App {
    @Resource
    private DBService dbService;

    public static void main(String[] args) {
        System.out.println("Hello World!");
    }
}
