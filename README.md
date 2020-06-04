# MeuTako
MeuTako allows users to track all finantial transactions in a few clicks and automatically generate monthly reports.

### Description
People use money daily to respond to some personal needs, they need to be alert to how they spend money, to know where the money goes, what they spend the most with. It becomes a difficult task to have control of all daily transactions when it comes to manual registration in notepad, applications such as excel spreadsheets even when it is to extract reports on expenses. Some daily expenses can slip away and have the wrong reports. 	 Contributing to a good financial education..
### Intended Users
MeuTako is intended to anyone who wants to have a clear control of his/her money.
### Features
- Save money transaction
- View transaction details
- Attach pictures to transaction
- View daily and monthly transactions
- Save categories
- Notify users to register transaction

You can also:
  - Share to friends
  - Show widget of last 5 transactions
# Key Considerations
### Data persistence? 

This app uses Firebase services like Authentication to save users, Firestore to persist users data. Also it is able to persist data locally when user if offline and will sync with firestore when users go online, 
### Edge or corner cases in the UX.

User may face problems on adding data to firestore database and storage due to Firebase Plan limitation. Subscribed plan allow for example up to 20k writes per day and 50k reads per day. When number of users increases writes and reads will blow up as each users may write many transactions per day. For example with 1k users it allows only 20 writes a day, then data is not persisted in firestore 
### Libraries

- Picasso to handle the loading and caching of images.
- FirebaseUI to handle users authentication
- Firebase Firestore to handle data persistence
- Firebase storage to save images
- MPAndroidChart to handle report charts
- StickyHeaders to create sectioned list of transactions
