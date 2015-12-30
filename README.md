envers-schema
=============
[![Build Status](https://travis-ci.org/anhahne/envers-schema.svg?branch=master)](https://travis-ci.org/anhahne/envers-schema)

Analyses the JPA entities in a specified package and generates DDL statements for schema generation. Besides the Entity
annotation, this plugin processes the Hibernate Envers annotation Audited and generates AUD tables accordingly.

Motivation
----------
Based on JPA entities, database schemas need to be created frequently. Using Hibernate 4 in connection with enversthere is
no built-in mechanism to generate both the data tables and the revision tables using a maven plugin.
To overcome this gap, this plugin makes use of the EnversSchemaGenerator to create an overall DDL file.

Usage
-----
Besides the plugin, this repository contains a sample project which generates the schema for a single JPA entity. Please
refer to the POM of this sample. The plugin should be applied in the package phase of the maven build lifecycle as it
makes use of the compiled classes.
Basically, the plugin provides three properties:
 - dialect (required): the fully-qualified name of the hibernate class representing the SQL dialect (e.g.
   org.hibernate.dialect.MySQL5Dialect for the MySQL dialect)
 - packageName (required): the package in which the JPA entities are defined
 - destination (optional): the file name to which the schema will be generated, default value is target/schema.sql
 
Version History
---------------
1.0.0    2014-02-08    First release with all features described above.
1.0.2    2015-12-30    Upgraded version of Plexus Plugin
