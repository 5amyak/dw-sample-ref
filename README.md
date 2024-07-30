# DwSampleRef

Organizing Your Project
=======================
* ``com.example.myapplication``:
    * ``api``: Request and response bodies.
    * ``cli``: DW commands
    * ``client``: Client code that accesses external HTTP services.
    * ``core``: Domain implementation; service layer resides here
    * ``db``: `Database access classes
    * ``health``: Health Checks
    * ``resources``: API Resources
    * ``MyApplication``: The application class
    * ``MyApplicationConfiguration``: The configuration class

How to start the DwSampleRef application
---

1. Run `mvn clean install` to build your application
1. Start application with `java -jar target/dw-sample-ref-1.0-SNAPSHOT.jar server config.yml`
1. To check that your application is running enter url `http://localhost:8080`

Health Check
---

To see your applications health enter url `http://localhost:8081/healthcheck`
