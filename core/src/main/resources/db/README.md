## DB migration scripts for OpenHub

* SQL schema should be handled by Flyway, which does apply all the scripts in the migration folder
with respect to the selected db engine.
* All scripts should be immutable, once added & applied, then they should not be changes. There is naming convention in place,
scripts should start with VX_X__ version, as it is default for Flyway.
* Interesting configuration properties:
    ```
    flyway.baseline-on-migrate = (true/false)
    
    - whether to automatically call baseline when migrate is executed against a non-empty schema with no metadata table. This schema will then 
    be baselined with the baselineVersion before executing the migrations. Only migrations above baselineVersion will then be applied.
    ```

* See flyway documentation (https://flywaydb.org/documentation/) for more information.
