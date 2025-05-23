# Module Configuration

A simple cascading configuration system that allows arbitrarily typed lambdas as values.

Every configuration contains configuration keys and values. `ConfigurationKey` is a typed class where the type 
represents the configuration value associated with it, and contains a default value of the type. Configuration values 
are lambdas and must be of the same type as the key.

A configuration may be appended on top of another configuration, replacing any defined values. To take advantage of this
further, a singleton `ConfigurationManager` object exists. Configuration values may be added to and removed from this
object just like you would with normal configuration instances, but when you get its backing configuration, it returns a 
copy. Other more specific configurations can then be appended on top of this copy, leaving the root configuration 
unchanged.