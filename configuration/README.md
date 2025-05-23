# Module Configuration

A simple cascading configuration system that allows arbitrarily typed lambdas as values.

## Configuration

`Configuration` is the core class of this module. Every configuration contains configuration keys and values. Keys are
of type `ConfigurationKey<T>`, and values are lambdas that return type `T`. A configuration may be appended on top of 
another configuration, replacing any defined values.

## ConfigurationKey

`ConfigurationKey` is a typed class where the type represents the configuration value associated with it. It also 
contains a default value of the type. 

## ConfigurationManager

`ConfigurationManager` is a singleton object. Configuration values may be added to and removed from this object just 
like you would with normal configuration instances, but when you get its backing configuration, it returns a copy. Other 
more specific configurations can then be appended on top of this copy, leaving the root configuration unchanged.