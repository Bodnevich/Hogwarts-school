package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.service.AvatarService;

import java.io.IOException;

@RestController
@RequestMapping("/avatar")
@Tag(name = "Avatar Controller", description = "Управление аватарами студентов")
public class AvatarController {
    private final AvatarService avatarService;

    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @PostMapping(value = "/{studentId}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Загрузить аватар студента",
            description = "Загружает аватар для указанного студента. Сохраняет файл на диск и превью в БД."
    )
    @ApiResponse(responseCode = "200", description = "Аватар успешно загружен")
    @ApiResponse(responseCode = "400", description = "Ошибка при загрузке файла")
    @ApiResponse(responseCode = "404", description = "Студент не найден")
    public ResponseEntity<Avatar> uploadAvatar(
            @Parameter(description = "ID студента") @PathVariable Long studentId,
            @Parameter(description = "Файл изображения") @RequestParam MultipartFile file) {
        try {
            Avatar avatar = avatarService.uploadAvatar(studentId, file);
            return ResponseEntity.ok(avatar);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping(value = "/{studentId}/preview")
    @Operation(
            summary = "Получить превью аватара из БД",
            description = "Возвращает превью аватара студента из базы данных"
    )
    @ApiResponse(responseCode = "200", description = "Превью успешно получено")
    @ApiResponse(responseCode = "404", description = "Аватар не найден")
    public ResponseEntity<byte[]> getAvatarPreviewFromDb(
            @Parameter(description = "ID студента") @PathVariable Long studentId) {
        byte[] avatarData = avatarService.getAvatarFromDb(studentId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentLength(avatarData.length);

        return new ResponseEntity<>(avatarData, headers, HttpStatus.OK);
    }

    @GetMapping(value = "/{studentId}/full")
    @Operation(
            summary = "Получить полный аватар с диска",
            description = "Возвращает полное изображение аватара студента с локального диска"
    )
    @ApiResponse(responseCode = "200", description = "Аватар успешно получен")
    @ApiResponse(responseCode = "404", description = "Аватар не найден")
    public ResponseEntity<byte[]> getAvatarFromDisk(
            @Parameter(description = "ID студента") @PathVariable Long studentId) {
        try {
            byte[] avatarData = avatarService.getAvatarFromDisk(studentId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            headers.setContentLength(avatarData.length);

            return new ResponseEntity<>(avatarData, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}