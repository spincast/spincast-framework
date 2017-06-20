# Spincast - Quick Start Application

## Documentation

[https://spincast.org/documentation#quick_start](https://spincast.org/documentation#quick_start)

## How to run

- Enter the root directory using a terminal.


- Compile the application using Maven :  

  `mvn clean package`

- If you want to be able to modify the configurations on a specific environment, copy the
*src/main/resources/app-config.yaml* file to the "target" directory and tweak it.
Otherwise, the version of this file on the classpath will be used! 

- Launch the application :  

  `java -jar target/spincast-quickstart-1.0.0-SNAPSHOT.jar`

The application is then accessible at [http://localhost:44419](http://localhost:44419).