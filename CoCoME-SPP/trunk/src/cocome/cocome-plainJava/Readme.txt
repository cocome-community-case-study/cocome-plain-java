This text file gives a short introduction on how to use the CoCoME reference
implementation.

You can either use Eclipse to start the Trading System or just use the
console. If you use the console, please set fork=true in the
deployment.properties, otherwise set fork=false.

The implementation comes as an Eclipse project. So if you use Eclipse, just
import this project and execute the targets as described below.


------------------------------------------------------------------------------
PREREQUISITES:
------------------------------------------------------------------------------

* Java Development Kit, version 1.5 or later
* Apache Ant, version 1.8.2 or later

* Eclipse (optional, but recommended)
* AMPL/CPLEX solver (optional, see svc/solver/README)

------------------------------------------------------------------------------
COMPILATION:
------------------------------------------------------------------------------

The following Ant targets can be used for compilation:

  * compile

    Compiles the Java source files into "bin" directory.

  * javadoc

    Generates javadoc documentation for the project.


------------------------------------------------------------------------------
DEPLOYMENT:
------------------------------------------------------------------------------

When executing the deployment targets from within Eclipse, the fork parameter
in the deployment.properties file can be set to false (which allows a smaller
number of virtual machiens to be launched). The following Ant targets can be
used for deployment:

INFRASTRUCTURE

  * startInfrastructure

    Starts the RMI registry, database server, and message broker.

  * stopInfrastructure

    Stops the database server and message broker.

    Note: In order to properly close the database manager / broker, it is
    important to use this target, and not kill the startInfrastructure
    process. To kill the RMI process, you need to kill the startInfrastructure
    process afterwards.


  * fillDB

    Fills the database with dummy data.

    We use automatic schema generation from our annotated persistent objects.
    This target must be executed once.

  * deleteDB

    Deletes the directory the database entries are saved in. This is used to
    start from scratch. The database server must be shut down before executing
    this target.

  * deleteMQ

    Deletes the directory containing message broker persistent data. This is
    used to start from scratch. The message broker must be shut down before
    executing this target.


INVENTORY

The components of the inventory system to start are configured in the
tradingsystem.properties file.

  * startInventory

    Starts the inventory system components.

  * startInventoryConsoles

    Starts the inventory system graphical consoles.


CASH DESK LINES

The components to start are configured in the tradingsystem.properties file.
It is important to keep JNDI configuration consistent with the contents of the
tradingsystem.properties file. For this purpose, the jndi.properties file
needed in the classpath is automatically generated from the contents of the
tradingsystem.properties file.

  * startCashDeskLines

    Starts the cash desk line components for all stores defined in
    tradingsystem.properties.


------------------------------------------------------------------------------
PROCESSING USE CASES:
------------------------------------------------------------------------------

In order to simulate the Use Cases, just execute the following targets in the
following order:

  1. deleteDB
  2. startInfrastructure
  3. fillDB
  4. startInventory
  5. startCashDeskLines
  6. startInventoryConsoles (not relevant for all Use Cases)

To stop the Trading System, execute the target: stopInfrastructure


In order to simulate the Use Cases, you need some standard values like:

  Valid Product Barcodes are among others: 900, 1000
  (Used during UC1:Process Sale)

  Valid Credit Card PIN: 7777
  (Used during UC1:Process Sale for card payment)


------------------------------------------------------------------------------
ISSUES:
------------------------------------------------------------------------------

* The INSTALL PATH of the system should not contain whitespaces.


------------------------------------------------------------------------------
Last update: 25.05.2011 by Lubomir Bulej