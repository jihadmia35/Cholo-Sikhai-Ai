package com.cholosikhai.cholosikhaiai.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
public class ChatService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("classpath:/prompts/lesson-system.st")
    Resource systemPrompt;

    @Value("classpath:/prompts/flutter.pdf")
    Resource flutterRoadMap;

    private final ChatClient chatClient;
    private VectorStore vectorStore;

    public ChatService(ChatClient chatClient, VectorStore vectorStore) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
    }

    public void saveInVDB(){

//        TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(flutterRoadMap);
//
//        TextSplitter textSplitter = new TokenTextSplitter();
//        List<Document> list = textSplitter.split(tikaDocumentReader.read());
//        vectorStore.accept(list);


//        List<String> list = List.of(
//
//        );
//        List<Document> documentList = list.stream().map(Document::new).toList();
//        vectorStore.accept(documentList);
    }



    public Flux<String> getResponse(String userQuery) {

//        saveInVDB();

        var qaAdvisor = QuestionAnswerAdvisor.builder(vectorStore)
                .searchRequest(SearchRequest.builder().similarityThreshold(0.1).topK(3).build())
                .build();

        var ragAdvisor = RetrievalAugmentationAdvisor
                .builder()
                .queryTransformers(
                        RewriteQueryTransformer.builder()
                                .chatClientBuilder(chatClient.mutate().clone())
                                .build(),
                        TranslationQueryTransformer.builder()
                                .chatClientBuilder(chatClient.mutate().clone())
                                .targetLanguage("Bangla")
                                .build()
                )
                .queryExpander(
                        MultiQueryExpander.builder()
                                .chatClientBuilder(chatClient.mutate().clone())
                                .numberOfQueries(2)
                                .build()
                )
                .documentRetriever(
                        VectorStoreDocumentRetriever.builder()
                                .similarityThreshold(0.1)
                                .topK(3)
                                .vectorStore(vectorStore)
                                .build()
                )
                .queryAugmenter(
                        ContextualQueryAugmenter.builder().build()
                )
                .build();

        return chatClient
                .prompt()
                .advisors(ragAdvisor)
                .user(userQuery)
                .system(s -> s.text(systemPrompt))
                .stream()
                .content();
    }
}
