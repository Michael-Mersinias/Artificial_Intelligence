package mainpackage;
import java.util.ArrayList;


//Got the basic structure to save time from:http://stackoverflow.com/a/22419453
//But the code had problems and was debugged from us, and we also added some extra functions.

//This class is used to create a tree of Vertexes
public class Node<T>{
  private ArrayList<Node<T>> children = new ArrayList<Node<T>>();
  private Node<T> parent = null;
  private Node<T> head;
  private T data = null;

  public Node(T data) {
      this.data = data;
      this.head=this;
  }
  
  public ArrayList<Node<T>> getChildren() {
      return children;
  }
  
  public Node<T> getParent(){
  	return parent;
  }
  public void setParent(Node<T> parent) {
      
      this.parent = parent;
  }
  private void setHead(Node<T> head){
  	this.head=head;
  }
  
  public Node<T> getHead(){
  	return this.head;
  }
  
  public Node<T> addChild(T data) {
      Node<T> child = new Node<T>(data);
      child.setParent(this);
      child.setHead(this.head);
      this.children.add(child);
      return child;
  }

  public void addChild(Node<T> child) {
      child.setParent(this);
      child.setHead(this.head);
      this.children.add(child); 
  }

  public T getData() {
      return this.data;
  }

  public void setData(T data) {
      this.data = data;
  }

  public boolean isRoot() {
      return (this.parent == null);
  }

  public boolean isLeaf() {
      if(this.children.size() == 0) 
          return true;
      else 
          return false;
  }

  public void removeParent() {
      this.parent = null;
  }


}