package com.example.shopping.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.shopping.entity.Product;
import com.example.shopping.repository.ProductRepository;

@Service
@Transactional
public class CatalogServiceImpl implements CatalogService {
    private final ProductRepository productRepository;

    public CatalogServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return productRepository.selectAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Product findById(String id) {
        return productRepository.selectById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findPage(int page, int size) {
        return productRepository.selectPage(page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public int countAll() {
        return productRepository.countAll();
    }
}