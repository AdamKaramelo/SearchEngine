package suchmaschine;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Class which offers synchronized methods for {@link LinkedDocumentCollection}.
 * @author adamk
 *
 */

public class SynchronizedLdcWrapper {

	private LinkedDocumentCollection ldc;
	private final double pageRankDampingFactor = 0.85;
	private final double weightingFactor = 0.6;

	public SynchronizedLdcWrapper() {
		this.ldc = new LinkedDocumentCollection();
	}

	public synchronized void appendDocument(LinkedDocument doc) {
		ldc.appendDocument(doc);
	}

	public synchronized void forEach(Consumer<Document> callback) {
		for (int i = 0; i < ldc.numDocuments(); i++) {
			callback.accept(ldc.get(i));
		}
	}

	public void query(String query, BiConsumer<Document, Double> callback) {

		double[] relevance;
		synchronized (ldc) {
			relevance = ldc.match(query, pageRankDampingFactor, weightingFactor);
		}
		for (int i = 0; i < ldc.numDocuments(); i++) {
			Document doc = ldc.get(i);
			synchronized (doc) {
				callback.accept(ldc.get(i), relevance[i]);
			}
		}

	}

	public void pageRank(BiConsumer<Document, Double> callback) {
		
		double[] relevance;
		synchronized (ldc) {
			relevance = ldc.pageRank(pageRankDampingFactor);
		}
		for (int i = 0; i < ldc.numDocuments(); i++) {
			Document doc = ldc.get(i);
			synchronized (doc) {
				callback.accept(ldc.get(i), relevance[i]);
			}
		}
	}
	
	public synchronized void crawl() {
		this.ldc = this.ldc.crawl();
	}
	
	
}
