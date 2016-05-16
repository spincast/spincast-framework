==========================================
Spincast Development
==========================================

- Contribution guide is there : https://www.spincast.org/community#contribution

- When you import the framework projects in your IDE, do NOT import the 
  "spincast-shaded-dependencies" or you are going to have a lot of
  "The import org.spincast.shaded cannot be resolved" errors.
  This package is generated when compiling "spincast-shaded-dependencies"
  and when you add the project to your IDE, the IDE tries to resolve the
  imports from open projects. In Eclipse, this is true when 
  "Resolve dependencies from Workspace projects" is selected on a project
  ("Properties" / Maven / "Resolve dependencies from Workspace projects").
  
  So install everything using command line ("mvn clean install") then open
  all projects except "spincast-shaded-dependencies" in your IDE.
  
- Make sure that you have Maven 3.3.1+ installed and that you have the "M2_HOME" 
  environment variable defined. 
  
  Maven 3.3.1+ is required because of https://issues.apache.org/jira/browse/MNG-5768
  We use this feature to compile spincast-website, while calling a
  Maven goal programmatically, in GenerateAggregatedJavadoc script. This script uses 
  the "M2_HOME" environment variable to find Maven's home.