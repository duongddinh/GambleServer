## GambleServer:

   + Listening on port 8989
   + All the special character are the substring(0) of the server message, use that as a guidance to implement the server
   
## How the GUI should implement the server:

   + To login, send to server ```in username password``` (username cannot have a space or special character)
   
   + to sign up, send to server ```up username password``` (username cannot have a space or special character)
   
   + To request changing username, send to server ```!# [String new username]```
   
   + To change password, send to server ```$# [String new password]```
   
   + To request loading user data, send to server ```##```
   
   + To delete account, send to server ```!!```
   
   + To tell server to write data and save data, send to server ```** [String Receiver]```
   
   + Send to server ```*! [String list of users]``` to send the list of removed user 
   
   + The server will send to client ```!``` in case of wrong password and ```$``` in case of correct password
   
   + Get number from atmospheric noise  ```__```
   
   + Get number from radioactive decay  ```_!```
      
## Other:

   + server will create data for every chat and chat history, deleting user account will not delete their chat history
   
   + If there is an error, server will send ```&```
   
   + file will be created in the form of ```sender->receiver1.txt```
   
   + file ```userData.txt``` in form of ```username password coins [__ self]```
   
   + the server send challenge to another user in a form of ```@username guess amountGamble```
      
   + server also stores all the file relate to users in case the server shutdown. 
