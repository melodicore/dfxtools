# Module Handles

An identification and categorising system.

## Handle

`Handle` is the core class of this module. A handle belongs to a `Space`, contains a string id, an integer index and an
integer subindex. A handle may also contain subhandles and tags, which are also handles. Handle has an internal 
constructor, to create a handle you must use `Space.createHandle()` or one of the other functions for this purpose. A 
handle's id must not contain at symbols (`@`) or colons (`:`), except for subhandles which use a single colon to 
separate its id from the parent handle's id in format `handle:subhandle`.

### Subhandle

Subhandles are handles that belong to a parent handle. To create subhandles, use `Handle.createSubhandle()` or one of 
the other functions for this purpose. A subhandle may not contain subhandles of its own, and attempting to create one
throws an exception.

### Tag

Tags are handles that may be used to categorize and identify other handles. All tags belong to a special tag space 
(`HandleManager.tagSpace`). Handles may be queried by using tags with functions like `Map<Handle, V>.getByTag()` and 
`Set<Handle>.getByTags()`. Tags may be added to handles (including tags themselves) directly through `Handle.tags` or by
one of the operator functions provided for convenience.

## Space

`Space` is a namespace for handles. A space is also identified by a handle, and all handles belong to a special space
(`HandleManager.spaceSpace`). Subhandles of space handles are used for identifying groups. Space has an internal 
constructor, to create a space you must use `HandleManager.createSpace()` or one of the other functions for this 
purpose.

## Group

`Group` is a container for handles specific to a `Space`. Groups can only be created with `Space.createGroup()` or one
of the other functions for this purpose. Handles may be added to groups directly through `Group.handles` or by one of
the operator functions provided for convenience.

## HandleManager

`HandleManager` is a singleton object that contains all spaces.

## HandleSet

`HandleSet` is a sorted mutable set that may only contain handles from one space. Handles may be added to it by string 
id with `HandleSet.add()`, which automatically creates handles (including subhandles) if they do not already exist. Many
other functions for working with string ids are also provided as extension functions of `Set<Handle>` and 
`MutableSet<Handle>`. These also include functions for querying handles using tags.

## HandleMap

`HandleMap` is a sorted mutable map that may only contain handle keys from one space. Many functions for working with
`Handled` values and string ids are also provided as extension functions of `Map<Handle, V>` and 
`MutableMap<Handle, V>`. These also include functions for querying values using tags.