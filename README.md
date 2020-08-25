# CargoDepot
CargoDepot is a small educational application for managing cargos and the customers who own them.

Diagram in project root has following name: 'sm_architecture_diagram.pdf'

### How to run from commandline
Arguments can be specified in the following exact order:
1. digit > 0 (will be ignored if not a digit)
2. path to the optional log file (file will be created if not existent)
3. log language (will be ignored if path is invalid or unspecified)
    * en = english [default]
    * bln = berlinerisch
    * any unknown input will result in [default]

## CLI syntax
The application should be closed with: `exit`

#### Change Modes:
* `:c` change to insert mode
* `:d` change to deletion mode
* `:r` change to view mode
* `:u` change to modification mode
* `:p` change to persitence mode
* `:config` change to configuration mode

* **Insert Mode**:
    * `[customer name]` adds customer
    * ```[cargo type] [customer name] [value] [duration of storage in seconds] [comma seperated hazards, single comma for no hazards] [[fragile(y/n)] [pressurized (y/n)] [solid(y/n)]]``` inserts cargo
   
   _Examples_:
     
        o `UnitisedCargo Beispielkunde 2000 86400 , n`
        o `MixedCargoLiquidBulkAndUnitised Beispielkunde 4000.50 86400 radioactive n y`
* **View Mode**:
    * `customer` Displays the customers with the amount of stored cargos 
    * ```cargo [[cargo type]]``` Displays the stored cargos, eventually filtered by type, with storage position, date of storage and the last inspection date
    * `hazard [contained(i)/not contained(e)]` Displays the contained or rather not contained hazards
* **Deletion Mode:**
    * `[customer name]` Deletes the customer
    * `[storage position]` Deletes the cargo at the specified position
* **Modification Mode**:
    * `[storage position]` sets the inspection date to the actual time 
* **Persistence Mode**:
    * `saveJOS` persists by JOS
    * `loadJOS` loads via JOS
    * `saveJBP` persists via JBP
    * `loadJBP` loads via JBP
    * `save [storage position]` 
    * `load [storage position]` Loads a single instance from file 
* **Configuration Mode**:
    * `add [class name]` Registers a specified listener
    * `remove [class name]` Deregisters a specified listener
    * Following listener can be added or removed:
        * `HazardChangeListener`
        * `CriticalCapacityListener`
        * `AddCargoEventListener`
