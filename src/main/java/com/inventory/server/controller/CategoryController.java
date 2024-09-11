package com.inventory.server.controller;

import com.inventory.server.dto.category.CategoryListData;
import com.inventory.server.dto.category.CreateCategoryData;
import com.inventory.server.infra.exception.CategoryAlreadyCreatedException;
import com.inventory.server.model.User;
import com.inventory.server.serialization.converter.YamlMediaType;
import com.inventory.server.service.CategoryService;
import com.inventory.server.utils.CreateRecordUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/categories")
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Categories", description = "Endpoints for managing categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping(
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, YamlMediaType.APPLICATION_YAML})
    @Operation(
            summary = "Finds all categories",
            description = "Finds all categories divided by pages",
            tags = {"Categories"},
            responses = {
                    @ApiResponse(
                            description = "OK",
                            responseCode = "200",
                            content = @Content(schema =
                            @Schema(implementation =
                                    Page.class))
                    ),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content),
                    @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
                    @ApiResponse(description = "Bad request", responseCode = "400", content = @Content)
            }
    )
    public ResponseEntity<Page<CategoryListData>> listCategories(@PageableDefault(sort = {"categoryName"}) Pageable pagination) {
        return ResponseEntity.ok(categoryService.listAllCategories(pagination));
    }

    @PostMapping
    public ResponseEntity<Object> registerCategory(
            @RequestBody @Valid CreateCategoryData data,
            UriComponentsBuilder uriBuilder) throws CategoryAlreadyCreatedException {
        CreateRecordUtil record = categoryService.registerCategory(data, uriBuilder);

        return ResponseEntity.created(record.getUri()).body(record.getObject());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategoryById(@PathVariable Long id, Authentication authentication) {

        if (categoryService.existsByIdAndUserId(id, ((User) authentication.getPrincipal()).getId())) {
            categoryService.deleteCategoryById(id);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategoryById(@PathVariable Long id,
                                                @RequestBody @Valid CreateCategoryData data,
                                                Authentication authentication) throws CategoryAlreadyCreatedException {

        if (categoryService.existsByIdAndUserId(id, ((User) authentication.getPrincipal()).getId())) {
            CreateCategoryData category = categoryService.updateCategory(id, data);
            return ResponseEntity.ok(category);
        }

        return ResponseEntity.notFound().build();
    }
}