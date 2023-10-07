package suchmaschine;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * A Collection of {@link LinkedDocument}s.
 * 
 * This class ensures, that only {@link LinkedDocument}s are added to this
 * collection.
 * 
 * @author
 *
 */
public class LinkedDocumentCollection extends DocumentCollection {

  /**
   * The epsilon for the PageRank algorithm
   */
  public static final double PAGERANK_EPS = 0.000000001;

  /**
   * Empty constructor that just utilizes constructor of super class.
   */
  public LinkedDocumentCollection() {
    super();
  }

  /**
   * The specified {@link Document} is added to this collection only if it is of
   * type {@link LinkedDocument} and if it is not already contained.
   * 
   * @param doc the document to add
   */
  public void prependDocument(LinkedDocument doc) {
    if (!(this.contains(doc))) {
      super.prependDocument(doc);
    }
  }

  /**
   * The specified {@link Document} is added to this collection only if it is of
   * type {@link LinkedDocument} and if it is not already contained.
   * 
   * @param doc the document to add
   */
  @Override
  public void appendDocument(Document doc) {
    if ((doc instanceof LinkedDocument) && !(this.contains(doc))) {
      super.appendDocument(doc);
    }
  }

  /**
   * Private helper method that crawls this collection.
   * 
   * This method adds all LinkedDocuments of this collection to the specified
   * {@link LinkedDocumentCollection}, if they are not already contained.
   * Additionally, this method recursively crawls all outgoing links of all
   * {@link LinkedDocument}s of this collection.
   * 
   * @param resultCollection in and out parameter. All LinkedDocuments are
   *                         (recursively) added to this
   *                         {@link LinkedDocumentCollection}.
   */
  private void crawl(LinkedDocumentCollection resultCollection) {
    if (this.numDocuments() == 0) {
      return;
    }

    /*
     * loop over all documents of this collection and add them to the in/out
     * parameter, if not already contained.
     */
    for (int i = 0; i < this.numDocuments(); i++) {
      LinkedDocument doc = (LinkedDocument) this.get(i);

      if (!resultCollection.contains(doc)) {
        resultCollection.appendDocument(doc);

        /* do the same recursively */
        doc.getOutgoingLinks(resultCollection).crawl(resultCollection);
      }
    }
  }

  /**
   * This method crawls this {@link LinkedDocumentCollection} and returns a new
   * {@link LinkedDocumentCollection}.
   * 
   * The returned LinkedDocumentCollection contains all LinkedDocuments of this
   * LinkedDocumentCollection plus any LinkedDocuments that the LinkedDocuments of
   * this collection link to. If these additional LinkedDocuments again link to
   * other LinkedDocuments they will be included as well, and so on.
   * 
   * @return a {@link LinkedDocumentCollection} that contains all
   *         {@link LinkedDocument}s of this collection plus any
   *         {@link LinkedDocument}s that are linked either directly or
   *         indirectly.
   */
  public LinkedDocumentCollection crawl() {
    /* prepare the resulting collection and begin crawling ... */
    LinkedDocumentCollection resultCollection = new LinkedDocumentCollection();
    this.crawl(resultCollection);
    return resultCollection;
  }

  /**
   * This private helper method multiplies the specified matrix with the specified
   * vector.
   * 
   * The resulting vector of the multiplication is returned. If one of the
   * parameters is <code>null</code>, <code>null</code> is returned.
   * 
   * @param matrix the matrix
   * @param vector the vector
   * @return the resulting of the multiplication of matrix with vector or
   *         <code>null</code>, if one of the arguments is <code>null</code>
   */
  private static double[] multiply(double[][] matrix, double[] vector) {
    if (matrix == null || vector == null) {
      return null;
    }

    double[] newVector = new double[vector.length];

    for (int i = 0; i < matrix.length; i++) {
      newVector[i] = scalarProduct(matrix[i], vector);
    }

    return newVector;
  }

  /**
   * This private helper method calculates the scalar product of the two specified
   * vectors.
   * 
   * If one of the vectors is <code>null</code> or if they differ in their length,
   * <code>0</code> is returned.
   * 
   * @param vectorA the 1st vector
   * @param vectorB the 2nd vector
   * @return the scalar product of the two vectors, or <code>0</code>, if one of
   *         the vectors is <code>null</code> or their length differ.
   */
  private static double scalarProduct(double[] vectorA, double[] vectorB) {
    if (vectorA == null || vectorB == null) {
      return 0;
    }

    if (vectorA.length != vectorB.length) {
      return 0;
    }

    /* calculate the scalar product */

    double result = 0;

    for (int i = 0; i < vectorA.length; i++) {
      result += vectorA[i] * vectorB[i];
    }

    return result;
  }

  /**
   * This private helper method calculates the matrix A, which we will need to
   * calculate the matrix M.
   * 
   * @return the matrix A
   */
  private double[][] calculateMatrixA() {
    /*
     * calculate all incoming links first, since we will rely on them in this method
     */
    this.calculateIncomingLinks();

    double[][] matrixA = new double[this.numDocuments()][this.numDocuments()];

    /* loop over all documents of this collection */
    for (int i = 0; i < this.numDocuments(); i++) {

      /* this LinkedDocument has no outgoing links */
      if (((LinkedDocument) this.get(i)).getOutgoingLinks().numDocuments() == 0) {
        // set value to every other LinkedDocument to 1/size()
        for (int j = 0; j < this.numDocuments(); j++) {
          if (i != j) {
            matrixA[j][i] = 1.0d / (this.numDocuments() - 1);
          }
        }
      }

      /*
       * loop over all documents linking to document at index i and set the
       * corresponding value in matrixA
       */
      LinkedDocumentCollection incomingLinks = ((LinkedDocument) this.get(i)).getIncomingLinks();
      for (int j = 0; j < incomingLinks.numDocuments(); j++) {
        /* inLink is a document that links to the document at index i */
        LinkedDocument inLink = (LinkedDocument) incomingLinks.get(j);

        int index = this.indexOf(inLink);
        if (index >= 0) {
          matrixA[i][index] = 1.0d / inLink.getOutgoingLinks().numDocuments();
        }
      }
    }

    return matrixA;
  }

  /**
   * This private helper methods calculates the matrix M, that will be the basis
   * for the calculation of the Page Rank values.
   * 
   * @return the matrix M
   */
  private double[][] calculateMatrixM(double dampingFactor) {
    double[][] matrixM = new double[this.numDocuments()][this.numDocuments()];

    /* calculate matrixA first, since we will rely on it in this method */
    double[][] matrixA = calculateMatrixA();

    /*
     * loop over all documents and calculate the values of matrixM according to the
     * algorithm based on the values of matrixA
     */
    for (int i = 0; i < this.numDocuments(); i++) {
      for (int j = 0; j < this.numDocuments(); j++) {
        matrixM[i][j] = dampingFactor * matrixA[i][j] + (1 - dampingFactor) / (double) this.numDocuments();
      }
    }

    return matrixM;
  }

  /**
   * Calculates PageRank recursively. This is the entry point for the
   * actually recursive pageRank() method
   *
   * @param dampingFactor The damping factor, a value between 0 and 1
   * @return The PageRank values of the documents in this collection
   */
  public double[] pageRankRec(double dampingFactor) {
    int[][] C = calculateMatrixC();
    int n = C.length;

    double[] PR = new double[n];
    for (int i = 0; i < n; i++)
      PR[i] = pageRankRec(C, i, dampingFactor, 0);
    return PR;
  }

  /**
   * The positions within this collection of the documents
   * pointing to the document at the given index position
   *
   * @param C The link matrix C
   * @param index The given index position
   * @return
   */
  private int[] getIncomingDocIndices(int[][] C, int index) {
    int n = C.length, j = 0;
    int[] indices = new int[n];
    for (int i = 0; i < n; i++)
      if (C[index][i] == 1)
        indices[j++] = i;
    if (j == 0)
      for (int i = 0; i < n; i++)
        if (i != index)
          indices[j] = j++;
    return Arrays.copyOf(indices, j);
  }

  /**
   * Sums up the number of outgoing links per document
   * based on the given link matrix C
   *
   * @param C The link matrix C
   * @return The number of outgoing links per document
   */
  private int[] getNumOutgoingLinks(int[][] C) {
    int n = C.length;
    int[] c = new int[n];
    for (int i = 0; i < n; i++)
      for (int j = 0; j < n; j++)
        c[i] += C[j][i];
    return c;
  }

  /**
   * Calculates PageRank recursively.
   *
   * @param C The link matrix C
   * @param i The document index for which the PageRank
   *          shall be calculated
   * @param d The damping factor
   * @param recDepth How deep this recursion call actually is.
   *                 Starts with 0.
   * @return
   */
  public double pageRankRec(int[][] C, int i, double d, int recDepth) {
    int n = C.length;
    int[] js = getIncomingDocIndices(C, i);
    int[] c = getNumOutgoingLinks(C);

    double[] PR = Arrays.copyOf(initPageRanks(), js.length);
    if (recDepth < 90)
      for (int j = 0; j < js.length; j++)
        PR[j] = pageRankRec(C, js[j], d, recDepth + 1);

    double sum = (1 - d) / n;
    for (int j = 0; j < js.length; j++)
      sum += d * PR[j] / c[js[j]]; // Page Rank Formula
    return sum;
  }

  /**
   * Calculates link matrix C.
   *
   * @return link matrix C
   */
  private int[][] calculateMatrixC() {
    double[][] A = calculateMatrixA();
    int n = A.length;
    int[][] C = new int[n][n];

    for (int i = 0; i < n; i++)
      for (int j = 0; j < n; j++)
        if (A[i][j] != 0)
          C[i][j] = 1;

    return C;
  }

  /**
   * Generates the initial PageRank values, i.e.,
   * an uniform distribution over all documents of this
   * collection.
   *
   * @return The initialized PageRank values
   */
  private double[] initPageRanks() {
    double[] pageRanks = new double[this.numDocuments()];
    for (int i = 0; i < this.numDocuments(); i++)
      pageRanks[i] = 1.0d / this.numDocuments();
    return pageRanks;
  }

  /**
   * This method calculates iteratively the Page Rank values for all documents in this
   * collection. It is in general, for bigger Document Numbers more efficient than 
   * the recursive version.
   * 
   * Source Page Rank Algorithm <a href= "http://pi.math.cornell.edu/~mec/Winter2009/RalucaRemus/Lecture3/lecture3.html">
   * http://pi.math.cornell.edu/~mec/Winter2009/RalucaRemus/Lecture3/lecture3.html</a>
   * 
   * The returned array contains the Page Rank value of every document in this
   * collection, such that for a {@link LinkedDocument} <code>linkedDoc</code>:
   * <code>PageRank(linkedDoc) = pageRank()[indexOf(linkedDoc)]</code>
   * 
   * @return the Page Rank values of the documents in this collection
   */
  public double[] pageRank(double dampingFactor) {
    /* calculate the matrixM and the initial Page Rank values */
    double[][] matrixM = calculateMatrixM(dampingFactor);
    double[] pageRanks = initPageRanks();

    /*
     * now, we will go for the approximation of the Page Rank values by multiplying
     * the matrixM with the Page Rank values until the approximation is well enough
     */

    /*
     * boolean that determines if the approximation is still bad; if so, we will do
     * another loop
     */
    boolean approximationIsBad;
    do {
      approximationIsBad = false;

      /*
       * calculate the new Page Rank values by multiplying the matrixM with the actual
       * Page Rank values
       */
      double[] newPageRanks = multiply(matrixM, pageRanks);

      /*
       * loop over the new and old Page Rank values and determine if the new
       * approximation is well enough
       */
      int i = 0;
      while (i < this.numDocuments() && !approximationIsBad) {
        /*
         * approximation is too bad, if one of the elements' change is greater than
         * epsilon
         */
        if (Math.abs(newPageRanks[i] - pageRanks[i]) > PAGERANK_EPS) {
          approximationIsBad = true;
        }
        i++;
      }

      /* assign new Page Rank values for next loop or return */
      pageRanks = newPageRanks;

    } while (approximationIsBad);

    return pageRanks;
  }


  /**
   * This method calculates all incoming links for every {@link LinkedDocument} in
   * this collection. The incoming links are assigned to every LinkedDocument via
   * the {@link LinkedDocument#addIncomingLink(LinkedDocument)} method.
   */
  public void calculateIncomingLinks() {
    /* loop over all documents in this collection */
    for (int i = 0; i < this.numDocuments(); i++) {
      LinkedDocument doc = (LinkedDocument) this.get(i);

      /* again, loop over all documents of this collection */
      for (int j = 0; j < this.numDocuments(); j++) {
        LinkedDocument incomingDoc = (LinkedDocument) this.get(j);

        /*
         * Check if doc is contained in the outgoing links of incomingDoc. If so, add to
         * incoming links of doc.
         */
        if (incomingDoc.getOutgoingLinks().contains(doc)) {
          doc.addIncomingLink(incomingDoc);
        }
      }
    }
  }


  /**
   * Returns a string representation of this {@link LinkedDocumentCollection}
   * using the IDs of the {@link LinkedDocument}s.
   * 
   * @return a string representation of this collection
   */
  public String toString() {
    if (this.numDocuments() == 0) {
      return "[]";
    }

    if (this.numDocuments() == 1) {
      return "[" + ((LinkedDocument) this.get(0)).getID() + "]";
    }

    String res = "[";
    for (int i = 0; i < this.numDocuments() - 1; i++) {
      res += ((LinkedDocument) this.get(i)).getID() + ", ";
    }
    res += ((LinkedDocument) this.get(this.numDocuments() - 1)).getID() + "]";
    return res;
  }

  /**
   * Sorts this instance descending by the relevance of the contained
   * {@link LinkedDocument}s.
   * 
   * This method returns an array containing the relevance values of the
   * {@link LinkedDocument}s of this instance.
   * 
   * @param dampingFactor   the damping factor that will be used for the
   *                        calculation of the page rank values
   * @param weightingFactor the weighting factor that will be used to weight the
   *                        similarity and the page rank values
   * @return an array containing the relevance values of the
   *         {@link LinkedDocument}s
   */
  private double[] sortByRelevance(double dampingFactor, double weightingFactor) {
    double[] pageRanks = this.pageRank(dampingFactor);
    double[] relevance = new double[this.numDocuments()];
    LinkedDocument[] docMap = new LinkedDocument[this.numDocuments()]; // keep track of relevance<->doc relationship

    /*
     * calculate relevance of all documents and initialize docMap to keep track of
     * the relationship between the relevances in the corresponding array and the
     * according LinkedDocuments
     */
    for (int i = 0; i < this.numDocuments(); i++) {
      relevance[i] = weightingFactor * this.getQuerySimilarity(i) + (1 - weightingFactor) * pageRanks[i];
      docMap[i] = (LinkedDocument) this.get(i);
    }

    /*
     * BubbleSort the relevances descending. !Also change the array of
     * LinkedDocuments. After the loop, the LinkedDocuments are sorted descending
     * according to their relevance
     */
    for (int i = 1; i < this.numDocuments(); i++) {
      for (int j = 0; j < this.numDocuments() - i; j++) {
        if (relevance[j] < relevance[j + 1]) {
          double tmpRel = relevance[j];
          relevance[j] = relevance[j + 1];
          relevance[j + 1] = tmpRel;

          LinkedDocument tmpDoc = docMap[j];
          docMap[j] = docMap[j + 1];
          docMap[j + 1] = tmpDoc;
        }
      }
    }

    /* create new collection containing documents in order according to relevance */
    for (int i = 0; i < this.numDocuments(); i++) {
      this.remove(this.indexOf(docMap[i]));
      this.appendDocument(docMap[i]);
    }

    return relevance;
  }

  /**
   * Matches the {@link LinkedDocument}s of this instance using
   * {@link DocumentCollection#match(String)} and then utilizes
   * {@link LinkedDocumentCollection#sortByRelevance(double, double)} to sort this
   * collection.
   * 
   * This method returns an array containing the relevance values of the
   * {@link LinkedDocument}s of this instance.
   * 
   * @param query           the query
   * @param dampingFactor   the damping factor that will be used for the
   *                        calculation of the page rank values
   * @param weightingFactor the weighting factor that will be used to weight the
   *                        similarity and the page rank values
   * @return an array containing the relevance values of the
   *         {@link LinkedDocument}s
   */
  public double[] match(String query, double dampingFactor, double weightingFactor) {
    super.match(query);

    return this.sortByRelevance(dampingFactor, weightingFactor);
  }

  /**
   * Finds a {@link LinkedDocument} with the given id 
   * if contained in this {@link LinkedDocumentCollection}
   * 
   * @param id           the id to be looked up
   * @return the found {@link LinkedDocument} or null
   */
  public LinkedDocument findByID(String id) {
    for (int i = 0; i < this.numDocuments(); i++)
      if (((LinkedDocument) this.get(i)).getID().equals(id))
        return ((LinkedDocument) this.get(i));
    return null;
  }
}
