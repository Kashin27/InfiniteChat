package com.shanyangcode.aiservice.config;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class RagDataLoader implements CommandLineRunner {

    @Resource
    private EmbeddingStoreIngestor embeddingStoreIngestor;

    @Value("${rag.docs-path}")
    private String docsPath;

    @Override
    public void run(String... args) {
        log.info("开始加载 RAG 知识库文档，路径: {}", docsPath);
        List<Document> documents = ClassPathDocumentLoader.loadDocuments(docsPath);
        log.info("成功加载 {} 个文档，开始向量化入库...", documents.size());
        embeddingStoreIngestor.ingest(documents);
        log.info("RAG 知识库文档入库完成");
    }
}