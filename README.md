# Implementation of an algorithm for community detection: Optimizing Graph Modularity for Suboptimal Clustering

We want to implement an algorithm that allows **identifying the communities in a graph**. The communities are groups of nodes in the graph that are densely connected to each other, while they are poorly connected to nodes in other groups/communities. 

![exemple_comunitats](https://upload.wikimedia.org/wikipedia/commons/f/f4/Network_Community_Structure.svg)

More information on what is the structure of communities can be found on the Wikipedia page: https://en.wikipedia.org/wiki/Community_structure

This program implements an algorithm that returns the best possible subdivision into communities, given a network loaded from a file. To do this, the following considerations are taken into account:

* The implementation will be programmed for graphs with the format: Pajek NET (https://gephi.org/users/supported-graph-formats/pajek-net-format/). .net files corresponding to the graph that wants to be studied must be placed outside any folder.

* The problem will only be considered for NON-directed and NON-labelled graphs.
  
* Each vertex will have an identifier that will serve as a key, and the value that is stored in the vertex will be the community to which this vertex belongs (which can be an integer in the range 0 to N, where N is the number of nodes).

* To measure the quality of a subdivision into communities, a metric known as Modularity (https://en.wikipedia.org/wiki/Modularity_(networks)) is used. This metric measures the density of links between the nodes of the community and the nodes of other communities. The higher the value of modularity, the better the subdivision into communities.

* This is an NP-hard optimisation problem, so it will not be possible to explore all possible options, and in most cases, a sub-optimal result will be obtained.

* At the end of the execution, the maximum value of modularity obtained will be returned, and the classification of nodes in groups in some output file (in the Pajek format, the output file is of type .clu: for instance, if the input is the network zachary.net, an output file called zachary_0.41880.clu will be returned, in which the partition in communities of the vertices is stored).

Finally, a series of graph examples are also included along with examples of output .clu files, resulting from the execution of the main program.

> [!NOTE]
> To change the graph, one must go to the Main.java file, change the filePath variable and include the graph file (in .net format) in the main directory.

# Resolution: 

The Louvain Method is used, a very efficient method for finding good partitions in large network communities. It consists of two phases, which are implemented in the GrafPajek.java file.

## Phase 1 (Local Community Variation):

* Once the graph is saved, a random vertex is taken. The neighbouring communities of this vertex are identified, and an attempt is made to move this vertex to each of these communities. Finally, the vertex is moved to the community that produces the greatest increase in modularity. This process is repeated iteratively for a sufficiently large number of iterations (ensuring that all vertices of the graph are covered). A maximum number of iterations is also set, and this first phase is stopped if the modularity does not improve after this number of iterations (at which point it is said that a local maximum of modularity has been reached).

## Phase 2 (Global Community Variation):

* Now, instead of changing individual vertices within communities, entire communities are varied. That is, for each community, an attempt is made to merge it with each of the remaining communities. The merger is carried out between the communities that maximize modularity, and if no merger maximizes modularity for a given community, the process moves on to the next community. This process is also repeated for a certain number of iterations; however, in this case, many iterations are not necessary since, in the implementation of this phase, all communities in the graph are considered (which was not the case in Phase 1, where the community of a randomly chosen vertex was varied).

Some example executions have been saved at the beginning of the file Main.java to provide an approximate idea of the computational cost of the algorithm with each of the attached graphs.

The returned file contains a list of the communities for each vertex in the graph, where the first number corresponds to the community of the first vertex, the second number to the community of the second vertex, and so on.
