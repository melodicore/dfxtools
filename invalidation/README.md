# Module Invalidation

System for invalidating values that depend on other values.

## Observable

`Observable` is an interface for classes that may invalidate `Observer` classes. It contains a `CyclicAwareSet` called 
`observers` and a function `onChanged()` which calls `invalidate()` on all observers in the set. `onChanged()` should be
called whenever a value in the observable changes in a way that could affect any of the observers. `InvalidatorProperty` 
may be used as a property delegate to do these calls automatically. `AbstractObservable` is an abstract implementation 
of this interface that populates `observers`.

## Observer

`Observer` is an interface for classes that may be invalidated by an `Observable` class. It contains a `propertyHandler`
property for handling invalidation of `InvalidatedProperty` properties and two functions, `invalidate()` and 
`onInvalidate()`. `invalidate()` is the one that is called by an observable, but it shouldn't be overridden, as when 
called it first invalidates all invalidated properties through the property handler, and then `onInvalidate()`, which is
an abstract function that should be overridden for custom invalidation logic. `AbstractObserver` is an abstract 
implementation of this interface that populates `propertyHandler`.

When writing custom invalidation logic, please keep in mind that `invalidate()` (and thus, `onInvalidate`) may be called
an arbitrary number of times. Due to this, it is recommended to only set an invalidated flag in the function and lazily
recalculate dependent values if the flag is set. This is also how `InvalidatedProperty` works internally.

## ObservableObserver

`ObservableObserver` is an interface that extends both `Observable` and `Observer`. `CyclicAwareSet` can only detect
cyclic dependencies when all the elements in the cycle implement both interfaces, so this is the interface that should
be implemented whenever a class is both observable and an observer. `AbstractObservableObserver` is an abstract 
implementation of this interface that populates both `observers` and `propertyHandler`.

## Observable Properties

`ObservableProperty`, `ObservableListProperty`, `ObservableSetProperty` and `ObservableSortedSetProperty` are property
delegates for `Observer` that wrap or contain `Observable` classes. All of them add their observer owner to their 
contained observables' observers set. Please note that the collection properties do not remove their owner from the set
when an observable is removed from them, as this could cause issues when the same observer is added to an observable 
from multiple of these properties. The collection properties are backed by `ObservableList` and `ObservableSet`.