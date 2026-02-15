package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.service.AvatarService;

@RestController
@RequestMapping("/avatar/pagination")
@Tag(name = "Avatar Pagination Controller", description = "Пагинация для аватарок")
public class AvatarPaginationController {
    private final AvatarService avatarService;

    public AvatarPaginationController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @GetMapping
    @Operation(
            summary = "Получить аватарки с пагинацией",
            description = "Возвращает список аватарок с поддержкой пагинации"
    )
    @ApiResponse(responseCode = "200", description = "Аватарки успешно получены")
    public ResponseEntity<Page<Avatar>> getAllAvatars(
            @Parameter(description = "Номер страницы с 0") @RequestParam (defaultValue = "0") int page,
            @Parameter(description = "Количество элементов на странице") @RequestParam(defaultValue = "10") int size) {
        Page<Avatar> avatars = avatarService.getAllAvatars(page, size);
        return ResponseEntity.ok(avatars);
    }
}
