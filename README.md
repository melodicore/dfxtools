# DFXTools

DFXTools is a collection of game dev -oriented general purpose libraries. It is the Kotlin rewrite of most parts of 
[DFXEngine](https://git.datafox.me/datafox/dfxengine).

## Modules

This project is still under active development and everything (including things marked as Done) may change without 
warning!

| Module                         | Description                                           | Status             |
|--------------------------------|-------------------------------------------------------|--------------------|
| [Configuration](configuration) | Cascading configuration with arbitrarily typed values | Done               |
| [Handles](handles)             | Categorizable identification                          | Done               |
| [Invalidation](invalidation)   | Invalidation of values that depend on other values    | Done               |
| [Entities](entities)           | Serializable entity-component system                  | Under construction |
| [Text](text)                   | Number formatting and text handling                   | Under construction |
| [Utils](utils)                 | Utilities used by other modules                       | Under construction |
| [Values](values)               | Mutable and dynamically modifiable numbers            | Under construction |

## Differences to DFXEngine

The most notable difference is the disappearance of the API modules, the Injector module and the Math module. The API 
modules were removed since they would've only been useful if someone else wanted to make their own implementation of a 
module. The Injector module was very sloppy and was missing features like Java's union types. The auto-promoting system
of the Math module was cool, but did not have enough advantages to justify its complexity.

Modules that depended on the Injector module are now rebuilt with Kotlin in mind, making use of singleton objects. The 
Values module now always uses `BigDecimal` numbers.