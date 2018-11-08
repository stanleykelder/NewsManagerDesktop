/**
 * 
 */
package application;


import application.news.Article;
import application.news.User;

/**
 * @author ÁngelLucas
 *
 */
public class NewsDetailsController {
	//TODO add attributes and methods as needed
	    private User usr;
	    private Article article;

	    

		/**
		 * @param usr the usr to set
		 */
		void setUsr(User usr) {
			this.usr = usr;
			if (usr == null) {
				return; //Not logged user
			}
			//TODO Update UI information
		}

		/**
		 * @param article the article to set
		 */
		void setArticle(Article article) {
			this.article = article;
			//TODO complete this method
		}
}
