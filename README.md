# Overview
This application is a simple Camunda Client App for Camunda 8, that gets a random picture of a cat, a dog, or a bear.

During bootstrap, it deploys a process definition to a Camunda cluster and starts a process instance to pick an animal.

A user can then assign the task to him/herself to pick a cat, a dog, or a bear via a form presented by Camunda.

Once an animal is selected, the application will pick a random picture of the animal by calling a third party system throuhg an API rest call.

The application then stores the photo as a record to a database.

The record id is returned to the process as shown in attached [screenshot]: (screenShotShowingUserRecord.jpg)

User can retrieve the pet photo or the record details using the following API calls:


**To retrieve the photo of the pet adopted, use the following API call:**
    
    http://localhost:8080/pet/{recordId}
    
    e.g.
    
    http://localhost:8080/pet/Mark-2024-09-07-14-08-46-0400-Cat
    


**To retrieve an adoption record:**

    http://localhost:8080/userchoice/{recordId}
    
    e.g.
    
    http://localhost:8080/userchoice/Mark-2024-09-06-18-59-16-0400


# Highlevel Architecture
**This application is composed of the following components:**

1. A **Springboot Web application** which provides API calls to retrieve the information about the adoption process. For example, the photo of the pet being adopted and query about the adoption meta data.
2. A **HarperDB** which is a NO-SQL DB to store the adoption data.

It interacts with the following external systems:

1. A Camunda process engine (**Zeebe**).  It drives the workflow for the pet adoption process by interacting with the various components.  A BPMN process is deployed and run in a Camunda cluster. It provides a form for a user to select a pet to adopt and defines a workflow for the pet adoption process.
2. **A third party microservice** to provide a photo of the pet selected.

See the diagram ![Pet Adoption App Highlevel Architecture](camundaPetAdoptionApp.pdf)

During the bootstrap, the web application will perform the followings:
1. Deploy the process model [pick-an-animal.bpmn](src/main/resources/pick-an-animal.bpmn)  to a Camunda cluster specified by the connection properties in the [application.properties] (src/main/resources/application.properties)
2. Start a process instance and print the process id in the app console.

The application can be deployed either by docker-compose or Helm Chart to a K8s cluster.

# Deploy using Docker Compose

**To deploy and start the containers in a docker engine**

1. Ensure the Camunda cluster is created and running healthy.
2. Start Docker Desktop.
3. Change directory to where the docker-compose.yml is stored.
4. Execute the docker-compose command below:

    	docker-compose up -d

The docker engine will run the web app and database as containers.  

The database will be deployed first and the web application will ping the database to make sure it is healthy before it starts.

**Docker build optimization**

The Dockerfile is optimized to only update the required docker image layer if the application image needs to be rebuilt due to code/configuration changes.


**To shutdown the containers and cleanup images, execute the following commands:**

    docker compose down;docker rmi -f camunda-animal-adoption-web harperdb/harperdb

# Deploy using Helm Chart

## Prerequisites

### SpringBoot app Docker image setup
It is assumed the springboot web app image was published to the Docker hub as:

	<userId>/camunda-animal-adoption-app:latest

In my case, the web image was published as:

	fwmarkcheung/camunda-animal-adoption-app:latest

Otherwise, follow the steps below to push the images to your own Docker Hub

1. Login to Docker Hub
2. Create a public repository
3. After you ran the docker compose, a web application will be built as a local image.  To check the image name, execute the command:

		$ docker images | grep cam
		
		Output:
		
		camunda-animal-adoption-web               latest                                                                        39e9f84ffc1f   9 hours ago     387MB
4. Tag the local image by executing the command:

		$ docker tag camunda-animal-adoption-web:latest <userid>/camunda-animal-adoption-app:latest

5. Push the image to Docker hub:

		$ docker push <userid>/camunda-animal-adoption-app:latest

6. Update the* image* section in the* values.yaml *as follow: 

   	 web:
    		image:
    			repository: <userid>/camunda-animal-adoption-app
    			tag: latest

### Setup an ingress controller
To expose the application beyond the K8s cluster:  

- Your Kubernetes cluster must have an Ingress Controller (e.g., NGINX Ingress Controller) installed.
- You need a DNS record (or IP) that points to the Ingress controller's external IP address.

Run the following commands to install nginx as an Ingress controller:
```bash
helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
helm repo update
helm install nginx-ingress ingress-nginx/ingress-nginx --namespace ingress-nginx --create-namespace
```

Verify nginx is running healthy using the following command:

    $ kubectl get pods --namespace ingress-nginx
	
	Expected Output
	
	NAME                                                      READY   STATUS    RESTARTS      AGE
	nginx-ingress-ingress-nginx-controller-xxx   1/1     Running   1 (92m ago)   5h25m

	
	
	
	
	
	


    

### Deploy the app using Helm Chart

1. Optionally, create the namespace *animal-adoption*.  For example, using kubectl:

		kubectl create namespace animal-adoption

2. Change directory to the helm chart file directory.

3. Install the helm chart specifying the namespace as argument:

		helm install animal-adoption-app . --namespace animal-adoption --create-namespace

This command:

- Installs the chart into the animal-adoption namespace.
- Automatically creates the namespace if it does not exist.

**To get the running pods:**

        
		$ kubectl get pods --namespace=animal-adoption
		
		Expected Output
		
		NAME                                           READY   STATUS    RESTARTS   AGE
		animal-adoption-app-database-xxx   1/1     Running   0          9s
		animal-adoption-app-web-xxx        1/1     Running   0          9s

The application is exposed out of the K8s cluster using an ingress.  If you run application locally and use a ClusterIP service, you can temporarily port-forward to access the app:

	kubectl port-forward svc/animal-adoption-app-web 8080:8080 --namespace animal-adoption

Then access the app via http://localhost:8080

**Debugging Tips**
To switch to the namespace

	kubectl config set-context --current --namespace=animal-adoption

To get the service

	kubectl get svc

To get all the pods in the namespace

	kubectl get pods

To get the application console log

	kubectl logs pod/animal-adoption-app-web-xxx

To get all the events

	kubectl get events


## To clean up

Remove the Helm chart:

    helm uninstall animal-adoption-app --namespace animal-adoption

Delete all resources created by the Helm chart including the  namespace itself

    	kubectl delete namespace animal-adoption

# Run Test cases
**To run test cases, execute the following command where the *pom.xml* is stored:**

    mvn clean verify

# Database Debug Tips
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