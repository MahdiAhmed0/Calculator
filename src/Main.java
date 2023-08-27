import java.util.Scanner;




class Node{
    public int value;
    public Node next;
    public Node(int v){
        value = v;
        next =null;
    }
}
class LinkedList{
    public Node head;
    public LinkedList(){
        head = null;
    }
    public void AddHead(int v){
        Node t = new Node(v);
        t.next = head;
        head = t;
    }
    public void DeleteHead(){
        if(head!=null)
            head = head.next;
    }
    public void AddTail(int v){
        Node tmp = new Node(v);
        if(head==null){
            head = tmp;
            return;
        }
        Node h = head;
        while(h.next!=null) h = h.next;
        h.next = tmp;
    }
    public void Print(){
        Node h = head;
        if(h==null) System.out.println("The list is empty...");
        while(h!=null){
            System.out.print(h.value);
            h = h.next;
        }
        System.out.println();
    }
    
    
    
    public int Length(){
        int count = 0;
        Node h = head;
        while(h!=null){
            count++;
            h = h.next;
        }
        return count;
    }
    public void DeleteTail(){
        Node h = head;
        if(head==null) return;
        if(head.next==null){
            head = null;
            return;
        }
        while(h.next.next!=null) h=h.next;
        h.next = null;
    }
    public void DeleteAll(){
        head = null;
    }
    public void AddAfter(int after, int v){
        Node found = Find(after);
        if(found==null){
            System.out.println(after+" does not exist in the list.");
            return;
        }
        Node h = new Node(v);
        h.next = found.next;
        found.next = h;        
    }
    public Node Find(int v){
        Node h = head;
        while(h!=null){
            if(h.value == v)
                return h;
            h=h.next;
        }
        return null;
    }
    
    public void convertStrToNumAndAddToList(String x){
          char currentNumToBeConverted;
          int currentNum;
              for(int i = 0;i<x.length();i++){
                  currentNumToBeConverted = x.charAt(i);
                  currentNum = Character.getNumericValue(currentNumToBeConverted);
                  AddHead(currentNum);
              }
    }

    public void addTwoLists(LinkedList list1, LinkedList list2){
        Node first = list1.head;
        Node second = list2.head;
        int currentRes;
        int carry = 0;
        int lastDigit;

        while(first!=null || second!=null){
            if (first==null){
                Node tmp = new Node(0);
                first = tmp;
            }
            if (second==null) {
                Node tmp = new Node(0);
                second = tmp;
            }
            
            currentRes = first.value + second.value + carry;
            carry = currentRes / 10;
            lastDigit = currentRes % 10;            
            
            Node t = new Node(lastDigit);
            t.next = head;
            head = t;
            
            
            first = first.next;
            second = second.next;
        }
        if (carry > 0) {
            Node t = new Node(carry);
            t.next = head;
            head = t; 
        }
    }
    
     public LinkedList addTwoListsForMultiply(LinkedList list1, LinkedList list2){
        Node first = list1.head;
        Node second = list2.head;
        LinkedList answer = new LinkedList();
        int currentRes;
        int carry = 0;
        int lastDigit;
        
        while(first!=null || second!=null){
            if (first==null){
                Node tmp = new Node(0);
                first = tmp;
            }
            if (second==null) {
                Node tmp = new Node(0);
                second = tmp;
            }
            
            currentRes = first.value + second.value + carry;
            carry = currentRes / 10;
            lastDigit = currentRes % 10;            
            
            answer.AddTail(lastDigit);
            Node t = new Node(lastDigit);
            t.next = head;
            head = t;
            
            
            first = first.next;
            second = second.next;
        }
        if (carry > 0) {
            answer.AddTail(carry);
            Node t = new Node(carry);
            t.next = head;
            head = t; 
        }
        return answer;
    }
    
    public int whichBigger(LinkedList list1, LinkedList list2){
       Node first = list1.head;
       Node second = list2.head;
       
       LinkedList list1_checker = new LinkedList();
       LinkedList list2_checker = new LinkedList();
       while(first!=null){
           list1_checker.AddHead(first.value);
           first = first.next;
       }
       while(second!=null){
           list2_checker.AddHead(second.value);
           second = second.next;
       }
       
       
       Node first_checker = list1_checker.head;
       Node second_checker = list2_checker.head;
       
           if (list1_checker.Length() > list2_checker.Length()){
               return 1;
           } else if (list1_checker.Length() < list2_checker.Length()){
               return 0;
           }
           
           if (list1_checker.Length() == list2_checker.Length()){
               while(true){
                   if(first_checker.value > second_checker.value){
                       return 1;
                   } else if (first_checker.value < second_checker.value){
                       return 0;
                   }
                   
                   if (first_checker.next==null){
                       return -1;
                   }
                   first_checker = first_checker.next;
                   second_checker = second_checker.next;
               }
           }
           return -1;
    }
    
    
    
    public void subtractTwoLists(LinkedList list1, LinkedList list2){
        Node first = list1.head;
        Node second = list2.head;
        
        
        int currentRes;
        int lastDigit;
        int count = 0;
        boolean neg = false;
        
        int bigger = whichBigger(list1, list2);
        
        while (first!=null || second!=null) {
            if (first==null){
                Node tmp = new Node(0);
                first = tmp;
            }
            if (second==null) {
                Node tmp = new Node(0);
                second = tmp;
            }
            
            
            
            
            if (bigger == 1){
                if(first.value < second.value){
                    first.value += 10;
                    first.next.value -= 1;
                    currentRes = first.value - second.value;
                    Node t = new Node(currentRes);
                    t.next = head;
                    head = t; 

                    first = first.next;
                    second = second.next;
                } else {
                    currentRes = first.value - second.value;
                    Node t = new Node(currentRes);
                    t.next = head;
                    head = t; 

                    first = first.next;
                    second = second.next;
                }
            } else if (bigger == 0){
                neg = true;
                if(second.value < first.value){
                    second.value += 10;
                    if (second.next!=null) second.next.value -= 1;
                    currentRes = second.value - first.value;
                    Node t = new Node(currentRes);
                    t.next = head;
                    head = t; 

                    first = first.next;
                    second = second.next;
                } else {
                    currentRes = second.value - first.value;
                    Node t = new Node(currentRes);
                    t.next = head;
                    head = t; 

                    first = first.next;
                    second = second.next;
                }
                
            } else if (bigger == -1){
                Node t = new Node(0);
                t.next = head;
                head = t; 
                return;
            }
            
            
            
            
            
            
        }
        while(head.value == 0){
            if(head!=null)
            head = head.next;
        }
        
        if (neg){
            Node hTmp = head;
            hTmp.value = hTmp.value + (-hTmp.value+-hTmp.value);
        }
    }
    
    
    public LinkedList singleMultiply(int v, LinkedList list){
        Node h = list.head;
        LinkedList answer = new LinkedList();
        int currentRes;
        int carry = 0;
        int lastDigit;
        
        while(h!=null){
            
        
            currentRes = (h.value * v) + carry;
            carry = currentRes / 10;
            lastDigit = currentRes % 10;
            
            
            answer.AddTail(lastDigit);
            
            h = h.next;
        }
        if (carry > 0) answer.AddTail(carry);
        return answer;
    }
    
    public void multiplyTwoLists(LinkedList list1, LinkedList list2){
        Node first = list1.head;
        Node second = list2.head;
        
        int addZero = 0;
        LinkedList answer = new LinkedList();
        LinkedList t = new LinkedList();
        answer.AddHead(0);
        
        Node h1 = first;
        Node h2 = second;
        while(h2!=null){
            LinkedList tmp = new LinkedList();
            tmp = singleMultiply(h2.value, list1);
            for (int i = 0; i < addZero; i++) {tmp.AddHead(0);}
            addZero++;
            answer = answer.addTwoListsForMultiply(answer, tmp);
            
            
            h2 = h2.next;
        }
        Node hAns = answer.head;
        while(hAns!=null){
            Node t1 = new Node(hAns.value);
            t1.next = head;
            head = t1;
            
            hAns = hAns.next;
        }
        
        Node hZeros = answer.head;
        int count = 0;
        while(hZeros!=null){
            if (hZeros.value == 0)
                count++;
            hZeros = hZeros.next;
        }
        
        
        if (count == answer.Length()){
            DeleteAll();
            AddHead(0);
        }
    }
}



public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.println("Welcome to Mahdi's Calculator");
        System.out.println("to quit at anytime just type \"Exit\"");
        while(true){
        
        LinkedList num1List = new LinkedList();
        LinkedList num2List = new LinkedList();
        LinkedList results = new LinkedList();
        String num1String, num2String, method;
        System.out.print("Enter num1: ");
        num1String = input.next();
        if (num1String.equalsIgnoreCase("exit")){
            System.exit(0);
        }
        System.out.print("Enter num2: ");
        num2String = input.next();
        if (num2String.equalsIgnoreCase("exit")){
            System.exit(0);
        }
        num1List.convertStrToNumAndAddToList(num1String);
        num2List.convertStrToNumAndAddToList(num2String);
        System.out.print("choose between (+, -, *) : ");
        method = input.next();
        
        
        if(method.equals("+")){
            System.out.print("results: ");
            results.addTwoLists(num1List, num2List);
            results.Print();
            System.out.println("");
        } else if (method.equals("-")){
            System.out.print("results: ");
            results.subtractTwoLists(num1List, num2List);
            results.Print();
            System.out.println("");
        } else if (method.equals("*")){
            System.out.print("results: ");
            results.multiplyTwoLists(num1List, num2List);
            results.Print();
            System.out.println("");
        } else if (method.equalsIgnoreCase("exit")){
            System.exit(0);
        } else {
            System.out.println("undefined.");
            System.out.println("");
        }
        }
        
    }   
}
