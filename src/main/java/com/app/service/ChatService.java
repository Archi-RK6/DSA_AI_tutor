package com.app.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.template.NoOpTemplateRenderer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    private static final int MAX_DOC_CHARS = 400_000;
    private static final int MAX_EXCERPT_CHARS = 2200;
    private static final int MAX_PDF_PAGES = 60;
    private static final int MAX_PDF_BYTES = 35 * 1024 * 1024;

    private static final int MAX_CONTEXT_CHARS = 11_000;
    private static final int MAX_CHAT_HISTORY_CHARS = 2500;

    private final ChatClient chatClient;
    private final ResourcePatternResolver resourcePatternResolver;
    private final boolean loadPdfs;
    private final List<RagDocument> documents = new CopyOnWriteArrayList<>();

    public ChatService(ChatClient.Builder chatClientBuilder,
                       ResourcePatternResolver resourcePatternResolver,
                       @Value("${RAG_LOAD_PDFS:false}") boolean loadPdfs) {

        this.chatClient = chatClientBuilder
                .defaultTemplateRenderer(new NoOpTemplateRenderer())
                .build();

        this.resourcePatternResolver = resourcePatternResolver;
        this.loadPdfs = loadPdfs;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        Thread t = new Thread(this::loadDocumentsSafely, "rag-loader");
        t.setDaemon(true);
        t.setPriority(Thread.MIN_PRIORITY);
        t.start();
    }

    private void loadDocumentsSafely() {
        try {
            loadDocuments();
        } catch (Exception e) {
            log.error("RAG load failed", e);
        }
    }

    private void loadDocuments() throws Exception {
        documents.clear();
        Resource[] resources = resourcePatternResolver.getResources("classpath:/rag/*");
        for (Resource resource : resources) {
            if (resource == null || !resource.isReadable()) continue;

            String filename = resource.getFilename();
            if (filename == null) continue;

            String lower = filename.toLowerCase(Locale.ROOT);

            if (lower.endsWith(".txt") || lower.endsWith(".md")) {
                String text = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                addDoc(filename, text);
                continue;
            }

            if (lower.endsWith(".pdf") && loadPdfs) {
                byte[] data = resource.getInputStream().readAllBytes();
                if (data.length > MAX_PDF_BYTES) {
                    log.warn("Skipping PDF (too large): {} size={}", filename, data.length);
                    continue;
                }

                try (PDDocument pdfDoc = Loader.loadPDF(data)) {
                    PDFTextStripper stripper = new PDFTextStripper();
                    stripper.setStartPage(1);
                    stripper.setEndPage(Math.min(MAX_PDF_PAGES, pdfDoc.getNumberOfPages()));
                    String text = stripper.getText(pdfDoc);
                    addDoc(filename, text);
                } catch (Exception e) {
                    log.warn("Failed to load PDF for RAG: {}", filename, e);
                }
            }
        }
    }

    private void addDoc(String name, String text) {
        if (text == null) text = "";
        if (text.length() > MAX_DOC_CHARS) text = text.substring(0, MAX_DOC_CHARS);
        documents.add(new RagDocument(name, text, text.toLowerCase(Locale.ROOT)));
    }

    public String chatWithContext(String message, Map<String, Object> uiContext) {
        return chatWithContext(message, uiContext, List.of());
    }

    public String chatWithContext(String message, Map<String, Object> uiContext, List<String> chatHistory) {
        String userMsg = message == null ? "" : message.trim();
        if (userMsg.isBlank()) return "Please type a message.";

        String ragContext = buildRagContext(userMsg);
        String contextStr = formatUiContext(uiContext);
        String historyStr = formatChatHistory(chatHistory);

        String systemPrompt = """
            You are an AI tutor for data structures and algorithms.

            You have access to course notes and textbook excerpts in the block called
            "RAG DOCUMENTATION". Use that documentation as your primary source of truth.

            IMPORTANT:
            - UI CONTEXT contains: selectedType, currentSnapshot, lastOperation, lastSteps,
              and operationHistory (the list of ALL operations the user performed on the CURRENT structure).
            - If the user asks: "List all operations I have done", you MUST use operationHistory.
            - If operationHistory is empty/missing, say that there is no recorded history for the current structure.

            Style:
            - Be clear and beginner-friendly.
            - Use the user's concrete values when present in context.
            - Do NOT mention internal file names/page numbers unless asked.

            === RECENT CHAT HISTORY ===
            %s
            === END CHAT HISTORY ===

            === UI CONTEXT ===
            %s
            === END UI CONTEXT ===

            === RAG DOCUMENTATION START ===
            %s
            === RAG DOCUMENTATION END ===
            """.formatted(historyStr, contextStr, ragContext);

        try {
            return chatClient
                    .prompt()
                    .system(systemPrompt)
                    .user(userMsg)
                    .call()
                    .content();
        } catch (Exception e) {
            log.error("AI chat failed. userMsgLen={}, uiCtxLen={}, ragDocs={}",
                    userMsg.length(),
                    contextStr.length(),
                    documents.size(),
                    e
            );
            String msg = e.getMessage();
            if (msg == null || msg.isBlank()) msg = e.getClass().getSimpleName();
            return "AI temporarily unavailable: " + msg;
        }
    }

    private String formatChatHistory(List<String> history) {
        if (history == null || history.isEmpty()) return "(empty)";
        String joined = String.join("\n", history);
        return truncate(joined, MAX_CHAT_HISTORY_CHARS);
    }

    private String formatUiContext(Map<String, Object> uiContext) {
        if (uiContext == null || uiContext.isEmpty()) return "(empty)";

        StringBuilder sb = new StringBuilder();

        Object type = uiContext.get("selectedType");
        if (type != null) sb.append("selectedType: ").append(type).append("\n");

        Object opHist = uiContext.get("operationHistory");
        if (opHist instanceof List<?> list) {
            sb.append("\noperationHistory:\n");
            int i = 1;
            for (Object it : list) {
                if (it == null) continue;
                sb.append("  ").append(i++).append(") ").append(it).append("\n");
                if (i > 60) { sb.append("  ... (trimmed)\n"); break; }
            }
        }

        Object lastOp = uiContext.get("lastOperation");
        if (lastOp != null) sb.append("\nlastOperation: ").append(lastOp).append("\n");

        Object lastSteps = uiContext.get("lastSteps");
        if (lastSteps != null) sb.append("\nlastSteps: ").append(lastSteps).append("\n");
        
        Set<String> used = new HashSet<>(Arrays.asList(
                "selectedType", "operationHistory", "lastOperation", "lastSteps"
        ));

        for (var e : uiContext.entrySet()) {
            if (used.contains(e.getKey())) continue;
            sb.append(e.getKey()).append(": ").append(Objects.toString(e.getValue())).append("\n");
        }

        return truncate(sb.toString().trim(), MAX_CONTEXT_CHARS);
    }

    private String buildRagContext(String question) {
        if (documents.isEmpty()) {
            return "(no RAG documents found - add .txt/.md into src/main/resources/rag; PDFs are ignored unless RAG_LOAD_PDFS=true)";
        }

        String normalized = question.toLowerCase(Locale.ROOT);
        Set<String> tokens = Arrays.stream(normalized.split("\\W+"))
                .filter(t -> t.length() > 2)
                .limit(30)
                .collect(Collectors.toSet());

        if (tokens.isEmpty()) {
            return documents.stream()
                    .limit(2)
                    .map(doc -> "### " + doc.name + "\n" + truncate(doc.text, MAX_EXCERPT_CHARS))
                    .collect(Collectors.joining("\n\n"));
        }

        List<ScoredDoc> scored = new ArrayList<>();
        for (RagDocument doc : documents) {
            int score = 0;
            for (String token : tokens) {
                if (token.length() < 3) continue;
                if (doc.lowerText.contains(token)) score++;
            }
            if (score > 0) scored.add(new ScoredDoc(doc, score));
        }

        if (scored.isEmpty()) {
            RagDocument doc = documents.get(0);
            return "### " + doc.name + "\n" + truncate(doc.text, MAX_EXCERPT_CHARS);
        }

        scored.sort(Comparator.comparingInt(sd -> sd.score));
        Collections.reverse(scored);

        return scored.stream()
                .limit(3)
                .map(sd -> "### " + sd.doc.name + "\n" + extractSnippet(sd.doc.text, tokens, MAX_EXCERPT_CHARS))
                .collect(Collectors.joining("\n\n"));
    }

    private String extractSnippet(String text, Set<String> tokens, int windowSize) {
        if (text == null || text.isEmpty()) return "";
        if (tokens == null || tokens.isEmpty()) return truncate(text, windowSize);

        String lower = text.toLowerCase(Locale.ROOT);
        int bestScore = 0;
        int bestStart = 0;
        int radius = windowSize / 2;

        for (String token : tokens) {
            if (token.length() < 3) continue;
            int idx = lower.indexOf(token);
            while (idx != -1) {
                int start = Math.max(0, idx - radius);
                int end = Math.min(lower.length(), start + windowSize);
                String window = lower.substring(start, end);

                int score = 0;
                for (String tt : tokens) {
                    if (tt.length() < 3) continue;
                    if (window.contains(tt)) score++;
                }

                if (score > bestScore) {
                    bestScore = score;
                    bestStart = start;
                }

                idx = lower.indexOf(token, idx + token.length());
            }
        }

        if (bestScore == 0) return truncate(text, windowSize);

        int finalEnd = Math.min(text.length(), bestStart + windowSize);
        return text.substring(bestStart, finalEnd);
    }

    private String truncate(String text, int maxLen) {
        if (text == null || text.length() <= maxLen) return text;
        return text.substring(0, maxLen) + " ...";
    }

    private static final class RagDocument {
        private final String name;
        private final String text;
        private final String lowerText;

        private RagDocument(String name, String text, String lowerText) {
            this.name = name;
            this.text = text;
            this.lowerText = lowerText;
        }
    }

    private static final class ScoredDoc {
        private final RagDocument doc;
        private final int score;

        private ScoredDoc(RagDocument doc, int score) {
            this.doc = doc;
            this.score = score;
        }
    }
}
