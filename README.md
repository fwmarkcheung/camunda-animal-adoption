# Highlevel Architecture

**This application is composed of the following components:**


1. The Camunda process enginee (**Zeebe**) which drives the workflow for the pet adoption process by interacting with the various components.  A BPMN process is deployed and run in a Camunda cluster. It provides a form for a user to select a pet to adopt and defines a workflow for the pet adoption process.
2. A Springboot Web application which provides API calls to retrieve the information about the adoption process. For example, the photo of the pet being adopted and query about the adoption meta data.
3. A HarperDB which is a NO-SQL DB to store the adoption data.
4. A third party microservice to provide a photo of the pet.

The Web app and the DB were packaged using docker-compose and run as containers in the docker desktop.

See the diagram ![Pet Adoption App Highlevel Architecture](camundaPetAdoptionApp.pdf)

# Run Test cases
**To run test cases, execute the following command:**

    mvn clean verify

# Using Docker Compose to run the application

**To deploy and start the containers in a docker engine**

1. Ensure the Camunda cluster is created and running healthy
2. Start Docker Desktop
3. Execute the docker-compose command below:

    	docker-compose up -d

The docker engine will run the web app and database as containers.

During the bootstrap, the web application will perform the followings:
1. Deploy the process model [pick-an-animal.bpmn](src/main/resources/pick-an-animal.bpmn)  to a Camunda cluster specified by the connection properties in the [application.properties] (src/main/resources/application.properties)
2. Start a process instance automatically and print out process id in the app console.

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