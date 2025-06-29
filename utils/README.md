# Module Utils

Various utilities used by other DFXTools modules.

## Logging

A utility object for logging, currently only has a function for logging and throwing an error with `KLogger`.

## Immutable Collection views

Simple delegated classes that allow to pass an immutable view to a mutable collection. Currently consists of
`ImmutableListView`, `ImmutableMapView` and `ImmutableSetView`.

## ConditionalReadWriteProperty

`ConditionalReadWriteProperty` is a property that may only be set if the value passes a check. Can be configured to
log a warning message, throw an exception or silently ignore an invalid value when one is attempted to be set.