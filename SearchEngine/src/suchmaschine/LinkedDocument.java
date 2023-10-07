package suchmaschine;

/**
 * This class represents a linked document.
 * 
 * A linked document is a specialized {@link Document} that has links to other
 * {@link LinkedDocument}s.
 * 
 * @author
 *
 */
public class LinkedDocument extends Document {
  /**
   * the prefix of the links in the document text
   */
  public static final String LINK_PREFIX = "link:";

  /**
   * the ID of this instance; ID is a unique identifier
   */
  private final String id;

  /**
   * the {@link LinkedDocument} this instance links to
   */
  private LinkedDocumentCollection outgoingLinks;

  /**
   * the {@link LinkedDocument}s that link to this instance
   */
  private LinkedDocumentCollection incomingLinks;

  /**
   * the LinkedDocument-IDs where this instance links to
   */
  private String[] outgoingIDs;

  /**
   * Creates a new instance of this class.
   * 
   * @param title       the title of the LinkedDocument
   * @param language    the language of the LinkedDocument
   * @param description the description of the LinkedDocument
   * @param releaseDate the release date of the LinkedDocument
   * @param author      the author of the LinkedDocument
   * @param text        the text of the LinkedDocument
   * @param id          the unique ID of the LinkedDocument
   */
  public LinkedDocument(String title, String language, String description, Date releaseDate, Author author, String text,
      String id) {
    super(title, language, description, releaseDate, author, text);

    this.id = id;
    this.incomingLinks = new LinkedDocumentCollection();

    this.outgoingIDs = this.findOutgoingIDs(text);
    this.outgoingLinks = null;
    this.setLinkCountZero();
  }

  /**
   * Returns a new {@link LinkedDocument} instance, that is created from the
   * specified file.
   * 
   * A valid file according to this method has 2 lines as follows:
   * <ol>
   * <li>The title of the document</li>
   * <li>The text of the document, including links (link:...)</li>
   * </ol>
   * 
   * If the file cannot be read correctly or if it has less than 2 lines,
   * <code>null</code> will be returned.
   * 
   * @param fileName the file to be transformed to a {@link LinkedDocument}
   * @return a new {@link LinkedDocument} instance, that is created from the
   *         specified file.
   */
  public static LinkedDocument createLinkedDocumentFromFile(String fileName) {
    String[] fileContent = Terminal.readFile(fileName);

    if (fileContent != null && fileContent.length >= 2) {
      String title = fileContent[0];
      String text = fileContent[1];

      return new LinkedDocument(title, "", "", null, null, text, fileName);
    } else {
      return null;
    }
  }

  /**
   * Searches the {@link WordCountsArray} of this instance for links (beginning
   * with {@link LinkedDocument#LINK_PREFIX}) and sets their count to
   * <code>0</code>.
   */
  private void setLinkCountZero() {
    WordCountsArray wca = this.getWordCounts();

    for (int i = 0; i < wca.size(); i++) {
      if (wca.getWord(i).startsWith(LinkedDocument.LINK_PREFIX)) {
        wca.setCount(i, 0);
      }
    }
  }

  /**
   * Returns <code>true</code>, if this instance and the specified
   * {@link Document} equal.
   * 
   * @param doc the 2nd document
   * @return <code>true</code>, if the documents equal, <code>false</code>
   *         otherwise
   */
  @Override
  public boolean equals(Document doc) {
    if (doc instanceof LinkedDocument) {
      return this.id.equals(((LinkedDocument) doc).id);
    } else {
      return super.equals(doc);
    }
  }

  /**
   * Returns the identifier of this instance.
   * 
   * @return the identifier of this instance
   */
  public String getID() {
    return this.id;
  }

  /**
   * This private helper method uses the {@link String}-Array of IDs. It creates a
   * new {@link LinkedDocument} for every ID and adds it to the
   * {@link LinkedDocumentCollection} of outgoing links, if it is not a self-link.
   */
  private void createOutgoingDocumentCollection() {
    this.outgoingLinks = new LinkedDocumentCollection();

    for (int i = 0; i < this.outgoingIDs.length; i++) {
      LinkedDocument newDoc = LinkedDocument.createLinkedDocumentFromFile(this.outgoingIDs[i]);

      /* do not add links to this page (page pointing to itself) */
      if (!this.equals(newDoc)) {
        this.outgoingLinks.appendDocument(newDoc);
      }
    }
  }

  /**
   * This private helper method uses the {@link String}-Array of IDs. It creates a
   * new {@link LinkedDocument} for every ID and adds it to the
   * {@link LinkedDocumentCollection} of outgoing links, if it is not a self-link.
   */
  private void createOutgoingDocumentCollection(LinkedDocumentCollection cache) {
    this.outgoingLinks = new LinkedDocumentCollection();

    LinkedDocument newDoc;
    for (int i = 0; i < this.outgoingIDs.length; i++) {
      newDoc = cache.findByID(this.outgoingIDs[i]);

      if (newDoc == null)
        newDoc = LinkedDocument.createLinkedDocumentFromFile(this.outgoingIDs[i]);

      /* do not add links to this page (page pointing to itself) */
      if (!this.equals(newDoc)) {
        this.outgoingLinks.appendDocument(newDoc);
      }
    }
  }

  /**
   * This method returns a {@link LinkedDocumentCollection} that contains all
   * {@link LinkedDocument}s that this instance links to.
   * 
   * @return a {@link LinkedDocumentCollection} containing all
   *         {@link LinkedDocument} this instance links to
   */
  public LinkedDocumentCollection getOutgoingLinks() {
    if (this.outgoingLinks == null) {
      this.createOutgoingDocumentCollection();
    }

    return this.outgoingLinks;
  }

  /**
   * This method returns a {@link LinkedDocumentCollection} that contains all
   * {@link LinkedDocument}s that this instance links to.
   * 
   * @return a {@link LinkedDocumentCollection} containing all
   *         {@link LinkedDocument} this instance links to
   */
  public LinkedDocumentCollection getOutgoingLinks(LinkedDocumentCollection cache) {
    if (this.outgoingLinks == null) {
      this.createOutgoingDocumentCollection(cache);
    }

    return this.outgoingLinks;
  }

  /**
   * This private helper method finds the outgoing links in the specified text.
   * 
   * A link always begins with {@link LinkedDocument#LINK_PREFIX}. This method
   * returns an array of all IDs that are linked in the specified text or
   * <code>null</code>, if the specified text is <code>null</code>.
   * 
   * @param text the text to find the outgoing links in.
   * @return array of all IDs that are linked in the specified text or
   *         <code>null</code>, if the specified text is <code>null</code>
   */
  private String[] findOutgoingIDs(String text) {
    if (text == null) {
      return null;
    }

    String textCopy = new String(text);

    /* the number of words in the WordCountsArray is sufficient */
    String[] tmpIDs = new String[this.getWordCounts().size()];

    /* get the next index of the LINK_PREFIX */
    int index = textCopy.indexOf(LinkedDocument.LINK_PREFIX);

    int noOfIDs = 0;

    /* loop ends, when index is not found */
    while (index != -1) {
      /* divide the text at the found index */
      String strBeforeLink = textCopy.substring(0, index);
      String strWithLink = textCopy.substring(index);

      int endIndex = strWithLink.indexOf(' ');

      String link;

      /* if endIndex is not found, we are looking at the last word */
      if (endIndex == -1) {
        link = strWithLink.substring(0);
        textCopy = strBeforeLink;
      } else {
        /* otherwise, there are more words */
        link = strWithLink.substring(0, endIndex);
        textCopy = strBeforeLink + strWithLink.substring(endIndex + 1);
      }

      /* add the ID to array */
      tmpIDs[noOfIDs] = link.substring(LinkedDocument.LINK_PREFIX.length());
      noOfIDs++;

      /* get next index for next loop */
      index = textCopy.indexOf(LinkedDocument.LINK_PREFIX);
    }

    /* create new array with the size according to the actual number of found IDs */
    String[] ids = new String[noOfIDs];

    for (int i = 0; i < noOfIDs; i++) {
      ids[i] = tmpIDs[i];
    }

    return ids;
  }

  /**
   * Adds the specified {@link LinkedDocument} to the
   * {@link LinkedDocumentCollection} that represents the {@link LinkedDocument}s
   * that link to this instance.
   * 
   * If the specified {@link LinkedDocument} is equal to this instance, it is not
   * added, since self-links are not allowed.
   * 
   * @param incomingLink the {@link LinkedDocument} to add
   */
  public void addIncomingLink(LinkedDocument incomingLink) {
    /* do not allow link on itself */
    if (!this.equals(incomingLink)) {
      /* addLast() will do the check, if document is already contained */
      this.incomingLinks.appendDocument(incomingLink);
    }
  }

  /**
   * Returns a {@link LinkedDocumentCollection} of {@link LinkedDocument}s that
   * link to this instance.
   * 
   * @return a {@link LinkedDocumentCollection} of {@link LinkedDocument}s that
   *         link to this instance
   */
  public LinkedDocumentCollection getIncomingLinks() {
    return this.incomingLinks;
  }
}
