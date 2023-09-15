# Description
The project runs on Gradle Spring Boot and MongoDb database. It is of the form of an MVC project with the controllers
stored in Library/interaction.

## Assumptions
* Once items added. The items won't be removed
* Items are only added at the start. Emptying the inventory and adding the new ones.
* Need Item Title and Item Type for searches.
* **BodyJsonObjects has examples for requests to be made**.
* Millions of records to be stored. Chose to store in Mongo Database as we can implement sharding and horizontal scaling easily.
* Database is running on port 27017.

## Run Docker Container for Mongo Database
To run docker:
```
docker-compose up -d
``` 
Verify mongodb defaults to port 27017.

## Run the Spring Boot Application
Navigate to LibraryApplication class and run the class. This should start up
a Spring Boot service which is connected to the database. The API endpoints are
defined in the interaction folder. It has Transaction, User and Library Service.

## Send Request to API endpoint
### User Service (UserService.java) "/user"
**Before using the library service. Create a user as this is a prerequisite for the service.**
* **/create**: Send a POST request to create user. Should have unique email and Not empty name. Returns User object
* **/delete**: Send a DELETE request.

### Library Service (LibraryService.java) "/library" (IngestData.json - For Initial Data)
* **/init**: Send a POST request with a json object. Ingests initial data and clears database as well. Example: ingestData.json is in the BodyJsonObjects folder.
* **/inventory**: Send a GET request. Returns inventory grouped by Item type. Empty if none found
* **/borrow**: Send a PUT request with Json Object in body. Returns object which has been borrowed. 
* **/return**: Send a PUT request with Json Object in body. Returns object which has been returned.
* **/overdue**: Send a GET request. Returns a list of items that are overdue. Empty if none found
* **/available**: Send a POST request with Json Object in body. Returns object if found or NOT_FOUND.
* **/user/items/{userId}**: Send a GET request with UserId from User table. Returns all items borrowed by user. Empty if none.

### Transaction Service (TransactionService.java) "/transaction"
* **/item/{itemId}**: Send a GET request to get all transactions for Unique Item Id. Returns empty if none found.
* **/user/{userId}**: Send a GET request to get all transactions for user. Returns empty if none found.

## Notes
* Written in Spring MVC architecture structure.
* Implemented a **Cron Job**(Service/OverdueItemSchedulerService) which checks for overdue items at midnight and updates the database.
* **Reentrant Lock** has been implemented to make the database CRUD operations thread safe.
* Transaction is only populated for BORROW and RETURN Actions.
* Tests have been written to validate the expected outcomes. Please note that the tests require the mongoDb docker image to be running.
* For millions of records, we can use sharding which is available with Mongo Database.