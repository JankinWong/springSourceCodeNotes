package com.lagou.edu.aop.impl;

import com.lagou.edu.aop.dao.Dao;

public class DaoImpl implements Dao {

    @Override
    public void select() {
        System.out.println("Enter DaoImpl.select()");
    }

    @Override
    public void insert() {
        System.out.println("Enter DaoImpl.insert()");
    }

}