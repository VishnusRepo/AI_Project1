readme.txt
-------------------------------------------------------
Contents:
1. How to run files to obtain outputs?
2. Structure of code

-------------------------------------------------------
1. Quick description
Please compile the file before running it
Command: javac Main.java
Q4 3.a The shortest path

To obtain shortest path run the following command:
If using DFS: java Main "DFS" "<mazefilepath>"

If using BFS: java Main "BFS" "<mazefilepath>"

If using BestFirst: java Main "BestFirst" "<mazefilepath>"

If using Astar: java Main "Astar" "<mazefilepath>"

-----------
Q4 3.b The number of states expanded by each algorithm when finding the path

Please note that when code is executed for Q4 3.a (above), 
number of states expanded for each algorithm is displayed

-----------
Q4 3.c To obtain number of unqiue paths, run below command:
Command: java Main "unique" "<filename>"
-----------
Q4 3.d To obtain number of states expanded in finding unique paths:
Please note that when code is executed for above Q4 3.c, number of states expanded is displayed
Command: java Main "unique" "<filename>"

-------------------------------------------------------
2. Structure of code: <Also present as comments in the code>
a. The Node class:
This class is created to represent each cell in the maze. For each cell,
one object of Node class is created.

b. NodeComparator:
This class is serves as a comparator for comparing two nodes/cells based on their heuristic

c. maze[][]
This two dimensional array stores objects of Node class, in the same
sequence as input maze.

d. main function:
	1. Read inputfile.
	2. Use function getMaze() that creates maze[][] (described above) data structure from input maze.
	3. Based on inputs, it calls BFS/DFS/BestFirst/AStar/UniquePaths

e. DFS():
	1. Create stack data structure that stores objects of Node class.
	2. A reference to source node(0,0) is created as start.
	3. The path values, cost values are initialized.
	4. Loop until stack goes empty:
		a. Pop one element from stack, call it current.
		b. As we popped, increase the state expansion counter (stateExpansionCounter++).
		c. Check if popped element is destination. If so, exit.
		d. If popped element in point b is not destination, Obtain the next cell to explore further. 
		Done using getOption() function.
		This function checks for reachable nodes from current node and returns one of them.
		As recommended, the order of preference is top,bottom,left,right.
		Also check if the obtained node from getOption function is already in stack.
		If already in stack, ignore. If not, add this to stack and continue in loop.
	Note: If the question is to find unique paths, we don't exit at point c. We continue
	till stack goes empty.
	
f. BFS():
	1. Create Queue data structure that stores objects of Node class.
	2. A reference to source node(0,0) is created as start.
	3. The path values, cost values are initialized.
	4. Loop until queue goes empty:
		a. Pop one element from queue, call it current.
		b. As we popped, increase the state expansion counter (stateExpansionCounter++).
		c. Check if popped element is destination. If so, exit.
		d. If popped element in point b is not destination, Obtain all the next cells to explore further. 
		All the reachable nodes from current are identified and added to queue. 
		As recommended, the order of queue addition is top,bottom,left,right.
		So, add the next node to queue and continue in loop.

g. BestFirst():
	1. Create Priority Queue data structure that stores objects of Node class and compares using 
	NodeComparator class which is explained above.
	2. A reference to source node(0,0) is created as start.
	3. The path values, cost values are initialized.
	4. Loop until priority queue goes empty:
		a. Pop one element from priority queue, call it current.
		b. As we popped, increase the state expansion counter (stateExpansionCounter++).
		c. Check if popped element is destination. If so, exit.
		d. If popped element in point b is not destination, Obtain the next cell to explore further. 
		The left,right,top,down nodes are obtained and heuristic=estimate for them is evaluated.
		Above estimate is calculated using heuristic(Node) function.
		All these nodes are pushed to priority queue.
		So, add all these nodes to priority queue and continue in loop.

h. AStar():
	1. Create Priority Queue data structure that stores objects of Node class and compares using 
	NodeComparator class which is explained above.
	2. A reference to source node(0,0) is created as start.
	3. The path values, cost values are initialized.
	4. Loop until priority queue goes empty:
		a. Pop one element from priority queue, call it current.
		b. As we popped, increase the state expansion counter (stateExpansionCounter++).
		c. Check if popped element is destination. If so, exit.
		d. If popped element in point b is not destination, Obtain the next cell to explore further. 
		The left,right,top,down nodes are obtained and heuristic=current_cost+estimate for them is evaluated.
		Above estimate is calculated using heuristic(Node) function.
		All these nodes are pushed to priority queue.
		So, add all these nodes to priority queue and continue in loop.
		
I. getLeftOption(current), getRightOption(current), getTopOption(current), getBottomOption(current):
	These functions return the left, right, top, bottom nodes to current node, if there is any.
	If not, they return null. These functions serve as utility functions for search functions.

J. Heuristic(node):
	1. If the node is destination, return 0.
	2. If the node is in reachable range of destination, return 1.
	3. If the node is not in reachable range of destination, return 2.
	A node B is reachable from A, if we are able to traverse from A to B in single step.
	This traversal can be horizontal or vertical or both(Relaxation).

K. getOption(Node):
	Return one of the neighbouring nodes of passed argument 'node' that can be reached in one step.
        Top,Bottom,Left,Right. This is the order of preference, as recommended by professor.
        If there's no top node, check bottom. If there's no bottom also, go for left.
        If there's no left as well, check right. If there's no right as well, return null.