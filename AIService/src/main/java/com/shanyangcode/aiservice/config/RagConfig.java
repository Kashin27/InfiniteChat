package com.shanyangcode.aiservice.config;


import com.shanyangcode.aiservice.rag.retriever.HybridContentRetriever;
import com.shanyangcode.aiservice.rag.retriever.PgBm25ContentRetriever;
import com.shanyangcode.aiservice.rag.retriever.RerankingContentRetriever;
import com.shanyangcode.aiservice.rag.splitter.MarkdownHeadingSplitter;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.scoring.ScoringModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@SuppressWarnings({"all"})
public class RagConfig {


    @Resource
    private EmbeddingModel embeddingModel;

    @Resource
    private EmbeddingStore<TextSegment> embeddingStore;



    @Value("${rag.docs-path}")
    private String docsPath;

    @Value("${pgvector.table}")
    private String table;

    // ============ 两阶段检索的关键参数 ============

    // 粗召回条数（Top-N）：向量/BM25 各自召回、以及 RRF 融合后保留的候选数。
    // 给得比最终 Top-K 大不少，好让精排有足够的候选空间。
    @Value("${rag.recall-top-n:20}")
    private int recallTopN;

    // 精排后最终保留条数（Top-K）：真正喂给大模型的资料条数。
    @Value("${rag.rerank-top-k:5}")
    private int rerankTopK;

    // 精排相关性阈值：qwen3-rerank 分数 0~1，低于它的候选直接丢弃。
    @Value("${rag.rerank-min-score:0.2}")
    private double rerankMinScore;

    @Bean
    public EmbeddingStoreIngestor embeddingStoreIngestor() {


        DocumentSplitter subSplitter = DocumentSplitters.recursive(1000, 100);


        DocumentSplitter splitter = new MarkdownHeadingSplitter(3, 1500, subSplitter);
        // DocumentByParagraphSplitter paragraphSplitter = new DocumentByParagraphSplitter(300, 100);

        return EmbeddingStoreIngestor.builder()
                .documentSplitter(splitter)
                .textSegmentTransformer(textSegment -> TextSegment.from(
                        textSegment.metadata().getString("file_name") + "\n" + textSegment.text(),
                        textSegment.metadata()
                ))

                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();
    }



    /** 稠密检索：向量语义检索(参数与基线一致,方便 A/B 对齐) */
    @Bean
    public ContentRetriever vectorContentRetriever() {
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(recallTopN)
                .minScore(0.75)
                .build();
    }
    /** 稀疏检索：基于 ParadeDB 的 BM25 关键词检索 */
    @Bean
    public PgBm25ContentRetriever bm25ContentRetriever(JdbcTemplate jdbcTemplate) {
        return new PgBm25ContentRetriever(jdbcTemplate, table, recallTopN);
    }

    /** 混合检索：向量 + BM25,RRF 融合后取 top-5。对外暴露给 AiService 和评测接口 */
    @Bean
    public ContentRetriever hybridContentRetriever(
            @Qualifier("vectorContentRetriever") ContentRetriever vectorContentRetriever,
            PgBm25ContentRetriever bm25ContentRetriever) {
        return new HybridContentRetriever(vectorContentRetriever, bm25ContentRetriever, rerankTopK, recallTopN);
    }
    /**
     * 重排检索（精排，两阶段检索的最终出口）：
     * 把混合检索的 recallTopN 条候选交给 qwen3-rerank 精排，过阈值后取 rerankTopK 条。
     * AiChatService 和 EvalController 都注入这个 Bean。
     */
    @Bean
    public ContentRetriever rerankingContentRetriever(
            @Qualifier("hybridContentRetriever") ContentRetriever hybridContentRetriever,
            ScoringModel scoringModel) {
        return new RerankingContentRetriever(hybridContentRetriever, scoringModel, rerankTopK, rerankMinScore);
    }
}