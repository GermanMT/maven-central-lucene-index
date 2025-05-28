import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

public class MavenArtifactExtractor {
    private static final String INDEX_PATH = "central-lucene-index";
    private static final String OUTPUT_FILE = "maven_artifacts.txt";
    private static final int BATCH_SIZE = 10_000;

    public static void main(String[] args) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_FILE, true))) {
            FSDirectory directory = FSDirectory.open(Paths.get(INDEX_PATH));
            DirectoryReader reader = DirectoryReader.open(directory);
            IndexSearcher searcher = new IndexSearcher(reader);
            StandardAnalyzer analyzer = new StandardAnalyzer();
            Query query = new QueryParser("u", analyzer).parse("*:*");
            Set<String> seenArtifacts = new HashSet<>();
            ScoreDoc lastDoc = null;
            int totalDocs = reader.maxDoc();
            System.out.println("🔍 Documents in the index: " + totalDocs);
            int batchCount = 0;
            while (true) {
                TopDocs topDocs = searcher.searchAfter(lastDoc, query, BATCH_SIZE);
                if (topDocs.scoreDocs.length == 0)
                    break;
                System.out.println("📦 Batch processing " + (++batchCount) + "...");
                for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                    Document doc = searcher.storedFields().document(scoreDoc.doc);
                    String uField = doc.get("u");
                    if (uField != null) {
                        String[] parts = uField.split("\\|");
                        if (parts.length >= 3) {
                            String group = parts[0];
                            String artifact = parts[1];
                            String version = parts[2];
                            if (!group.isEmpty() && !artifact.isEmpty() && !version.isEmpty()
                                    && !group.equals("NA") && !artifact.equals("NA")
                                    && !version.equals("NA")) {
                                String artifactEntry = group + ":" + artifact + ":" + version;
                                artifactEntry = fixArtifactLine(artifactEntry);
                                if (artifactEntry != null && !seenArtifacts.contains(artifactEntry)) {
                                    seenArtifacts.add(artifactEntry);
                                    writer.write(artifactEntry);
                                    writer.newLine();
                                }
                            }
                        }
                    }
                }
                writer.flush();
                lastDoc = topDocs.scoreDocs[topDocs.scoreDocs.length - 1];
            }
            System.out.println("✅ File generated without duplicates: " + OUTPUT_FILE);
            reader.close();
            directory.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String fixArtifactLine(String line) {
        String[] parts = line.strip().split(":");
        if (parts.length == 3) {
            return line.strip();
        } else if (parts.length == 4) {
            String group_id = parts[0];
            String part1 = parts[1];
            String part2 = parts[2];
            String version = parts[3];
            String artifact_id = part1 + "__" + part2;
            return group_id + ":" + artifact_id + ":" + version;
        } else {
            return null;
        }
    }
}
