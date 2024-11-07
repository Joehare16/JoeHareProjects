/**
 * RBT
 * Red-Black Tree Insert
 * @author jh2199@kent.ac.uk
 */
import java.util.*;
public class RBT {
    private Node root;

    public RBT() {
    }

    public boolean isRed(Node x) {
        if (x == null) return false;
        return x.getColor() == Node.Color.RED;
    }

    public boolean isEmpty() {
        return root == null;
    }

    public boolean contains(int x) {
        return nodeContainsData(root, x);
    }

    private boolean nodeContainsData(Node r, int x) {
        while (r != null) {
            if (r.getData() - x < 0) {
                r = r.getLeft();
            } else if (r.getData() - x > 0) {
                r = r.getRight();
            } else {
                return true;
            }
        }
        return false;
    }

    public List<Integer> serializeTree() {
        return serializeTree(root);
    }

    private List<Integer> serializeTree(Node r) {
        if (r == null) return new LinkedList<>();
        int data = r.getData();
        List<Integer> left = serializeTree(r.getLeft());
        List<Integer> right = serializeTree(r.getRight());
        left.add(data);
        left.addAll(right);
        return left;
    }

    public int maxHeight() {
        return maxHeight(root);
    }

    private int maxHeight(Node r) {
        if (r == null) return 0;
        return 1 + Math.max(maxHeight(r.getLeft()), maxHeight(r.getRight()));
    }


    // ************************************************************************
    // * INSERT INTO RED-BLACK TREE
    // ************************************************************************

    public void insert(int x) {
        root = nodeInsertData(root, x);
        root.setColor(Node.Color.BLACK);

    }
    private Node nodeInsertData(Node r, int x) {
        //BST insertion
        if (r == null)
            return new Node(x, Node.Color.RED); //if the root is empty then the inserted root becomes the root and color is set to red as all new nodes are inserted as red

        if (x < root.getData()) { //this checks if the value inserted is less then the data in the root - if it is less then it is place to the left of the tree
            // if it is then the left node is set to the return of the function call and it recursively calls nodeInsertData but with the left node now as the root
            r.setLeft(nodeInsertData(r.getLeft(), x));
        }
        //checks if the inserted value is bigger then the root - if it is then must be place to the right of the tree
        else if (r.getData() < x) {
            //sets the right node to the return of the function call and calls the function with the node to the right of the root as the root, meaning the inserted value is now being checked against the right side of the tree
            r.setRight(nodeInsertData(r.getRight(), x));
        }
        //RBT checks
        // This called on a CASE 3 break as the two children are different colours so when the node get inserted it breask the properties of the tree
        //therefore a left rotation is called on the tree so that the new node being added becomes the parent and old root becomes child
        //called if right node is red and the left node is black
       if (isRed(r.getRight()) && !isRed(r.getLeft())) {
            r = rotateLeft(r);
       }
       //this function call is invoked when there is a CASE 4 break as there are two red nodes in a line on the tree.
        //the grandparent is rotated in order to maintain the trees properties
        if (isRed(r.getLeft()) && isRed(r.getLeft().getLeft())) {
            r = rotateRight(r);
        }
        //Covers CASE 2 where the unlce and parent are red and grandparent black so colors need to be flipped 
        //calls the flip color function when both of the child nodes are red as color flip function make the node red and its children black
        if (isRed(r.getLeft()) && isRed(r.getRight())) {
            flipColors(r);
       }

        return r;
        //returns the root
    }

    private Node rotateRight(Node h) {
        assert (h != null) && isRed(h.getLeft());
       // gets left child of the root h, which will become the new root
        Node leftChild = h.getLeft();
        //gets the right child of left child and sets it as the new left child of h
        //as the new root is now smaller than h so the right children of the left child have ot become the left child of h
        //left children stay as left children of leftchild(new root) and right children of h stay as right children
        h.setLeft(leftChild.getRight());
        //now all these nodes are left children of h the new root (left child) is now set to the parent of h by making h the right child of leftChild
        leftChild.setRight(h);
        //sets the color of the new root to the color of h (old root)
        //preserves color properties of the rbt tree
        leftChild.setColor(h.getColor());
        h.setColor(Node.Color.RED);
        return leftChild;
}
 
    private Node rotateLeft(Node h) {
        assert (h != null) && isRed(h.getRight());
        //h becomes the parent of right child, gets the right child of h which will become new root
        Node rightChild = h.getRight();
        //gets the left child of hs right child (new root) and makes it the right child of h
        //this is because the left children are no longer larger than the root so must become the right child of the old root
        //any nodes to the left of h stay as the left child
        //likewise any right children of hs right child will stay as right children of the new root
        h.setRight(rightChild.getLeft());
        //this then sets the h node with its new children as the left child of the new root(rightchild)
        //makes right child the new parent of h
        rightChild.setLeft(h);

        //this sets the color of the new root(rightchild) to the same color as h in order to preserve the trees properites
        rightChild.setColor(h.getColor());
        h.setColor(Node.Color.RED);
        return rightChild;

    }

    // flip the colors of a node and its two children
    private void flipColors(Node h) {

        //sets the root to red
        h.setColor(Node.Color.RED);
        //sets the left and right child of root to black
        //accounts for rule that is node is red then both children must be black
        h.getLeft().setColor(Node.Color.BLACK);
        h.getRight().setColor(Node.Color.BLACK);
    }
}
