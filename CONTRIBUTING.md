==========================================
Spincast Development
==========================================

- Contributions are appreciated, thank you! 

- Please read the contribution guide: https://www.spincast.org/community#contribution

- Make sure that you have Maven 3.3.1+ installed and that you have the 
  associated "M2_HOME" environment variable defined. 
  
  Maven 3.3.1+ is required because of https://issues.apache.org/jira/browse/MNG-5768 .
  We use this feature to compile "spincast-website", by calling a
  Maven goal programmatically, in class "SpincastMavenPreparePackageRelease". 
  This code uses the "M2_HOME" environment variable to find Maven's home.
  
- If you need to import the "spincast-shaded-dependencies" project in your IDE 
  in addition to the framework's projects, please note that you may get errors.
  If the "spincast-shaded-dependencies" version is the one used by the other 
  framework's projects, IDEs will try to use the sources from it, 
  but they won't find any since the source is dynamically generated when the project 
  is built. This is the main reason why we decided to extract "spincast-shaded-dependencies" 
  to a standalone project which does not follow the main Spincast release cycle.
  
  
  

  