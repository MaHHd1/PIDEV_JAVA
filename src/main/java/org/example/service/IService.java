package org.example.service;

import java.util.List;

public interface IService<T> {
    void add(T t);
    void update(T t);
    void delete(T t);
    T getById(int id);  // 🆕 AJOUTER CETTE LIGNE
    List<T> getAll();
}