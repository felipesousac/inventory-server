package com.inventory.server.item;

import com.inventory.server.item.dto.CreateItemData;
import com.inventory.server.item.dto.ItemListData;
import com.inventory.server.item.dto.ItemUpdateData;
import com.inventory.server.serialization.converter.YamlMediaType;
import com.inventory.server.utils.CreateRecordUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/items")
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Items", description = "Endpoints for managing items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, YamlMediaType.APPLICATION_YAML})
    @Operation(
            summary = "Finds all items",
            description = "Finds all items divided by pages",
            tags = {"Items"},
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
    public ResponseEntity<Page<ItemListData>> getItems(@PageableDefault(sort = "itemName") Pageable pagination) {
        return ResponseEntity.ok(itemService.findAllItems(pagination));
    }

    @GetMapping(value = "/{id}/category", produces = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE, YamlMediaType.APPLICATION_YAML})
    @Operation(
            summary = "Find items filtered by category",
            description = "Finds all items filtered by category id",
            tags = {"Items"},
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
    public ResponseEntity<Page<ItemListData>> itemsByCategoryId(
            @PathVariable @Parameter(description = "The id of the category") Long id,
            @PageableDefault(sort = "itemName") Pageable pagination) {
        return ResponseEntity.ok(itemService.itemsByCategoryId(id, pagination));
    }

    @GetMapping(value = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, YamlMediaType.APPLICATION_YAML})
    @Operation(
            summary = "Finds item",
            description = "Finds item by id",
            tags = {"Items"},
            responses = {
                    @ApiResponse(
                            description = "OK",
                            responseCode = "200",
                            content = @Content(schema =
                                    @Schema(implementation = ItemListData.class)
                            )
                    ),
                    @ApiResponse(description = "Not found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
                    @ApiResponse(description = "Bad request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content)
            }
    )
    public ResponseEntity<?> detailItemById(
            @PathVariable @Parameter(description = "The id of the item to find") Long id) {
        return ResponseEntity.ok(itemService.detailItemById(id));
    }

    @PostMapping(
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, YamlMediaType.APPLICATION_YAML},
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, YamlMediaType.APPLICATION_YAML})
    @Operation(
            summary = "Creates item",
            description = "Creates an item by passing in a JSON, XML or YAML representation of the item",
            tags = {"Items"},
            responses = {
                    @ApiResponse(
                            description = "Created",
                            responseCode = "201",
                            content = @Content(schema = @Schema(implementation = ItemListData.class))
                    ),
                    @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
                    @ApiResponse(description = "Bad request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content)
            }
    )
    public ResponseEntity<Object> createItem(
            @RequestBody @Valid CreateItemData data,
            UriComponentsBuilder uriBuilder) {
        CreateRecordUtil record = itemService.createItem(data, uriBuilder);

        return ResponseEntity.created(record.uri()).body(record.object());
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Deletes item",
            description = "Deletes items by id",
            tags = {"Items"},
            responses = {
                    @ApiResponse(description = "No content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Not found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
                    @ApiResponse(description = "Bad request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content)
            }
    )
    public ResponseEntity<?> deleteItemById(
            @PathVariable @Parameter(description = "Id of item to delete") Long id) {
        itemService.deleteItemById(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, YamlMediaType.APPLICATION_YAML},
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, YamlMediaType.APPLICATION_YAML})
    @Operation(
            summary = "Updates item data",
            description = "Updates item data by passing in a JSON, XML or YAML representation of the item",
            tags = {"Items"},
            responses = {
                    @ApiResponse(
                            description = "OK",
                            responseCode = "200",
                            content = @Content(schema =
                            @Schema(implementation = ItemListData.class)
                            )
                    ),
                    @ApiResponse(description = "Not found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
                    @ApiResponse(description = "Bad request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content)
            }
    )
    public ResponseEntity<ItemListData> updateItemById(
            @RequestBody @Valid ItemUpdateData data,
            @PathVariable @Parameter(description = "Id of item that will be updated") Long id) {
        ItemListData item = itemService.updateItemById(data, id);

        return ResponseEntity.ok(item);
    }

    @PostMapping("/search")
    public ResponseEntity<?> findItemsByCriteria(@RequestBody Map<String, String> searchCriteria,
                                                 Pageable pagination) {

        return ResponseEntity.ok(itemService.findByCriteria(searchCriteria, pagination));
    }

    @PostMapping("/{itemId}/upload")
    @Operation(
            summary = "Adds image file in item",
            description = "Adds image in item by passing a file of user's file system",
            tags = {"Items"},
            responses = {
                    @ApiResponse(description = "No content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Not found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
                    @ApiResponse(description = "Bad request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content),
                    @ApiResponse(description = "Unsupported media type", responseCode = "415", content =
                    @Content(schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    public ResponseEntity<?> uploadImage(@PathVariable Long itemId,
                                         @RequestParam("image") MultipartFile img) throws IOException {
        itemService.uploadImage(itemId, img);

        return ResponseEntity.noContent().build();
    }

//    @PreAuthorize("hasAuthority('MANAGER')")
//    @GetMapping("/admin")
//    public String soAdmin() {
//        return ".";
//    }
}
