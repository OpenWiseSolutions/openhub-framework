Conventions for DTAG Lean HCS Solution
====================================================================

1. General
- Basic rules for writing source code is taken from the Sun and are described in the Java Code Conventions.
    (http://www.oracle.com/technetwork/java/codeconvtoc-136057.html)
- Basic unit of tab are 4 chars. Tab is converted into backspace.
- Length of line is wrapped fo 120 chars. Maximum length of the java class is 1000 lines.
- Uncoding is UTF-8
- Hierarchy of exceptions is defined in com.cleverlance.dtag.leanhcs.core.common.exception;
    Each exception is subclass of IntegrationException.
- Javadoc and comments are in English

2. Naming convection of objects and patterns
2.1 Naming convection of class and interfaces

General package for all classes is com.cleverlance.dtag.leanhcs

Naming convection of class:
...Route        Camel route
...Exception    Exception
...Test	        Test class
...Tools	    Tools class
...Helper	    Helper class mainly for local use
...Enum	        Enum

2.2. Naming convection of method

set...	    setter
get...	    getter
add...	    add to...
insert...   insert data to persistence layer
update...	update of data
delete...	delete data
create...	create new object without persisting
exists...	check of existence
check...	general check, returns true or false
validate...	validate input
findAll...	find by criteria, result must not be null, only blank collection
findBy...   or findXyzBy...	 find single result by criteria
test...	    method of test

If an input parameter can be null or method can output a null, then this parameter / method must
be marked using the annotation javax.annotation.Nullable.


3. Directory
Structure of packages:
com.cleverlance.dtag.leanhcs.core.common  - common classes
com.cleverlance.dtag.leanhcs.modules
    - in: inbound modules = modules, which publishes WSDL contract
    - out: outbound modules = modules, which calls external systems


4. Design of WSDL contract
- Contract-first approach (http://static.springsource.org/spring-ws/site/reference/html/why-contract-first.html)


5. Definition of route
During creating of routing rules is necessary to:
- extends com.cleverlance.dtag.leanhcs.core.common.AbstractHcsRoute
- defines unique ID of Spring Bean and route ID
- implementation of asnych process is described - https://jira.cleverlance.com/jira/browse/CBSS-48

5.1. Naming convection of route ID
a) synchronous message - route ID: SERVICE_OPERATION_SUFFIX, where
SERVICE is name of service (=module),
OPERATION is name of operation
SUFFIX is constant (AbstractBasicRoute#ROUTE_SUFFIX) for synchronous routes

eg. ServiceEnum.CUSTOMER + "_" + "getCustomer" + ROUTE_SUFFIX

b) asynchronous message (inbound) - route ID: SERVICE_OPERATION_INSUFFIX, where
INSUFFIX is constant (AbstractBasicRoute#IN_ROUTE_SUFFIX) for asynchronous inbound routes

c) asynchronous message (outbound) - route ID: SERVICE_OPERATION_OUTSUFFIX, where
OUTSUFFIX is constant (AbstractBasicRoute#OUT_ROUTE_SUFFIX) for asynchronous outbound routes

This route ID is used for dynamic routing among inbound and outbound part of asynchronous process.

5.2. Naming convection of route bean
a) ROUTE_BEAN = OPERATION_NAME + "RouteBean"

6. Scheduled tasks (route)

Necessary to extends base class for scheduled routes - com.cleverlance.dtag.leanhcs.core.common.AbstractScheduledHcsRoute.
You have to:
    1. override getCronExpression() to get cron expression of trigger, e.g.: 0 0 3 ? * *
    2. override getJobName() to get name of job, e.g.: GetRdmCapabilitiesTask
    3. override getJobGroupName() to get name of job group, e.g.: DeviceCapabilitiesJobs

Best practice is also override isTaskEnabled() method to configure starting state of task.

7. Autowired beans for database layer

Best practice is use to getApplicationContext().getBean(DeviceCapabilityService.class) instead @Autowired in routes,
because routes are by default auto scanned too for test purposes