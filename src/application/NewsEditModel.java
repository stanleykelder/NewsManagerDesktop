/**
 * 
 */
package application;

import application.news.Article;
import application.news.Categories;
import application.news.User;
import javafx.beans.property.Property;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;


/**
 * This class is used to represent an article when it is editing
 * This class is needed to develop NewsEditController
 * @author Ã�ngelLucas
 *
 */
class NewsEditModel {
	//Reference to original article. Useful for undo changes
	private Article original;
	//Reference to modified article
	private Article edited;
	//User that made article modifications (not used in this version) 
	private User user;
	/**
	 * Flag used to indicate that exist a modified article.
	 * - False: unmodified
	 * - True: modified
	 */
	private boolean bModified = false;
	NewsEditModel (User usr){
		this.user = usr;
		original = new Article();
		if (user!=null) {
			original.setIdUser(user.getIdUser());
		}
			
		edited = new Article (original);
		addedChangeListener();
	}
	
	NewsEditModel (User usr, Article org){
		this.user = usr;
		original = org;
		edited = new Article (original);
		addedChangeListener();
	}

	//Getters and setters
	
	/**
	 *  
	 * @return a copy of edited article body
	 */
	String getBodyText() {
		return edited.getBodyText();
	}

	/**
	 * @return a StringPorperty of edited article body.
	 * The returned value is suitable for binding
	 */
	StringProperty bodyTextProperty(){
		return edited.bodyTextProperty();
	}
	
	/**
	 * @return a StringPorperty of edited article title.
	 * The returned value is suitable for binding
	 */
	StringProperty titleProperty(){
		return edited.titleProperty();
	}
	
	/**
	 * @return a StringPorperty of edited article title.
	 * The returned value is suitable for binding
	 */
	StringProperty subtitleProperty(){
		return edited.subtitleProperty();
	}
	
	
	/**
	 * @return the original article if only if has a valid title
	 */
	Article getArticleOriginal(){
		return (this.original != null &&
				this.original.getTitle() != null &&
				! this.original.getTitle().equals(""))? this.original : null;
	}
	
	
	/**
	 * Change the associated image in the edited article
	 * @param urlImage uri to an image. The image will be loaded
	 */
	void setUrlImage(String urlImage) {
		edited.setUrlImage(urlImage);
		this.bModified = true;
	}
	

	/**
	 * Change the associated image in the edited article
	 * @param image new image for the edited article
	 */
	void setImage(Image image) {
		edited.setImageData(image);
		this.bModified = true;
	}
	
	/**
	 * 
	 * @return a copy the edited article title
	 */
	public String getTitle() {
		return edited.getTitle();
	}
	
	/**
	 * 
	 * @return a copy the edited article subtitle
	 */
	public String getSubtitle() {
		return edited.getSubtitle();
	}
	
	/**
	 * 
	 * @return isDeleted from the edited article
	 */
	boolean isDeleted(){
		return edited.isDeleted();
	}
	
	/**
	 * @return a Property of edited article isDeleted.
	 * The returned value is suitable for binding
	 */
	public Property<Boolean> isDeletedProperty() {
		return edited.isDeletedProperty();
	}
	
	/**
	 * 
	 * @return category from the edited article
	 */
	public Categories getCategory() {
		return Categories.valueOf(
				edited.getCategory().toUpperCase());
	}
	
	/**
	 * Set category to the edited article
	 * @param category new category for edited article
	 */
	public void setCategory(Categories category){
		this.bModified = true;
		edited.setCategory(category.toString());
	}



	
	/**
	 *  
	 * @return a copy of edited article abstract
	 */
	public String getAbstractText() {
		return edited.getAbstractText();
	}

	/**
	 * @return a StringPorperty of edited article abstract.
	 * The returned value is suitable for binding
	 */
	public StringProperty abstractTextProperty() {
			return edited.abstractTextProperty();
	}

	// Setters
	
	/**
	 * Set title to the edited article
	 * @param title new title for edited article
	 */
	public void setTitle(String title){
		this.bModified = true;
		edited.setTitle(title);
	}
	

	/**
	 * Set subtitle to the edited article
	 * @param subtitle new subtitle for edited article
	 */
	public void setSubtitle(String subtitle){
		this.bModified = true;
		edited.setSubtitle(subtitle);
	}

	/**
	 * Set body to the edited article
	 * @param body new body for edited article
	 */
	public void setBody(String body){
		this.bModified = true;
		edited.setBodyText(body);
	}

	
	/**
	 * Make changes permanent. Changes can't be undone 
	 * Copy article data from the edited one to the original
	 */
	public void commit(){
		if (!this.bModified)
			return; // Nothing to do
		copyArticleData(this.edited, this.original);
		this.bModified = false;
		this.original.setNeedBeSaved(true);
	}
	
	/**
	 * Discard all changes. Changes will be lost
	 * Copy article data from the original one to the edited
	 * 
	 */
	public void discardChanges (){
		if (!this.bModified)
			return; // Nothing to do
		copyArticleData(this.original, this.edited);
		this.bModified = false;
	}



	
	/**
	 * Copy article data from source to dest
	 * @param source article to copy
	 * @param dest copy from the original (source)
	 */
	private void copyArticleData (Article source, Article dest){
		dest.setAbstractText(
				source.getAbstractText());
		dest.setBodyText(source.getBodyText());
		dest.setTitle(source.getTitle());
		dest.setSubtitle(source.getSubtitle());
		dest.setCategory(
				source.getCategory());
		dest.setDeleted(
				source.getDeleted());
		dest.setImageData(source.getImageData());
	}
	
	/**
	 *  This method adds a change listener to all article
	 *  properties in order to know whenever
	 *  there is a change on edited article
	 */
	private void addedChangeListener(){
 	 this.edited.abstractTextProperty().addListener(
				 (observable, oldvalue, newvalue) ->this.bModified =  true);
 	this.edited.bodyTextProperty().addListener(
			 (observable, oldvalue, newvalue) ->this.bModified =  true);
 	this.edited.isDeletedProperty().addListener(
			 (observable, oldvalue, newvalue) ->this.bModified =  true);
 	this.edited.titleProperty().addListener(
			 (observable, oldvalue, newvalue) ->this.bModified =  true);
 	this.edited.subtitleProperty().addListener(
			 (observable, oldvalue, newvalue) ->this.bModified =  true);
	}
}
