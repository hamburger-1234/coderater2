package com.se.coderater.repository;

import com.se.coderater.entity.Code;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CodeRepository extends JpaRepository<Code, Long> {

    // 查询用户上传的代码列表
    List<Code> findByUploaderId(Long userId);

    // 新增方法：检查代码是否存在且属于特定用户
    boolean existsByIdAndUploaderId(Long id, Long uploaderId);
}