package com.se.coderater.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "codes") // 表名 codes
@Data // Lombok: 自动生成 getter, setter, toString, equals, hashCode
@NoArgsConstructor // Lombok: 无参构造函数
@AllArgsConstructor // Lombok: 全参构造函数
public class Code {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主键自增
    private Long id;

    // 添加用户关联：代码与上传用户是多对一关系
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User uploader;

    @Column(nullable = false)
    private String fileName; // 文件名

    @Lob // 大对象，适合存储较长文本
    @Column(nullable = false, columnDefinition = "TEXT") // 数据库类型为 TEXT
    private String content; // 代码内容

    private LocalDateTime uploadedAt; // 上传时间

    // 解析结果字段
    private Integer classCount;    // 类数量
    private Integer methodCount;   // 方法数量
    private Integer lineCount;     // 代码行数（不含空行和注释）

    // 关系映射: 一个代码对应一个分析结果（未启用，保留注释）
    // @OneToOne(mappedBy = "code", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // private Analysis analysis;

    @PrePersist // 在实体持久化前设置上传时间
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
    }

    // 构造函数，方便测试
    public Code(String fileName, String content, User uploader) {
        this.fileName = fileName;
        this.content = content;
        this.uploader = uploader;
    }
}