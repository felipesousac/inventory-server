package com.inventory.server.controller;

import com.inventory.server.dto.category.CategoryListData;
import com.inventory.server.serialization.converter.YamlMediaType;
import com.inventory.server.service.CategoryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/categories")
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Categories", description = "Endpoints for managing categories")
public class CategorieController {

    private final CategoryService categoryService;

    public CategorieController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE,
            YamlMediaType.APPLICATION_YAML})
    public ResponseEntity<Page<CategoryListData>> listCategories(@PageableDefault(sort = {"categoryName"}) Pageable pagination) {
        return ResponseEntity.ok(categoryService.listAllCategories(pagination));
    }

}
