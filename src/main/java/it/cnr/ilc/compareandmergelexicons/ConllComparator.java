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

    public ConllComparator() {

    }

    void compareLineByLine(String outputfilename) throws IOException, MalformedConllRowException, Exception {

        boolean nextA = true;
        boolean nextB = true;

        boolean endA = false;
        boolean endB = false;

        ConllRow conllA = null; //Priority file to take info
        ConllRow conllB = null;

        ConllRow fakeConllRow = new ConllRow(Constants.FAKEROWSTRING); //create a fake entry as last word ;
        FileWriter fileWriter = new FileWriter(outputfilename);
        PrintWriter printWriter = new PrintWriter(fileWriter);

        while (!endA || !endB) {
            if (nextA && !endA) {
                String firstString = firstFile.readLine();
                if (firstString != null) {
                    conllA = new ConllRow(firstString);
                } else {
                    System.err.println("End of first file");
                    endA = true;
                    conllA = fakeConllRow;

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
                    conllB = fakeConllRow;

                }
                nextB = false;
            }

            int entryComparison = conllA.compareEntry(conllB);

            switch (entryComparison) {//A=B
                case 0:
                    int traitsComparison = conllA.compareTraits(conllB);
                    switch (traitsComparison) {
                        case 0:
                            //prendo un qualsiasi
                            if (!endA) {
                                conllA.appendId(conllB);
                                conllA.copyMisc(conllB);
                                printWriter.print(conllA.toString());
                            }
                            nextA = true;
                            nextB = true;
                            break;
                        case 1:
                            //A contiene B
                            if (!endA) {
                                conllA.appendId(conllB);
                                conllA.copyMisc(conllB);
                                printWriter.print(conllA.toString());
                            }
                            nextA = true;
                            nextB = true;
                            break;
                        case 2:
                            //B contiene A
                            if (!endB) {
                                conllB.appendId(conllA);
                                conllB.copyMisc(conllA);
                                printWriter.print(conllB.toString());
                            }
                            nextA = true;
                            nextB = true;
                            break;
                        case -1: //A precede B
                            if (!endA) {
                                printWriter.print(conllA.toString());
                            }
                            nextA = true;
                            break;
                        case -2: //B precede A
                            if (!endB) {
                                printWriter.print(conllB.toString());
                            }
                            nextB = true;
                            break;

                        default: //-1 A=B != C
                            //che si fa? Si prende una tra A e B e si prende C
                            throw new AssertionError();
                    }
                    break;

                case 1: //B precede A
                    if (!endB) {
                        printWriter.print(conllB.toString());
                    }
                    nextB = true;

                    break;

                case -1: //A precede B
                    if (!endA) {
                        printWriter.print(conllA.toString());
                    }
                    nextA = true;
                    break;

                default:
                    throw new AssertionError();

            }
        }

        printWriter.flush();
        printWriter.close();

    }

}
