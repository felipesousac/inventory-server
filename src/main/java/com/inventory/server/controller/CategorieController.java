package com.inventory.server.controller;

import com.inventory.server.dto.category.CategoryListData;
import com.inventory.server.service.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/categories")
public class CategorieController {

    private final CategoryService categoryService;

    public CategorieController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<Page<CategoryListData>> listCategories(@PageableDefault(sort = {"categoryName"}) Pageable pagination) {
        return ResponseEntity.ok(categoryService.listAllCategories(pagination));
    }

}
