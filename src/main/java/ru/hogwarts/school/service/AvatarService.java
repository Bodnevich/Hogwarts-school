package ru.hogwarts.school.service;

import lombok.Value;
import org.hibernate.result.Output;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repositories.AvatarRepository;
import ru.hogwarts.school.repositories.StudentRepository;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Service
public class AvatarService {
    private final AvatarRepository avatarRepository;
    private final StudentRepository studentRepository;

    @Value("${avatars.dir.path}")
    private String avatarDir;

    public AvatarService(AvatarRepository avatarRepository, StudentRepository studentRepository) {
        this.avatarRepository = avatarRepository;
        this.studentRepository = studentRepository;
    }

    public Avatar uploadAvatar(Long studentId, MultipartFile file) throws IOException {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(()-> new RuntimeException("Студент не найден"));

        Path filePath = Path.of(avatarDir, studentId + "." + getExtension(file.getOriginalFilename()));
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);

        try (InputStream is = file.getInputStream();
             OutputStream os = Files.newOutputStream(filePath);
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             BufferedOutputStream bos = new BufferedOutputStream(os, 1024)) {
            bis.transferTo(bos);
        }

        byte[] preview = generateImagePreview(filePath);

        Avatar avatar = avatarRepository.findByStudentId(studentId).orElse(new Avatar());
        avatar.setStudent(student);
        avatar.setFilePath(filePath.toString());
        avatar.setFileSize(file.getSize());
        avatar.setMediaType(file.getContentType());
        avatar.setData(preview);

        Avatar savedAvatar = avatarRepository.save(avatar);
        student.setAvatar(savedAvatar);
        studentRepository.save(student);

        return savedAvatar;
    }

    private byte[] generateImagePreview(Path filePath) throws IOException {
        try (InputStream is = Files.newInputStream(filePath);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            BufferedImage originalImage = ImageIO.read(is);
            if (originalImage == null) {
                return Files.readAllBytes(filePath);
            }

            int previewWidth = 100;
            int previewHeight = (int) Math.round(originalImage.getHeight() *
                    ((double) previewWidth / originalImage.getWidth()));

            BufferedImage previewImage = new BufferedImage(previewWidth, previewHeight,
                    originalImage.getType());
            Graphics2D graphics = previewImage.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics.drawImage(originalImage, 0, 0, previewWidth, previewHeight, null);
            graphics.dispose();

            ImageIO.write(previewImage, "jpg", baos);
            return baos.toByteArray();
        }
    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    public Optional<Avatar> findAvatar(Long studentId) {
        return avatarRepository.findByStudentId(studentId);
    }

    public byte[] getAvatarFromDb(Long studentId) {
        return avatarRepository.findByStudentId(studentId)
                .map(Avatar::getData)
                .orElseThrow(() -> new RuntimeException("Аватар не найден"));
    }

    public byte[] getAvatarFromDisk(Long studentId) throws IOException {
        Avatar avatar = avatarRepository.findByStudentId(studentId)
                .orElseThrow(() -> new RuntimeException("Аватар не найден"));
        Path filePath = Path.of(avatar.getFilePath());
        return Files.readAllBytes(filePath);
    }
}

