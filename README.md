# pgDilatoHibernateHelper
Small JavaFX utility to help generate Hibernate classes to use with the pgDilato Class library.


This is a JavaFX application that will connect to a postgres database and generate Hibernate user types for postgres UDT/Types.

Hibernate and JDBC does not support non-relational datatypes such as int[](array of integers), json and types out of the box. 
To implement this a developer has to extend and implement hibernate user types etc. While not difficult to do it requires 
a developer to write a lot more boiler plate code.This utility uses the pgDilato library and the pgjdbc-ng drivers to speed up 
this process. 

The application can generate the following types of Hibernate classes for you.
1. All UDT/Types 
2  UDT array types

For example to use a postgres structure such as:

CREATE TYPE test_person AS
(
    "firstName" character varying(96),
    "LastName" character varying(96)
);

You will need to manually create the class and use it in hibernate. This utility will create the classes. 
