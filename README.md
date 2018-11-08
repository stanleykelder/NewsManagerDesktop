# NewsManagerDesktop
Desktop version of our news manager project


• Public users (login is not needed):
  They can access to all published articles (news).
  They can see articles details.
  They can create a new article but it can only saves It to file
  
• Registered users (login is required):
  Can see all published articles.
  Can edit articles that belong to the user.
  Can create a new article.
  Can delete articles that belong to the user.
  
  
  Main Window Functionalities 
  
  1. List of all published news. Login is not required.
  2. Login
  3. New. Allows create a new news. Only logged in users can send news to server. Users can save
  news to a file, in this case login is not required.
  4. Load a news from a file.
  5. Edit. Login is required. User can edit a news, if only if, this news belonging to the user.
  6. Delete. Login is required. User can delete a news, if only if, this news belonging to the user.
  7. Exit.
  8. Headlines list. Shows a list with all headlines. When user selects one headline: Command ‘read
  more’ will be enabled, news image and abstract will be shown. Moreover, if news belongs to
  the logged user, commands ‘Edit’ and ‘Delete’ will be enabled.
  9. Category filter. Filter headlines that will be listed. Only headlines with the selected category will
  be listed. If category is equals to ALL, all headlines will be listed.
  10. ReadMore.Allowsanyusertoaccesstotheselectednewsdetails
  
  
  Form for news details
  
  1. Always display news details: image, title, subtitle, category
  2. At the beginning this form shows news body
  3. Switch between body and abstract
  4. Back to the previous form
  
  
  Form for Editing and Creating news
  
  • Add an image. Student can use the provided form ‘ImagePicker’ to implement this functionality
  • Add title and subtitle
  • Define news category
  • Add abstract and body. These elements can be edited in plain text or html text
  • Send the new or modify news to a server and back to main window. To send a news, title and category must have been      defined.
  • Save a draft to a file. This command saves the news to a file in the local machine. This draft could be loaded again for editing it or send it to a server. To save a news, title must have been defined.
• Back. This command discards all changes made since last ‘Save to File’ command
