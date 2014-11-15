CleverBus integration framework - Maven archetype for admin web GUI
====================================================================

Maven archetype for fast and easy creating customized project based upon CleverBus integration framework.

Steps to create maven archetype:
1) in module web-admin execute "mvn archetype:create-from-project" to create basic structure of archetype
(see also http://maven.apache.org/archetype/maven-archetype-plugin/create-from-project-mojo.html)
2) copy from sc-web-admin/target/generated-sources/archetype to under this module
3) delete unnecessary files (e.g. *.iml and so on)
4) modify /META-INF/maven/archetype-metadata.xml (it is possible to add new variables)
5) IMPORTANT:
   It is very necessary to modify all ${groupId} placeholder in classes, Spring configurations and web.xml (no pom.xml)
   and replace it by org.cleverbus. It is necessary for reasons that Maven archetype execution replaces all
   occurrences of org.cleverbus by placeholder ${groupId}. For classes from CleverBus Core it is incorrect.

   Exclude files: README.txt, pom.xml
   Include files: all other

   Example:
         before: import ${groupId}.core.entity.EntityTypeExtEnum;
         after: import org.cleverbus.core.entity.EntityTypeExtEnum;

6) modify /archetype-resources/pom.xml file:
   - add new parameters if they are needed (http://maven.apache.org/archetype/archetype-models/archetype-descriptor/archetype-descriptor.html)

7) build customized project from archetype it is possible by command:

   Note: one of best practices is that name package ends on ".admin", because archetype contains only admin console, all other is as dependency.

   mvn archetype:generate
        -DarchetypeCatalog=local
        -DarchetypeGroupId=org.cleverbus
        -DarchetypeArtifactId=cleverbus-web-admin-archetype
        -DarchetypeVersion=1.1
        -DcleverBusVersion=1.1
        -DcleverBusServerName=ESB
        -DcleverBusServerDescription="Enterprise Service Bus"
