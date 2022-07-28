# Reservation service


This is project that uses a Spring Boot backend with Java 11, Maven, a Postgres database, and a frontend that uses React and npm among other Javascript libraries. 


## How to run Backend


On the shared-objects directory (the parent directory) you should run to compile the Java project and all its modules

```
mvn clean install
```

Next you need to have installed a postgres database or have Docker installed on your machine

I used docker, so I ran this command

```
cd reservation-service

docker-compose -f docker/docker-compose.yml up

```

Finally you can run 

```
mvn spring-boot:run
```


Then you can open the following URL to look at the Swagger configuration

http://localhost:8080/swagger-ui/index.html


## How to run the frontend

```
cd frontend

npm install

npm start

```


#ML small proposal

Maybe a good way to use ML on this project is create a way to analyze common times that a user based on several things like personal history of events (avoiding missing reservation), or maybe cluster users from a certain location (country city) and then propose back to the user the suggested times to reserve a meeting. 