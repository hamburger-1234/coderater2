package com.se.coderater.service;

import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.se.coderater.entity.Code;
import com.se.coderater.entity.User;
import com.se.coderater.repository.CodeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class CodeService {

    private static final Logger logger = LoggerFactory.getLogger(CodeService.class);
    private final CodeRepository codeRepository;

    @Autowired
    public CodeService(CodeRepository codeRepository) {
        this.codeRepository = codeRepository;
    }

    public Code storeFileAndParse(MultipartFile file) throws IOException, IllegalArgumentException {
        // 1. 校验文件名和文件类型
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file must have a name.");
        }
        String extension = StringUtils.getFilenameExtension(originalFileName);
        if (!"java".equalsIgnoreCase(extension)) {
            throw new IllegalArgumentException("Only .java files are allowed.");
        }
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot upload an empty file.");
        }

        // 2. 读取文件内容
        String content = new String(file.getBytes(), StandardCharsets.UTF_8);

        // 3. 获取当前登录用户
        User uploader = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 4. 创建 Code 实体并关联用户
        Code newCode = new Code();
        newCode.setFileName(originalFileName);
        newCode.setContent(content);
        newCode.setUploader(uploader);

        // 5. 使用 JavaParser 解析代码
        try {
            CompilationUnit cu = StaticJavaParser.parse(content);
            int classCount = cu.findAll(ClassOrInterfaceDeclaration.class).size();
            newCode.setClassCount(classCount);
            int methodCount = cu.findAll(MethodDeclaration.class).size();
            newCode.setMethodCount(methodCount);
            long nonEmptyLines = content.lines().filter(line -> !line.trim().isEmpty()).count();
            newCode.setLineCount((int) nonEmptyLines);

            logger.info("Parsed {}: Classes={}, Methods={}, Lines={}, Uploader={}",
                    originalFileName, classCount, methodCount, nonEmptyLines, uploader.getUsername());
        } catch (ParseProblemException e) {
            logger.error("Failed to parse Java file: {}. Reason: {}", originalFileName, e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error during parsing file: {}. Reason: {}", originalFileName, e.getMessage());
        }

        // 6. 保存到数据库
        return codeRepository.save(newCode);
    }

    public List<Code> getCodesByUserId(Long userId) {
        return codeRepository.findByUploaderId(userId);
    }

    // 新增方法：删除用户上传的代码
    public void deleteCode(Long codeId, Long userId) {
        // 验证代码是否存在且属于当前用户
        if (!codeRepository.existsByIdAndUploaderId(codeId, userId)) {
            throw new IllegalStateException("Code not found or you do not have permission to delete it.");
        }
        // 删除代码
        codeRepository.deleteById(codeId);
        logger.info("Deleted code with ID {} by user ID {}", codeId, userId);
    }
}