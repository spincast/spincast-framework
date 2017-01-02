# Spincast - Sum demo

## How to run

- Enter the root directory using a terminal.


- Compile the application using Maven :  

  `mvn clean package`  
  
  *(notice that the tests are running and are passing!)*
  

- Launch the application :  

  `java -jar target/spincast-demos-sum-${project.version}.jar`

The application is then accessible at [http://localhost:44419](http://localhost:44419).

Try to POST on the `[http://localhost:44419/sum](http://localhost:44419/sum)` route.
