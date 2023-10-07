package suchmaschine;

/**
 * The class {@code Document} represents a document.
 * 
 * This class ensures that neither the title nor the language nor the
 * description of the document is <code>null</code>.
 * 
 * @see Date
 * @see Author *
 */
public class Document {
  /**
   * the title of the document
   */
  private String title;

  /**
   * the language of the document
   */
  private String language;

  /**
   * description of the document
   */
  private String description;

  /**
   * the release date of the document
   * 
   * @see Date
   */
  private Date releaseDate;

  /**
   * the {@link Author} of the document
   * 
   * @see Author
   */
  private Author author;

  /**
   * Most common german suffices
   */
  public static final String[] SUFFICES = { "ab", "al", "ant", "artig", "bar", "chen", "ei", "eln", "en", "end", "ent",
  "er", "fach", "fikation", "fizieren", "fähig", "gemäß", "gerecht", "haft", "haltig", "heit", "ieren", "ig", "in",
  "ion", "iren", "isch", "isieren", "isierung", "ismus", "ist", "ität", "iv", "keit", "kunde", "legen", "lein",
  "lich", "ling", "logie", "los", "mal", "meter", "mut", "nis", "or", "sam", "schaft", "tum", "ung", "voll", "wert",
  "würdig", "ie" };

  /**
   * the words of this document and their counts
   */
  private WordCountsArray wordCounts;

  /**
   * Constructs a document with the given values.
   * 
   * If one of the parameters <code>title</code>, <code>language</code> or
   * <code>description</code> is <code>null</code>, the corresponding field is set
   * according to the description of {@link Document#setTitle(String)},
   * {@link Document#setLanguage(String)} and
   * {@link Document#setDescription(String)}.
   * 
   * @param title       the document's title
   * @param language    the language the document is written in
   * @param description a short description of the document
   * @param releaseDate the release date of the document
   * @param author      the author of the document
   * @param content        the text content of this document
   */
  public Document(String title, String language, String description, Date releaseDate, Author author, String content) {
    /* use this methods, just in case the value of the parameters is null */
    this.setTitle(title);
    this.setLanguage(language);
    this.setDescription(description);

    this.releaseDate = releaseDate;
    this.author = author;
    
    this.addContent(content);
  }

  /**
   * Returns the title of the document.
   * 
   * @return the title of the document
   */
  public String getTitle() {
    return title;
  }

  /**
   * Returns the language the document is written in.
   * 
   * @return the language the document is written in
   */
  public String getLanguage() {
    return language;
  }

  /**
   * Returns a short description of the document.
   * 
   * @return a short description of the document
   */
  public String getDescription() {
    return description;
  }

  /**
   * Returns the release date of the document.
   * 
   * @return the release date of the document
   */
  public Date getReleaseDate() {
    return releaseDate;
  }

  /**
   * Returns the author of the document.
   * 
   * @return the author of the document
   * @see Author
   */
  public Author getAuthor() {
    return author;
  }

  /**
   * Returns an {@link WordCountsArray} representing the words and its counts of
   * this document.
   * 
   * @return the words and its counts of this document
   */
  public WordCountsArray getWordCounts() {
    return this.wordCounts;
  }

  /**
   * Returns a brief string representation of this document.
   * 
   * @return a brief string representation of this document
   */
  public String toString() {
    return this.title + " by " + this.author.toString();
  }

  /**
   * Returns the age of this document at the specified date in days.
   * 
   * @param today the specified date
   * @return the age of this document at the specified date:
   */
  public int getAgeAt(Date today) {
    return this.releaseDate.getAgeInDaysAt(today);
  }

  /**
   * Sets the title of the document.
   * 
   * If the new title is <code>null</code>, then the title is set to an empty
   * {@link String}.
   * 
   * @param title the new title
   */
  public void setTitle(String title) {
    if (title == null) {
      this.title = "";
    } else {
      this.title = title;
    }
  }

  /**
   * Sets the language of the document.
   * 
   * If the new language is <code>null</code>, then the language is set to an
   * empty {@link String}.
   * 
   * @param language the new language
   */
  public void setLanguage(String language) {
    if (language == null) {
      this.language = "";
    } else {
      this.language = language;
    }
  }

  /**
   * Sets the description of the document.
   * 
   * If the new description is <code>null</code>, then the description is set to
   * an empty {@link String}.
   * 
   * @param description the new description
   */
  public void setDescription(String description) {
    if (description == null) {
      this.description = "";
    } else {
      this.description = description;
    }
  }

  /**
   * Sets the release date of the document.
   * 
   * @param releaseDate the release date
   */
  public void setReleaseDate(Date releaseDate) {
    this.releaseDate = releaseDate;
  }

  /**
   * Sets the author of the document.
   * 
   * @param author the new author
   */
  public void setAuthor(Author author) {
    this.author = author;
  }

  /**
   * Splits the specified text into its single words.
   * 
   * This method looks for spaces, splits the specified text at the spaces and
   * returns an array of the single words. We assume that the specified text only
   * consists of lower case letters and case.
   * 
   * @param content the text to split
   * @return an array of single words of the argument
   */
  private static String[] tokenize(String content) {
    int wordCount = 0;

    /* count spaces in the content */
    for (int i = 0; i < content.length(); i++) {
      if (content.charAt(i) == ' ') {
        wordCount++;
      }
    }

    // there is always one word more than there are spaces
    wordCount++;

    // the resulting array
    String[] words = new String[wordCount];

    String word = "";
    int wordIndex = 0;

    for (int i = 0; i <= content.length(); i++) {
      /*
       * reached end of content or end of word important: check end of content first!!
       */
      if (i == content.length() || content.charAt(i) == ' ') {
        if (word.length() > 0) {
          /* put word in array */
          words[wordIndex] = word;
          wordIndex++;

          /* start with empty word for next loop */
          word = "";
        }
      } else {
        /* not end of word: append character */
        word = word + content.charAt(i);
      }
    }

    return words;
  }

  private void addContent(String content) {
    String[] words = Document.tokenize(content);

    this.wordCounts = new WordCountsArray(0);

    for (int i = 0; i < words.length; i++) {
      String word = words[i];

      /* find suffix and cut it */
      String suffix = Document.findSuffix(word);
      word = Document.cutSuffix(word, suffix);

      this.wordCounts.add(word, 1);
    }
  }

  /**
   * Determines, whether the last <code>n</code> characters of the specified
   * <code>String</code>s word1 and word2 are equal.
   * 
   * If <code>n</code> &gt; <code>word1.length()</code> or <code>n</code> &gt;
   * <code>word2.length()</code>, <code>false</code> is returned.
   * 
   * @param word1 the first word
   * @param word2 the second word
   * @param n     how many characters to check
   * @return <code>true</code>, if the last <code>n</code> characters of
   *         <code>word1</code> and <code>word2</code> are equal;
   *         <code>false</code> otherwise
   */
  private static boolean sufficesEqual(String word1, String word2, int n) {
    /* if n is too large, last n chars are not equal */
    if (n > word1.length() || n > word2.length()) {
      return false;
    }

    boolean isEqual = true;
    int i = 0;

    while (isEqual && i < n) {
      /* begin comparison at last char */
      if (word1.charAt(word1.length() - 1 - i) != word2.charAt(word2.length() - 1 - i)) {
        isEqual = false;
      }
      i++;
    }

    return isEqual;
  }

  /**
   * This method utilizes {@link Document#SUFFICES} whether to find out, if the
   * specified <code>word</code> ends with one of these suffices.
   * 
   * @param word
   * @return the suffix of the specified word according to
   *         {@link Document#SUFFICES} or an empty string, if there is no suffix.
   */
  private static String findSuffix(String word) {
    if (word == null || word.equals("")) {
      return null;
    }

    String suffix = "";
    String suffixHit = "";
    int i = 0;

    while (i < Document.SUFFICES.length) {
      suffix = Document.SUFFICES[i];

      /* check, if this suffix is a suffix of word */
      if (sufficesEqual(word, suffix, suffix.length())) {
        if (suffixHit.length() < suffix.length()) {
          suffixHit = suffix;
        }
      }

      i++;
    }
    return suffixHit;
  }

  /**
   * If <code>suffix</code> is a suffix of <code>word</code>, then this suffix is
   * cut off from <code>word</code> and the remaining word stem is returned.
   * 
   * If <code>suffix</code> is not a suffix of <code>word</code>, then the word
   * itself is returned
   * 
   * @param word   the word
   * @param suffix the potential suffix of the word
   * @return the word stem of <code>word</code> with the suffix
   *         <code>suffix</code> cut off; or <code>word</code>, if
   *         <code>suffix</code> is not a suffix of word
   */
  private static String cutSuffix(String word, String suffix) {
    if (suffix == null || suffix.equals("")) {
      return word;
    }

    if (word == null) {
      return null;
    }

    /* not a suffix */
    if (!sufficesEqual(word, suffix, suffix.length())) {
      return word;
    }

    /* create word without suffix, by copying all characters of the word stem */
    String wordWithoutSuffix = "";

    for (int i = 0; i < word.length() - suffix.length(); i++) {
      wordWithoutSuffix = wordWithoutSuffix + word.charAt(i);
    }

    return wordWithoutSuffix;
  }

  /**
   * Returns true, if this instance and the specified {@link Document} equal.
   * 
   * @param document the other document
   * @return true, if this instance and the specified {@link Document} equal
   */
  public boolean equals(Document document) {
    if (this == document) {
      return true;
    }

    if (document == null) {
      return false;
    }

    return this.title.equals(document.title) && this.language.equals(document.language)
        && this.description.equals(document.description)
        && ((this.author != null && this.author.equals(document.author))
            || (this.author == null && document.author == null))
        && ((this.releaseDate != null && this.releaseDate.equals(document.releaseDate))
            || (this.releaseDate == null && document.releaseDate == null))
        && ((this.wordCounts != null && this.wordCounts.equals(document.getWordCounts()))
            || (this.wordCounts == null && document.getWordCounts() == null));
  }
}
