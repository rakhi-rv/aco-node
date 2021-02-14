# Amazing Co Node Service

## Introduction

We in Amazing Co need to model how our company is structured so we can do awesome stuff.
We have a root node (only one) and several child nodes, each one with its own children as well. It's a tree-based structure.

Something like:

```
root
/   \
a    b
|
c
```
We need two HTTP APIs that will serve the two basic operations:
Get all (direct and non-direct) descendant nodes of a given node (the given node can be anyone in the tree structure).
Change the parent node of a given node (the given node can be anyone in the tree structure).They need to answer quickly, even with tons of nodes.
Also, we can't afford to lose this information, so persistence is required.

Each node should have the following info:
- a) node identification
- b) who is the parent node
- c) who is the root node
- d) the height of the node. In the above example, height(root) = 0 and height(a) == 1.

## Installation
This service requires Docker and docker-compose to be installed on your system. To start the service use :
```
docker-compose up
```

## Swagger-UI 
Url to open swagger-ui : http://localhost:8080/swagger-ui.html

## Initial data Setup on application startup
On application loads initial data on start-up to test the functionality. Each value here is the node identifier(id) 
```
  1
/   \
2    4
|
3
```

