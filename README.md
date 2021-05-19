## CRM lead validator:
Elaborated by Ivan David Galvis Sandoval

GitHub of this repo: `https://github.com/davidgalvis95/crm-validator`

### **Terms and Assumptions to take into account:**

* A lead will be validated based on its id only, which is an integer number in this case.
* A lead in this case will only be composed of id, first name, last name, birthdate and email, but can be extended yo more fields if needed.
* Some lead id, could have changing scores throughout the time, so different ones. Moreover, since here this project is based in mocked external services, while the external one is a real spring microservice, it is not a service with a real DB, so it cannot store real data and will sometimes return something different for the same lead id. The only information that should not change is the lead personal data, due that this service has a mongo db storing that data in the beginning of the validation process. 
* A `Sample Lead` is an id that will be validated using mocked services, in this case the national and judicial service will be replaced by `MockServer` 
* If you select a `Not Sample Lead`, please be sure that you have run this command before: `docker-compose up -d external_service` because internally, if the lead is not a sample, and the service will try to reach to the real national and judicial services through feign clients, which will fail because of that.

### **Architecture**

The application is basically composed of 3 independent components, following the hexagonal architecture pattern, the application component, the domain one and the infrastructure one, as it can be seen in the structure of the project and its dependencies (Mongo DB, external services, configuration, UI client, CLI controllers).
The architecture of the application is shown in the next image

![src/main/resources/templates/crm-service-architecture.jpg](src/main/resources/templates/crm-service-architecture.jpg)

* The application layer = purple.
* The domain layer = green.
* The infrastructure components = yellow.

### **How to run the application and its dependencies:**

1) Stand over the root directory of the project `/cli-crm-validator-master`
2) If you just want to run all the integration(with `docker`) of the services including the custom UI `React.js` client that has been created for this project(`https://github.com/davidgalvis95/crm-lead-validator-UI-client`), you must be at the root folder of this project and execute the following command: `docker-compose up`. The client runs on the `http://localhost:3000/` url.
3) Run the following command in your terminal `docker-compose up -d mongodb_container` to start the Mongo DB, which is needed for this project.
4) **Optional:** If you want to use the real National Service and the real Judicial Service then run `docker-compose up -d external_service`, this is another `java/spring` service, which can be found here: `https://github.com/davidgalvis95/crm-lead-validator-external-service` 
5) Once the steps 1 and 2 are completed you can run this service by following the instructions in the step 5 (using `Intellij` and **recommended**). Or if you prefer the command, then `./gradlew bootRun` in the root directory.
6) **Optional:** Also if you have `node` and installed in your machine, you can run the UI client that has been created specifically for this project **without using docker**: To do so, go to the repository and follow the instructions on its `README.md` (it runs in `http://localhost:3000/` and is launched with `npm start`), it is pretty simple to use (**remember to have this API and its dependencies running before running the custom client**). If you do not want to use that option you can reach this service through a `REST` client like `Postman`, or by CLI as described in steps 7 and 8.
7) If your option was to do it using the `CLI` you should go to your IDE, in this case the project was built with `Intellij`, so go there and select the `Run/Debug configurations` and fill up the required fields as follows: ![src/main/resources/templates/idea-config.png](src/main/resources/templates/idea-config.png)
8) If your intention is to use it through the `REST` endpoint directly, then: Run the service using any of the already described ways in the steps 4 and 6. Here is an example of the curl request:

##### Example Request

`   curl --location --request GET 'http://localhost:7000/api/v1/validate/123456789?isSampleLead=true' \
   --header 'Content-Type: application/json' \
   --data-raw ''`

##### Example Response

`{
   "lead": {
      "idNumber": 123456789,
      "birthDate": [ 2024,
                     6,
                     18 ],
      "firstName": "eOMtThyhVNLWUZNRcBaQKxI",
      "lastName": "yedUsFwdkelQbxeTeQOvaScfqIOOmaa",
      "email": "JxkyvRnL@addi.com"
   },
   "score": 69,
   "isAProspect": true,
   "reasonMessage": "The lead complies with the requested criteria"
}`

### **Custom UI Client**

In case you are interested in using the UI client that has been created for this project exclusively, here is an image of how it does look:

![src/main/resources/templates/client-layout.png](src/main/resources/templates/client-layout.png)

Remember that if you want to use this custom UI client, you should have the crm-lead-validator running. Is recommended that if you want to test with the client, you should run it for simplicity with the docker command explained in the step 2 of the 'How to run the application and its dependencies' section.

### **Resources**

##### Docker Images
* `https://hub.docker.com/repository/docker/davidgalvis1995/crm_lead_validator`
* `https://hub.docker.com/repository/docker/davidgalvis1995/externalleadvalidator`
* `https://hub.docker.com/repository/docker/davidgalvis1995/crm-validator-client`

Thank you! :)
