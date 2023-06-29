/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.cnr.ilc.compareandmergelexicons;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author Simone Marchi
 */
public class ConllComparator {

    private BufferedReader firstFile;
    private BufferedReader secondFile;
    private BufferedReader thirdFile;

    public BufferedReader getFirstFile() {
        return firstFile;
    }

    public void setFirstFile(BufferedReader firstFile) {
        this.firstFile = firstFile;
    }

    public BufferedReader getSecondFile() {
        return secondFile;
    }

    public void setSecondFile(BufferedReader secondFile) {
        this.secondFile = secondFile;
    }

    public BufferedReader getThirdFile() {
        return thirdFile;
    }

    public void setThirdFile(BufferedReader thirdFile) {
        this.thirdFile = thirdFile;
    }

    public ConllComparator() {

    }

    void compareLineByLine() throws IOException, MalformedConllRowException, Exception {

        boolean nextA = true;
        boolean nextB = true;
        boolean nextC = true;
        boolean endA = false;
        boolean endB = false;
        boolean endC = false;
        ConllRow conllA = null; //Priority file to take info
        ConllRow conllB = null;
        ConllRow conllC = null;
        FileWriter fileWriter = new FileWriter("/home/simone/Nextcloud/PROGETTI/FormarioItalex/Lexico/merge.csv");
        PrintWriter printWriter = new PrintWriter(fileWriter);

        while (!endA || !endB || !endC) {
            if (nextA && !endA) {
                String firstString = firstFile.readLine();
                if (firstString != null) {
                    conllA = new ConllRow(firstString);
                } else {
                    System.err.println("End of first file");
                    endA = true;
                    conllA = new ConllRow("1\tzzz\tzzz\tzzz\t_\t_\t_\t_\t_\t_"); //create a fake entry as last word 

                }
                nextA = false;
            }

            if (nextB && !endB) {
                String secondString = secondFile.readLine();
                if (secondString != null) {
                    conllB = new ConllRow(secondString);
                } else {
                    System.err.println("End of second file");
                    endB = true;
                    conllB = new ConllRow("2\tzzz\tzzz\tzzz\t_\t_\t_\t_\t_\t_"); //create a fake entry as last word 

                }
                nextB = false;
            }

            if (nextC && !endC) {
                String thirdString = thirdFile.readLine();
                if (thirdString != null) {
                    conllC = new ConllRow(thirdString);
                } else {
                    System.err.println("End of third file");
                    endC = true;
                    conllC = new ConllRow("3\tzzz\tzzz\tzzz\t_\t_\t_\t_\t_\t_"); //create a fake entry as last word

                }
                nextC = false;
            }

            int compareAB = conllA.compareEntry(conllB);
            int compareAC = conllA.compareEntry(conllC);
            int compareBC = conllB.compareEntry(conllC);

            int traitsAB = conllA.compareTraits(conllB);
            int traitsAC = conllA.compareTraits(conllC);
            int traitsBC = conllB.compareTraits(conllC);

            if (compareAB == 0) {//A=B
                if (compareAC == 0) { //A=C
                    nextA = true;
                    nextB = true;
                    nextC = true;
                    if (traitsAB == 0) {
                        switch (traitsAC) {
                            case 0: //A=B=C
                                //prendo un qualsiasi
                                printWriter.print(conllA.toString());
                                break;
                            case 1:
                                //A=B contiene C
                                printWriter.print(conllA.toString());
                                break;
                            case 2:
                                //C contiene A=B
                                printWriter.print(conllC.toString());
                                break;
                            default: //-1 A=B != C
                                printWriter.print(conllA.toString());
                                printWriter.print(conllC.toString());
                                //che si fa? Si prende una tra A e B e si prende C
                                break;
                        }
                    }

                } else if (compareAC < 0) { //A<C, A=B
                    nextA = true;
                    nextB = true;
                    switch (traitsAB) {
                        case 0:
                            //prendo un qualsiasi
                            printWriter.print(conllA.toString());
                            break;
                        case 1:
                            //A contiene B 
                            printWriter.print(conllA.toString());
                            break;
                        case 2:
                            //B contiene A
                            printWriter.print(conllB.toString());
                            break;
                        default: //-1 A != B
                            //che si fa? Si prendeno entrambi? Alrimenti quale?
                            printWriter.print(conllA.toString());
                            printWriter.print(conllB.toString());
                            break;
                    }
                } else { //A=B>C
                    printWriter.print(conllC.toString());
                    nextC = true;
                }
            } else if (compareAB > 0) { //A>B
                if (compareBC == 0) { //A>B=C
                    nextB = true;
                    nextC = true;
                    switch (traitsBC) {
                        case 0: //B=C
                            //prendo un qualsiasi
                            printWriter.print(conllB.toString());
                            break;
                        case 1:
                            //B contiene C
                            printWriter.print(conllB.toString());
                            break;
                        case 2:
                            //C contiene B
                            printWriter.print(conllC.toString());
                            break;
                        default: //-1 B != C
                            printWriter.print(conllB.toString());
                            printWriter.print(conllC.toString());
                            //che si fa? Si prendeno entrambi? Alrimenti quale?
                            break;
                    }
                } else if (compareBC > 0) {//A>B, B>C => A>B>C
                    printWriter.print(conllC.toString());
                    nextC = true;
                } else { //A>B, B<C => A>B<C
                    printWriter.print(conllB.toString());
                    nextB = true;
                }
            } else { //A<B
                if (compareBC == 0) { //A<B=C
                    nextA = true;
                } else if (compareBC > 0) {//A<B, B>C => A<B>C
                    if (compareAC == 0) {//A=C<B
                        switch (traitsAC) {
                            case 0: //A=C
                                //prendo un qualsiasi
                                printWriter.print(conllA.toString());
                                break;
                            case 1:
                                //A contiene C
                                printWriter.print(conllA.toString());
                                break;
                            case 2:
                                //C contiene A
                                printWriter.print(conllC.toString());
                                break;
                            default: //-1 A != C
                                //che si fa? Si prendeno entrambi? Alrimenti quale?
                                printWriter.print(conllA.toString());
                                printWriter.print(conllC.toString());
                                break;
                        }
                        nextA = true;
                        nextC = true;
                    } else if (compareAC < 0) {//A<B A<C => B>A<C
                        printWriter.print(conllA.toString());
                        nextA = true;
                    } else { //A<B C<A => C<A<B
                        printWriter.print(conllC.toString());
                        nextC = true;
                    }
                } else { //A<B, B<C
                    printWriter.print(conllA.toString());
                    nextA = true;
                }
            }

        }

        printWriter.flush();
        printWriter.close();

        /*
        A=B=C => NEXT SU TUTTI v
        A=B<C => NEXT SU A,B
        A=B>C => NEXT SU C
        A<B=C => NEXT SU A
        A<B<C => NEXT SU A
        A<B>C => NEXT SUL minore tra A e C
        A>B=C => NEXT SU B,C
        A>B<C => NEXT SU B
        A>B>C => NEXT SU C
        A=C<B => NEXT SU A,C
        A=C>B => NEXT SU B
        A<C>B => NEXT SUL minore tra A e B
        B<A>C => NEXT SUL minore tra B e C
        
         */
    }
}
