import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
class Node {
    /*This class is created to represent each cell in the maze. For each cell,
    one object of Node class is created. The fields here represent various properties of a cell.
    For example if maze[2][3] has value of 5, the properties stored are:
    rowIndex=2
    colIndex=3
    value=5*/
    public int rowIndex;
    public int colIndex;
    public int value;
    public int cost=Integer.MAX_VALUE;
    public int current_cost=Integer.MAX_VALUE;
    public int inStack = 0;
    public int wasQueued = 0;
    public String path="";
    public String current_path="";
    public int heuristic=Integer.MAX_VALUE;
    public ArrayList<String> traversals = new ArrayList<String>();
    Node(int r, int c, int v) {
        rowIndex = r;
        colIndex = c;
        value = v;
    }
}
class NodeComparator implements Comparator<Node>{
    /*This class is serves as a comparator for comparing two nodes/cells based on their heuristic*/
    public int compare(Node n1, Node n2){
        if(n1.heuristic<n2.heuristic) return -1;
        else if(n1.heuristic>n2.heuristic) return 1;
        return 0;
    }
}
public class Main {
    static int rowCount,colCount,stateExpansionCounter=0,destinationRowIndex,destinationColIndex;
    static Node [][]maze;
    static PriorityQueue<Node> pqueue;
    static int getRowCount(String filepath) throws IOException {
        Path p = Paths.get(filepath);
        return (int)Files.lines(p).count();
    }
    static Node[][] getMaze(String filepath) throws IOException {
        /*This function creates a 2 dimensional object containing objects of Node class. This data structure
        can effectively represent the input maze.
         */
        Node [][]maze = new Node[rowCount][];
        BufferedReader br = new BufferedReader(new FileReader(filepath));
        String currentLine;
        StringTokenizer stokenizer;
        int rowIndex=0;
        colCount=0;
        while((currentLine = br.readLine())!=null){
            stokenizer = new StringTokenizer(currentLine,",");
            colCount = stokenizer.countTokens();
            int colIndex=0;
            maze[rowIndex]=new Node[colCount];
            while(stokenizer.hasMoreTokens()){
                String tok = stokenizer.nextToken();
                Node n;
                if(tok.equals("G")){
                    n=new Node(rowIndex,colIndex,0);
                    destinationRowIndex=rowIndex;
                    destinationColIndex=colIndex;
                }
                else n=new Node(rowIndex,colIndex,Integer.parseInt(tok));
                maze[rowIndex][colIndex]=n;
                colIndex++;
            }
            rowIndex++;
        }
        return maze;
    }
    static void printMaze(Node [][]maze){
        for(int i=0;i<rowCount;i++){
            for(int j=0;j<colCount;j++){
                System.out.print(maze[i][j].value+" ");
            }
            System.out.println("");
        }
    }
    static void createFile(String data, String algorithm) throws IOException {
        /*Create output file with path information*/
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("hh-mm-ss");
        String strDate = dateFormat.format(date);
        String fileName=algorithm+"_"+strDate+".txt";
        Path currentRelativePath = Paths.get("");
        String path = currentRelativePath.toAbsolutePath().toString();
        FileOutputStream out = new FileOutputStream(fileName);
        out.write(data.getBytes());
        out.close();
        System.out.println("For output file, please check path: " + path+" with file name: "+fileName);
    }
    static Node getLeftOption(Node node){
        /*getLeftOption(current), getRightOption(current), getTopOption(current), getBottomOption(current):
	These functions return the left, right, top, bottom nodes to current node, if there is any.
	If not, they return null. These functions serve as utility functions for search functions.*/
        if(node.colIndex-node.value>=0) return maze[node.rowIndex][node.colIndex-node.value];
        else return null;
    }
    static Node getRightOption(Node node){
        /*getLeftOption(current), getRightOption(current), getTopOption(current), getBottomOption(current):
	These functions return the left, right, top, bottom nodes to current node, if there is any.
	If not, they return null. These functions serve as utility functions for search functions.*/
        if(node.colIndex+node.value<colCount) return maze[node.rowIndex][node.colIndex+node.value];
        else return null;
    }
    static Node getTopOption(Node node){
        /*getLeftOption(current), getRightOption(current), getTopOption(current), getBottomOption(current):
	These functions return the left, right, top, bottom nodes to current node, if there is any.
	If not, they return null. These functions serve as utility functions for search functions.*/
        if(node.rowIndex-node.value>=0) return (maze[node.rowIndex-node.value][node.colIndex]);
        else return null;
    }
    static Node getBottomOption(Node node){
        /*getLeftOption(current), getRightOption(current), getTopOption(current), getBottomOption(current):
	These functions return the left, right, top, bottom nodes to current node, if there is any.
	If not, they return null. These functions serve as utility functions for search functions.*/
        if(node.rowIndex+node.value<rowCount) return(maze[node.rowIndex+node.value][node.colIndex]);
        else return null;
    }
    static Node getOption(Node node){
        /*Return one of the neighbouring nodes of passed argument 'node' that can be reached in one step.
        Top,Bottom,Left,Right. This is the order of preference, as recommended by professor.
        If there's no top node, check bottom. If there's no bottom also, go for left.
        If there's no left as well, check right. If there's no right as well, return null
         */
        Node topOption = getTopOption(node);
        if(topOption!=null&&topOption.inStack!=1){
            boolean match=false;
            for(String x: topOption.traversals){
                if(x.contains(node.current_path+
                        (topOption.rowIndex)+","+(topOption.colIndex))) match=true;
            }
            if(!match) {
                return topOption;
            }
        }
        Node bottomOption = getBottomOption(node);
        if(bottomOption!=null&&bottomOption.inStack!=1){
            boolean match=false;
            for(String x: bottomOption.traversals){
                if(x.contains(node.current_path+
                        (bottomOption.rowIndex)+","+(bottomOption.colIndex))) match=true;
            }
            if(!match) {
                return bottomOption;
            }
        }
        Node leftOption = getLeftOption(node);
        if(leftOption!=null&&leftOption.inStack!=1){
            boolean match=false;
            for(String x: leftOption.traversals){
                if(x.contains(node.current_path+
                        (leftOption.rowIndex)+","+(leftOption.colIndex))) match=true;
            }
            if(!match) {
                return leftOption;
            }
        }

        Node rightOption = getRightOption(node);
        if(rightOption!=null&&rightOption.inStack!=1){
            boolean match=false;
            for(String x: rightOption.traversals){
                if(x.contains(node.current_path+
                        (rightOption.rowIndex)+","+(rightOption.colIndex))) match=true;
            }
            if(!match) {
                return rightOption;
            }
        }
        return null;
    }
    static void DFS(int unique){
        /*1. Create stack data structure that stores objects of Node class.
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
	till stack goes empty.*/
        stateExpansionCounter=0;
        Node start = maze[0][0];
        Stack<Node> stack = new Stack<Node>();
        start.inStack=1;
        start.path="0,0\n";
        start.current_path="0,0\n";
        start.cost=1;
        start.current_cost=1;
        stack.push(start);
        while(stack.empty()==false){
            Node current = stack.peek();
            stateExpansionCounter++;
            if(current.value==0){
                if(unique==0) break;
            }
            Node pushed = getOption(current);
            if(pushed!=null){
                pushed.current_cost=current.current_cost+1;
                pushed.current_path=current.current_path+pushed.rowIndex+","+pushed.colIndex+"\n";
                if(pushed.current_cost<pushed.cost){
                    pushed.cost=pushed.current_cost;
                    pushed.path=pushed.current_path;
                }
                pushed.traversals.add(pushed.current_path);
                    pushed.inStack = 1;
                    stack.push(pushed);
                //}
            }else{
                Node popped = stack.pop();
                popped.inStack=0;
                maze[popped.rowIndex][popped.colIndex]=popped;
            }
        }
        System.out.println("DFS: Best Route for given commands:\n"+maze[destinationRowIndex][destinationColIndex].path+
                " Cost: "+maze[destinationRowIndex][destinationColIndex].cost);
        if(unique==1) System.out.println("Number of unique paths: "
                +maze[destinationRowIndex][destinationColIndex].traversals.size());
        System.out.println("Number of states expanded: "+stateExpansionCounter);
        //System.out.println("total state expansion counter: "+(stateExpansionCounter));
        try {
            if(unique==1){
                String paths="List of unique paths\n";
                for(String x: maze[destinationRowIndex][destinationColIndex].traversals){
                    paths=paths+"One of the paths: \n"+x+"\n";
                }
                createFile(paths,"DFS");
            }
            else createFile(maze[destinationRowIndex][destinationColIndex].path,"DFS");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    static void BFS(){
        /*1. Create Queue data structure that stores objects of Node class.
	2. A reference to source node(0,0) is created as start.
	3. The path values, cost values are initialized.
	4. Loop until queue goes empty:
		a. Pop one element from queue, call it current.
		b. As we popped, increase the state expansion counter (stateExpansionCounter++).
		c. Check if popped element is destination. If so, exit.
		d. If popped element in point b is not destination, Obtain all the next cells to explore further.
		All the reachable nodes from current are identified and added to queue.
		As recommended, the order of queue addition is top,bottom,left,right.
		So, add the next node to queue and continue in loop.*/
        stateExpansionCounter=0;
        Node start = maze[0][0];
        Queue<Node> queue = new LinkedList<Node>();
        start.wasQueued=1;
        start.path="0,0\n";
        start.cost=1;
        ((LinkedList<Node>) queue).add(start);
        while(queue.size()>0){
            //System.out.println("loop: "+loopCounter++);
            Node current = queue.remove();
            stateExpansionCounter++;
            if(current.value==0) {//solution node
                maze[destinationRowIndex][destinationColIndex] = current;
                break;
            }

            Node top = getTopOption(current);
            if(top!=null&&top.wasQueued!=1){
                top.cost=current.cost+1;
                top.path=current.path+top.rowIndex+","+top.colIndex+"\n";
                top.wasQueued=1;
                ((LinkedList<Node>) queue).add(top);
            }
            Node bottom = getBottomOption(current);
            if(bottom!=null&&bottom.wasQueued!=1){
                bottom.cost=current.cost+1;
                bottom.path=current.path+bottom.rowIndex+","+bottom.colIndex+"\n";
                bottom.wasQueued=1;
                ((LinkedList<Node>) queue).add(bottom);
            }
            Node left = getLeftOption(current);
            if(left!=null&&left.wasQueued!=1){
                left.cost=current.cost+1;
                left.path=current.path+left.rowIndex+","+left.colIndex+"\n";
                left.wasQueued=1;
                ((LinkedList<Node>) queue).add(left);
            }
            Node right = getRightOption(current);
            if(right!=null&&right.wasQueued!=1){
                right.cost=current.cost+1;
                right.path=current.path+right.rowIndex+","+right.colIndex+"\n";
                right.wasQueued=1;
                ((LinkedList<Node>) queue).add(right);
            }

        }
        System.out.println("BFS: Path to destination:\n"+maze[destinationRowIndex][destinationColIndex].path);
        System.out.println("Cost: "+maze[destinationRowIndex][destinationColIndex].cost);
        System.out.println("Number of states expanded: "+stateExpansionCounter);
        try {
            createFile(maze[destinationRowIndex][destinationColIndex].path,"BFS");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    static int heuristic(Node node){
        /*Heuristic(node):
	1. If the node is destination, return 0.
	2. If the node is in reachable range of destination, return 1.
	3. If the node is not in reachable range of destination, return 2.
	A node B is reachable from A, if we are able to traverse from A to B in single step.
	This traversal can be horizontal or vertical or both(Relaxation).
         */
        if(node.value==0) {
            return 0;
        }
        else{
            int temp1x,temp1y=0,temp2x,temp2y=0;
            for(int i=0;i<=node.value;i++){
                temp1x=node.rowIndex+i;
                temp2x=node.rowIndex-i;
                for(int j=node.value-i;j>=0;j--){
                    temp1y=node.colIndex+j;
                    temp2y=node.colIndex-j;
                }
                if(!(temp1x>=rowCount||temp1y>=colCount)){
                    Node next=maze[temp1x][temp1y];
                    if(next.value==0) return 1;
                }
                if(!(temp2x<0||temp2y<0)){
                    Node next=maze[temp2x][temp2y];
                    if(next.value==0) return 1;
                }
            }
        }
        return 2;
    }
    static Node updateCheapCost(Node node){
        if(node.current_cost<node.cost){
            node.cost=node.current_cost;
            node.path=node.current_path;
        }
        return node;
    }
    static void addToPQueue(Node current, Node next, String algorithm){
        next.current_path=current.current_path+next.rowIndex+","+next.colIndex+"\n";
        next.current_cost = current.current_cost + 1;
        boolean containsCheck=false;
        for(String x: next.traversals){
            if(x.contains(next.current_path)){
                containsCheck=true;
                break;
            }
        }
        if(!containsCheck) {
            if(algorithm.equals("astar")) next.heuristic=next.current_cost+heuristic(next);
            else if(algorithm.equals("bestfirst")) next.heuristic = heuristic(next);
            next.wasQueued = 1;
            updateCheapCost(next);
            next.traversals.add(next.current_path);
            pqueue.add(next);
        }
        maze[next.rowIndex][next.colIndex]=next;
    }
    static void BestFirst(){
        /*1. Create Priority Queue data structure that stores objects of Node class and compares using
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
		So, add all these nodes to priority queue and continue in loop.*/
        stateExpansionCounter=0;
        Node start = maze[0][0];
        pqueue = new PriorityQueue<Node>(new NodeComparator());
        start.wasQueued=1;
        start.current_path="0,0\n";
        start.path="0,0\n";
        start.cost=1;
        start.current_cost=1;
        start.heuristic=heuristic(start);
        pqueue.add(start);
        while(pqueue.isEmpty()==false){
            Node current=pqueue.poll();
            stateExpansionCounter++;
            current.wasQueued=0;
            maze[current.rowIndex][current.colIndex]=current;
            if(current.value==0){
                break;
            }
            Node left=getLeftOption(current);
            if(left!=null//&&left.wasQueued!=1
                    &&!current.current_path.contains(left.rowIndex+","+left.colIndex)){
                addToPQueue(current,left,"bestfirst");

            }
            Node right=getRightOption(current);
            if(right!=null//&&right.wasQueued!=1
                    &&!current.current_path.contains(right.rowIndex+","+right.colIndex)){
                addToPQueue(current,right,"bestfirst");
            }
            Node bottom=getBottomOption(current);
            if(bottom!=null//&&bottom.wasQueued!=1
                    &&!current.current_path.contains(bottom.rowIndex+","+bottom.colIndex)){
                addToPQueue(current,bottom,"bestfirst");
            }
            Node top=getTopOption(current);
            if(top!=null//&&top.wasQueued!=1
                    &&!current.current_path.contains(top.rowIndex+","+top.colIndex)){
                addToPQueue(current,top,"bestfirst");
            }
        }
        System.out.println("Best First: Path to destination, for given commands:\n"
                +maze[destinationRowIndex][destinationColIndex].path);
        System.out.println("Cost: "+maze[destinationRowIndex][destinationColIndex].cost);

        System.out.println("Number of states expanded: "+stateExpansionCounter);
        try {
            createFile(maze[destinationRowIndex][destinationColIndex].path,"BestFirst");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    static void AStar(){
        /*1. Create Priority Queue data structure that stores objects of Node class and compares using
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
		So, add all these nodes to priority queue and continue in loop.*/
        stateExpansionCounter=0;
        Node start = maze[0][0];
        pqueue = new PriorityQueue<Node>(new NodeComparator());
        start.wasQueued=1;
        start.current_path="0,0\n";
        start.path="0,0\n";
        start.cost=1;
        start.current_cost=1;
        start.heuristic=1+heuristic(start);
        pqueue.add(start);
        while(pqueue.isEmpty()==false){
            Node current=pqueue.poll();
            stateExpansionCounter++;
            current.wasQueued=0;
            maze[current.rowIndex][current.colIndex]=current;
            if(current.value==0){
                break;
            }
            Node left=getLeftOption(current);
            if(left!=null//&&left.wasQueued!=1
                    &&!current.current_path.contains(left.rowIndex+","+left.colIndex)){
                addToPQueue(current,left,"astar");

            }
            Node right=getRightOption(current);
            if(right!=null//&&right.wasQueued!=1
                    &&!current.current_path.contains(right.rowIndex+","+right.colIndex)){
                addToPQueue(current,right,"astar");
            }
            Node bottom=getBottomOption(current);
            if(bottom!=null//&&bottom.wasQueued!=1
                    &&!current.current_path.contains(bottom.rowIndex+","+bottom.colIndex)){
                addToPQueue(current,bottom,"astar");
            }
            Node top=getTopOption(current);
            if(top!=null//&&top.wasQueued!=1
                    &&!current.current_path.contains(top.rowIndex+","+top.colIndex)){
                addToPQueue(current,top,"astar");
            }
        }
        System.out.println("Astar: Path to destination, for given commands:\n"
                +maze[destinationRowIndex][destinationColIndex].path);
        System.out.println("Cost: "+maze[destinationRowIndex][destinationColIndex].cost);

        System.out.println("Number of states expanded: "+stateExpansionCounter);
        try {
            createFile(maze[destinationRowIndex][destinationColIndex].path,"AStar");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws IOException {
        /*1. Read inputfile.
	2. Use function getMaze() that creates maze[][] (described above) data structure from input maze.
	3. Based on inputs, it calls BFS/DFS/BestFirst/AStar/UniquePaths*/
        rowCount = getRowCount(args[1]);
        maze = getMaze(args[1]);
        if(args[0].equalsIgnoreCase("unique")) DFS(1);
        else if(args[0].equalsIgnoreCase("DFS")) DFS(0);
        else if (args[0].equalsIgnoreCase("BFS")) BFS();
        else if(args[0].equalsIgnoreCase("BestFirst")) BestFirst();
        else if(args[0].equalsIgnoreCase("astar")) AStar();
    }
}