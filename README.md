# Highlevel Architecture

**This application is composed of the following components:**


1. A Springboot Web application which allows a user to adopt a pet.
2. A HarperDB which is a NO-SQL DB to store the user's choice.
3. A process model deployed in a Camunda cluster to provide a form for the user to select a pet to adopt.

The Web app and the DB were packaged using docker-compose and run as containers in the docker desktop.

See the diagram ![Pet Adoption App Highlevel Architecture](camundaPetAdoptionApp.pdf)


# Deploying and running the application as container

**To deploy and start the containers:**

1. Ensure the Camunda cluster is created and running healthy
2. Start the Docker Desktop
3. Run docker-compose up -d

Once the web app is started, it will deploy the process model [pick-an-animal.bpmn](src/resources/pick-an-animal.bpmn)  to the Camunda cluster (connection properties were configured in the application.properties) and start a process instance automatically.  

The process id is printed out in the app console.

A user can then assign the process to him/herself and select a pet to adopt.  The application will make an API call to get a random photo of the pet selected and store the selection as a record to the DB.

The record id is returned to the process as shown in attached [screenshot]: (screenShotShowingUserRecord.jpg)

User can retrieve the pet photo using the ID

**To retrieve the photo of the pet adopted, use the following API call:**
    
    http://localhost:8080/pet/{recordId}
    
    e.g.
    
    http://localhost:8080/pet/Mark-2024-09-07-14-08-46-0400-Cat
    


**To retrieve an adoption record:**

    http://localhost:8080/userchoice/{recordId}
    
    e.g.
    
    http://localhost:8080/userchoice/Mark-2024-09-06-18-59-16-0400

# Run Test cases
**To run test cases, execute the following command:**

    mvn clean verify

# Debug tips
    // Return the definitions of all databases and tables within the database.
    curl --location 'http://localhost:9925' \
    --header 'Authorization: Basic cm9vdDpwYXNzd29yZA==' \
    --header 'Content-Type: application/json' \
    --data '{"operation": "describe_all"}'
    
    curl --location 'http://localhost:9925' \
    --header 'Authorization: Basic cm9vdDpwYXNzd29yZA==' \
    --header 'Content-Type: application/json' \
    --data '{"operation": "describe_table", "table": "userchoice", "database": "animals_app"}'
    
    # Fetch the pets chosen by a user
    curl --location 'http://localhost:9925' \
    --header 'Authorization: Basic cm9vdDpwYXNzd29yZA==' \
    --header 'Content-Type: application/json' \
    --data '{
        "operation": "search_by_value",
        "database": "animals_app",
        "table": "userchoice",
        "search_attribute": "username",
        "search_value": "Mark",
        "get_attributes": [
            "id",
            "imageURL",
            "animal"
        ]
    }'        
    
    # Find out who likes Dog
    curl --location 'http://localhost:9925' \
    --header 'Authorization: Basic cm9vdDpwYXNzd29yZA==' \
    --header 'Content-Type: application/json' \
    --data '{
        "operation": "search_by_value",
        "database": "animals_app",
        "table": "userchoice",
        "search_attribute": "animal",
        "search_value": "Dog",
        "get_attributes": [
            "username"
        ]
    }'        