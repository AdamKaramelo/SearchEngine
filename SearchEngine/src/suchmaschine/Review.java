package suchmaschine;

/**
 * The class {@code Review} represents a review of a {@link Document}.
 * 
 * @see Document
 * @see Author
 * @see Date
 * 
 *
 */
public class Review {
  /**
   * the {@link Author} of the review
   * 
   * @see Author
   */
  private Author author;

  /**
   * the reviewed {@link Document}
   * 
   * @see Document
   */
  private Document reviewedDocument;

  /**
   * the language of the review
   */
  private String language;

  /**
   * the release date of the review
   * 
   * @see Date
   */
  private Date releaseDate;

  /**
   * the rating of the document, ranging from 0 to 10
   */
  private int rating;

  /**
   * the text content of the review
   */
  private String content;

  /**
   * the maximum possible rating
   */
  public static final int MAX_RATING = 10;

  /**
   * the minimum possible rating
   */
  public static final int MIN_RATING = 0;

  /**
   * Constructs a review with the given values
   * 
   * @param author           the author of the review
   * @param reviewedDocument the document that has been reviewed
   * @param language         the language the review is written in
   * @param releaseDate      the release date of the review
   * @param rating           the rating of the reviewed document
   */
  public Review(Author author, Document reviewedDocument, String language, Date releaseDate, int rating, String content) {
    this.setAuthor(author);
    this.setReviewedDocument(reviewedDocument);
    this.setLanguage(language);
    this.setReleaseDate(releaseDate);
    this.setRating(rating);
    this.setContent(content);
  }

  /**
   * Returns the author of the review.
   * 
   * @return the author of the review
   * @see Author
   */
  public Author getAuthor() {
    return author;
  }

  /**
   * Returns the reviewed document.
   * 
   * @return the reviewed document
   * @see Document
   */
  public Document getReviewedDocument() {
    return reviewedDocument;
  }

  /**
   * Returns the language the review is written in.
   * 
   * @return the language the review is written in
   */
  public String getLanguage() {
    return language;
  }

  /**
   * Returns the release date of the review.
   * 
   * @return the release date of the review
   * @see Date
   */
  public Date getReleaseDate() {
    return releaseDate;
  }

  /**
   * Returns how the reviewed document has been rated.
   * 
   * @return how the reviewed document has been rated
   */
  public int getRating() {
    return rating;
  }

  /**
   * Returns the written review text content.
   * 
   * @return the written review text content
   */
  public String getContent() {
    return content;
  }


  /**
   * Returns a brief string representation of this review.
   */
  public String toString() {
    return this.reviewedDocument.toString() + " is rated " + this.rating + " by " + this.author.toString();
  }

  /**
   * Returns the age of this review at the specified date in days.
   * 
   * @param today the specified date
   * @return the age of this review at the specified date:
   */
  public int getAgeAt(Date today) {
    return this.releaseDate.getAgeInDaysAt(today);
  }

  /**
   * Sets the {@link Author} of this review.
   * 
   * @param author the new author
   */
  public void setAuthor(Author author) {
    this.author = author;
  }

  /**
   * Sets the language of this review.
   * 
   * If the specified language is <code>null</code>, then the language is set to
   * an empty {@link String}.
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
   * Sets the content of this review.
   * 
   * If the specified content is <code>null</code>, then the content is set to
   * an empty {@link String}.
   * 
   * @param content the new content
   */
  public void setContent(String content) {
    if (content == null) {
      this.content = "";
    } else {
      this.content = content;
    }
  }

  /**
   * Sets the reviewed {@link Document} of this review.
   * 
   * @param doc the new reviewed document
   */
  public void setReviewedDocument(Document doc) {
    this.reviewedDocument = doc;
  }


  /**
   * Sets the release date {@link Date} of this review.
   * 
   * @param doc the new release date
   */
  public void setReleaseDate(Date date) {
    this.releaseDate = date;
  }

  /**
   * Sets the rating of this review.
   * 
   * If the specified rating is lower than {@link Review#MIN_RATING}, then the
   * rating is set to {@link Review#MIN_RATING}. If the specified rating is
   * greater than {@link Review#MAX_RATING}, then the rating is set to
   * {@link Review#MAX_RATING}.
   * 
   * @param rating the new rating
   */
  public void setRating(int rating) {
    if (rating < Review.MIN_RATING) {
      this.rating = Review.MIN_RATING;
    } else if (rating > Review.MAX_RATING) {
      this.rating = Review.MAX_RATING;
    } else {
      this.rating = rating;
    }
  }

  /**
   * Returns true, if this instance and the specified {@link Review} equal.
   * 
   * @param review the other Review
   * @return true, if this instance and the specified {@link Review} equal
   */
  public boolean equals(Review review) {
    if (this == review) {
      return true;
    }

    if (review == null) {
      return false;
    }

    return this.language.equals(review.language) && this.rating == review.rating
        && ((this.author != null && this.author.equals(review.author))
            || (this.author == null && review.author == null))
        && ((this.releaseDate != null && this.releaseDate.equals(review.releaseDate))
            || (this.releaseDate == null && review.releaseDate == null))
        && ((this.reviewedDocument != null && this.reviewedDocument.equals(review.reviewedDocument))
            || (this.reviewedDocument == null && review.reviewedDocument == null));
  }
}
