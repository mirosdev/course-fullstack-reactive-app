# course-fullstack-reactive-app
Angular 7 SPA + (webflux) Spring Boot 2 + (for external, on default port: 27017) MongoDB Reactive NoSQL DB - Course Practice Project

# Run Requirements
- Node.js
- Angular CLI
- Maven

# Database
MongoDB is used for reactive implementation approach. On every spring-boot run database will be emptied and initialized with same new dummy data. MongoDB should be active on default port: 27017 so spring-boot may connect.

# Run Commands
After required installations, locate your command line prompt into SPA directory,
- run 'npm install' to install Angular dependencies,
- then run 'ng serve' to run Angular SPA on port localhost:4200,
- and for Spring Boot, locate into Spring Rest directory and run 'mvn spring-boot:run'.

# For Application User Experience
Try registration or login with dummy example profile 'bob@test.com', pw: 'password'.

# Features
Auto login with non-expired token, Reactive Spring Boot API, Lazily & Eagerly Loaded Modules (SPA), Upload & Serve Images from Spring-Boot directory.
