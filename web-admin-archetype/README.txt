CleverBus integration framework - Maven archetype for admin web GUI
====================================================================

!!!!!!!
    Warning: this module with Maven archetype is not updated from version 0.2!

    Reason is that update of this module is quite time-consuming and because this module is not often
    used by CleverBus users then we would like to move our focus to more important tasks.
    We would like to return back to this module in the future.
!!!!!!!

Maven archetype for fast and easy creating admin web GUI server.

Postup prvotniho vytvoreni Maven archetypu:
1) v modulu sc-web-admin zavolat "mvn archetype:create-from-project" pro vytvoreni zakladni struktury archetypu
(viz http://maven.apache.org/archetype/maven-archetype-plugin/create-from-project-mojo.html)
2) prekopirovat z sc-web-admin/target/generated-sources/archetype pod tento modul
3) vymazat nadbytecne soubory (napr. *.iml)
4) upravit /META-INF/maven/archetype-metadata.xml (pridat pripadne promenne)
5) upravit cesty ke tridam - Maven pri vytvareni archetypu funguje hodne jednoduse a pokud najde retezec zakladniho
    baliku "org.cleverbus", tak ho vzdy nahradi ${package} resp. ${packageInPathFormat}, případně ${groupId},
    ktery bude pak pri vytvareni noveho projektu nahrazen novou cestou.
    Jenze toto je spatne pro tridy, na ktere se odkazuje ze zavislych modulu, napr. sc-core
    => projet vsechy tridy, Spring konfigurace a web.xml a spravne upravit cesty ke tridam

    Priklad:
      puvodne: import ${package}.core.entity.EntityTypeExtEnum;
      nove: import com.cleverance.cleverbus.core.entity.EntityTypeExtEnum;

6) upravit /archetype-resources/pom.xml
    - pridat nove parametry pokud jsou potreba (http://maven.apache.org/archetype/archetype-models/archetype-descriptor/archetype-descriptor.html)
	
7) samotny projekt lze pak vytvorit napriklad pres prikaz:

	mvn archetype:generate -DarchetypeCatalog=local -DcleverBusVersion=0.4-SNAPSHOT -DcleverBusServerName=LeanHCS -DcleverBusServerDescription="Lean Handset Capability Server"


