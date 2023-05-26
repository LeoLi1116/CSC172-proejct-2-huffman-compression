// Import any package as required



import java.io.File;
import java.io.FileWriter;
import java.util.*;

public class HuffmanSubmit implements Huffman {
  
	// Feel free to add more methods and variables as required.
    class Node{
       int priority;
       char character;
       Node left;
       Node right;

        Node(int num, char c) {
            this.priority = num;
            this.character = c;
            right = null;
            left = null;
        }

        void setPriority(int num){
            this.priority = num;
        }
        void setCharacter(char c){
            this.character = c;
        }

        void insertLeft(Btree l){
            this.left = l.root;
        }

        void insertRight(Btree r){
            this.right = r.root;

        }
    }



    class Btree {
        Node root;

        Btree(){
            root = new Node(0,'\0');
        }
        void setPriority(int num){
            root.setPriority(num);
        }
        void setCharacter(char c){
            root.setCharacter(c);
        }

        void insertChildren(Btree l, Btree r){
            root.insertLeft(l);
            root.insertRight(r);
            root.setPriority(l.root.priority+r.root.priority);
        }




    }





    public ArrayList<String> binaryValue(Node a, String b,ArrayList<String> c){
        if (a.left == null && a.right == null){
            String d = "";
            d=  d+ a.character;
            c.add(d);
            c.add(b);
        }
        else {
            if (a.left != null){
                binaryValue(a.left, b + "0",c);
            }
            if (a.right != null){
                binaryValue(a.right,b + "1",c);
            }
        }

        return c;
    }

    public String findString(Node a, BinaryIn s){
        String bs = s.readString();
        StringBuilder stringBuilder = new StringBuilder();
        Node x = a;
        for  (int i = 0; i < bs.length(); i++) {
            int j = bs.charAt(i);
            if (j== '0') {
                a = a.left;
                if (a.left == null && a.right == null) {
                    stringBuilder.append(a.character);
                    a = x;
                }
            }
            if (j=='1') {
                a = a.right;
                if (a.left == null && a.right == null) {
                    stringBuilder.append(a.character);
                    a = x;
                }
            }
        }
        return stringBuilder.toString();
    }


	public void encode(String inputFile, String outputFile, String freqFile){
		// TODO: Your code here
        try {
            File fFile = new File(freqFile);
            FileWriter freqFileWriter = new FileWriter(fFile);
            String fileType = inputFile.substring(inputFile.length()-3);


            if(fileType.compareTo("txt")==0) {
                File inFile = new File(inputFile);


                BinaryOut outFileWriter = new BinaryOut(outputFile);

                Scanner fileReader = new Scanner(inFile);
                ArrayList<ArrayList<Character>> fileChar = new ArrayList<ArrayList<Character>>();
                ArrayList<Character> diffChar = new ArrayList<Character>();
                ArrayList<Integer> num = new ArrayList<Integer>();

                //read file
                while (fileReader.hasNextLine()) {
                    String s = fileReader.nextLine();
                    ArrayList<Character> line = new ArrayList<Character>();
                    //convert file to list of chars
                    if (! s.isEmpty()) {
                        //if line is not empty
                        for (int i = 0; i < s.length(); i++) {
                            char c = s.charAt(i);
                            line.add(c);
                            // check frequency of chars, if it is the first one, add to list
                            if (!diffChar.contains(c)) {
                                diffChar.add(c);
                                num.add(1);
                            } else {
                                int index = diffChar.indexOf(c);
                                num.set(index, num.get(index) + 1);
                            }
                        }
                            line.add('|');
                    }
                    else {
                        //if line is empty
                        char c = '|';
                        line.add(c);
                        // check frequency of chars, if it is the first one, add to list
                        if (!diffChar.contains(c)) {
                            diffChar.add(c);
                            num.add(1);
                        } else {
                            int index = diffChar.indexOf(c);
                            num.set(index, num.get(index) + 1);
                        }
                    }
                    //add post read line to the list
                    fileChar.add(line);
                }


                PriorityQueue<Btree> leaves = new PriorityQueue<Btree>((a,b)->a.root.priority-b.root.priority);
                //write freq file and create node of trees
                for (int i = 0; i < diffChar.size(); i++){

                        Btree leaf = new Btree();
                        leaf.setPriority(num.get(i));
                        leaf.setCharacter(diffChar.get(i));
                        leaves.add(leaf);

                        //write freq file
                        char c = diffChar.get(i);
                        byte bc = (byte) c;//tranlate char to 9 digits binary value
                        String s1 = String.format("%8s", Integer.toBinaryString(bc & 0xFF)).replace(' ', '0');
                        String output = s1 + ":" + num.get(i).toString()+ "\n";
                        freqFileWriter.write(output);
                }

                //build huffman tree
                while (leaves.size() > 1){
                    Btree left = leaves.remove();
                    Btree right = leaves.remove();

                    Btree internal = new Btree();
                    internal.insertChildren(left,right);
                    leaves.add(internal);

                }
                //root node
                Btree huffman = leaves.remove();

                ArrayList<String> bv = new ArrayList<String>();
                bv = binaryValue(huffman.root,"",bv);

                ArrayList<ArrayList<String>> postfix = new ArrayList<ArrayList<String>>();

                //create 2D list for post fixed file
                for (int i = 0;i< fileChar.size();i++){
                    ArrayList<Character> line = fileChar.get(i);
                    ArrayList<String> postFixedLine = new ArrayList<String>();
                    for (int j =0; j < line.size(); j++){
                        for (int k= 0; k < bv.size()-1; k=k+2){
                            String compare = bv.get(k);
                            if (line.get(j) == compare.charAt(0)){
                                postFixedLine.add(bv.get(k+1));
                            }
                        }
                    }
                    postfix.add(postFixedLine);
                }
                //create encode file
                for (int i = 0;i< postfix.size();i++){
                    for (int j =0; j < postfix.get(i).size(); j++){
                        for (int k = 0; k < postfix.get(i).get(j).length(); k++){
                            boolean x;
                            if (postfix.get(i).get(j).charAt(k) == '0'){
                                x = false;
                            }
                            else x= true;
                            outFileWriter.write(x);
                        }

                    }
                }
                    outFileWriter.flush();
                    freqFileWriter.close();




        }
            else {
                BinaryIn input = new BinaryIn(inputFile);
                BinaryOut output = new BinaryOut(outputFile);
                ArrayList<Character> chars = new ArrayList<Character>();
                ArrayList<Integer> num  = new ArrayList<>();
                while (!input.isEmpty()){
                    char c = input.readChar();
                    if (!chars.contains(c)) {
                        chars.add(c);
                        num.add(1);
                    } else {
                        int index = chars.indexOf(c);
                        num.set(index, num.get(index) + 1);
                    }
                }

                PriorityQueue<Btree> leaves = new PriorityQueue<Btree>((a,b)->a.root.priority-b.root.priority);
                //write freq file and create node of trees
                for (int i = 0; i < chars.size(); i++){

                    Btree leaf = new Btree();
                    leaf.setPriority(num.get(i));
                    leaf.setCharacter(chars.get(i));
                    leaves.add(leaf);

                    //write freq file
                    char c = chars.get(i);

                    String s1 = Integer.toBinaryString(c).replace(' ', '0');
                    String freq = s1 + ":" + num.get(i).toString()+ "\n";
                    freqFileWriter.write(freq);
                }

                freqFileWriter.close();

                //build huffman tree
                while (leaves.size() > 1){
                    Btree left = leaves.remove();
                    Btree right = leaves.remove();

                    Btree internal = new Btree();
                    internal.insertChildren(left,right);
                    leaves.add(internal);

                }
                //root node
                Btree huffman = leaves.remove();

                ArrayList<String> bv = new ArrayList<String>();
                bv = binaryValue(huffman.root,"",bv);


                BinaryIn in = new BinaryIn(inputFile);


                while (!in.isEmpty()) {
                    char c = in.readChar();
                    String s = "" + c;
                    int index = bv.indexOf(s);
                    String code = bv.get(index+1);
                    /*for (int i = 0; i < code.length(); i ++){
                        if (code.charAt(i) == '0'){
                            System.out.print(0);
                            output.write(false);
                        }
                        else {
                            System.out.print(1);
                            output.write(true);
                        }
                    }

                     */
                    output.write(code);
                    output.flush();
                }
                output.close();
            }

        }
        catch (Exception file){
            file.printStackTrace();
            System.out.println("Errors Occur with encoding");
        }

    }


   public void decode(String inputFile, String outputFile, String freqFile){
		// TODO: Your code here
       try {

           String fileType = outputFile.substring(outputFile.length()-3);


           //decode file
           if (fileType.compareTo("txt")==0){
               File fFile = new File(freqFile);


               BinaryIn inFileReader = new BinaryIn(inputFile);
               FileWriter outFileWriter = new FileWriter(outputFile);


               Scanner fileReader = new Scanner(fFile);
               ArrayList<Character> diffChar = new ArrayList<Character>();
               ArrayList<Integer> num = new ArrayList<Integer>();

               //read freq file
               while (fileReader.hasNextLine()){
                   String s = fileReader.nextLine();
                   String binary = s.substring(0,8);
                   byte bc = Byte.parseByte(binary,2);
                   Character c =  (char) (bc &  0xFF);
                   int freq = Integer.parseInt(s.substring(9));
                   diffChar.add(c);
                   num.add(freq);
               }

               PriorityQueue<Btree> leaves = new PriorityQueue<Btree>((a,b)->a.root.priority-b.root.priority);
               //create node of trees from freq file value
               for (int i = 0; i < diffChar.size(); i++){

                   Btree leaf = new Btree();
                   leaf.setPriority(num.get(i));
                   leaf.setCharacter(diffChar.get(i));
                   leaves.add(leaf);
               }

               //build huffman tree
               while (leaves.size() > 1){
                   Btree left = leaves.remove();
                   Btree right = leaves.remove();

                   Btree internal = new Btree();
                   internal.insertChildren(left,right);
                   leaves.add(internal);

               }
               //root node
               Btree huffman = leaves.remove();

               ArrayList<String> bv = new ArrayList<String>();
               bv = binaryValue(huffman.root,"",bv);
               ArrayList<String> chars = new ArrayList<String>();
               ArrayList<String> code = new ArrayList<String>();
               //create lists for chars and binary code
               for (int i = 0; i < bv.size(); i += 2){
                   chars.add(bv.get(i));
               }
               for (int i = 1; i < bv.size(); i += 2){
                   code.add(bv.get(i));
               }


               //encode file is not empty
               while (!inFileReader.isEmpty()) {
                   String line = "";
                   String letter = "";
                   //switch line
                   while (letter.compareTo("|") != 0) {

                       line = line + letter;
                       String bLetter = "";
                       //add up code till a match one found
                       while (!code.contains(bLetter) ) {
                           if (inFileReader.isEmpty()){
                               outFileWriter.flush();
                               return;
                           }
                           boolean tf = inFileReader.readBoolean();
                           String c;
                           if (tf) {
                               c = "1";
                           } else {
                               c = "0";
                           }
                           bLetter += c;
                       }
                       letter = chars.get(code.indexOf(bLetter));
                   }
                   line += "\n";
                   outFileWriter.write(line);
                   }
                    outFileWriter.flush();
                    outFileWriter.close();
           }
           else {
               File fFile = new File(freqFile);
               BinaryIn Input = new BinaryIn(inputFile);
               BinaryOut out = new BinaryOut(outputFile);
               Scanner fileReader = new Scanner(fFile);


               ArrayList<Character> diffChar = new ArrayList<Character>();
               ArrayList<Integer> num = new ArrayList<Integer>();
               while (fileReader.hasNextLine()){
                   String s = fileReader.nextLine();
                   int index = s.indexOf(":");
                   String binary = s.substring(0,index);
                   int parseInt = Integer.parseInt(binary, 2);
                   char c = (char)parseInt;
                   int freq = Integer.parseInt(s.substring(index+1));
                   diffChar.add(c);
                   num.add(freq);
               }

               PriorityQueue<Btree> leaves = new PriorityQueue<Btree>((a,b)->a.root.priority-b.root.priority);
               //create node of trees from freq file value
               for (int i = 0; i < diffChar.size(); i++){

                   Btree leaf = new Btree();
                   leaf.setPriority(num.get(i));
                   leaf.setCharacter(diffChar.get(i));
                   leaves.add(leaf);
               }

               //build huffman tree
               while (leaves.size() > 1){
                   Btree left = leaves.remove();
                   Btree right = leaves.remove();

                   Btree internal = new Btree();
                   internal.insertChildren(left,right);
                   leaves.add(internal);

               }
               //root node
               Btree x = leaves.remove();

               ArrayList<String> bv = new ArrayList<String>();
               bv = binaryValue(x.root,"",bv);

               ArrayList<String> chars = new ArrayList<String>();
               ArrayList<String> code = new ArrayList<String>();


               //create lists for chars and binary code
               for (int i = 0; i < bv.size(); i += 2){
                   chars.add(bv.get(i));
               }
               for (int i = 1; i < bv.size(); i += 2){
                   code.add(bv.get(i));
               }








               out.write(findString(x.root,Input));
               out.flush();
               out.close();

           }


       }
       catch (Exception file){
           file.printStackTrace();
           System.out.println("Errors Occur with decoding");
       }




   }




   public static void main(String[] args) {
      Huffman  huffman = new HuffmanSubmit();



      huffman.encode("C:\\Users\\13410\\IdeaProjects\\Project_2\\src\\ur.jpg",
              "C:\\Users\\13410\\IdeaProjects\\Project_2\\src\\ur.enc",
              "C:\\Users\\13410\\IdeaProjects\\Project_2\\src\\freq.txt");





      huffman.decode("C:\\Users\\13410\\IdeaProjects\\Project_2\\src\\ur.enc",
              "C:\\Users\\13410\\IdeaProjects\\Project_2\\src\\ur_dec.jpg",
              "C:\\Users\\13410\\IdeaProjects\\Project_2\\src\\freq.txt");


       // After decoding, both ur.jpg and ur_dec.jpg should be the same.
       // On linux and mac, you can use `diff' command to check if they are the same.
   }

}
