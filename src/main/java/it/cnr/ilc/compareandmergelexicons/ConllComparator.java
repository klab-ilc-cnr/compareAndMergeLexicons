/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.cnr.ilc.compareandmergelexicons;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.Collator;
import java.text.RuleBasedCollator;

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

        String customRules
                = ("< '%' < ''' < '\"'  < '(' < ')' < '-' < '.' < 0 < 1 < 2 < 3 < 4 < 5 "
                + "< a,A < b,B < c,C < d,D < e,E < f,F < g,G < h,H < i,I < j,J < k,K < l,L "
                + "< m,M < n,N < o,O < p,P < q,Q < r,R < s,S < t,T < u,U < v,V < w,W < x,X < y,Y < z,Z "
                + "< ° < µ < à,À < è,È < é,É < ì < ò < ù < '_'");

//        String customRules = (" < v < ù");
        RuleBasedCollator collator = new RuleBasedCollator(customRules);
        collator.setStrength(Collator.SECONDARY);

        //System.err.println(collator.compare("\"", "z"));
        ConllRow fakeConllRow = new ConllRow(Constants.FAKEROWSTRING, collator); //create a fake entry as last word ;
        FileWriter fileWriter = new FileWriter(outputfilename);
        PrintWriter printWriter = new PrintWriter(fileWriter);

        boolean printA = true;
        while (!endA || !endB) {
            if (nextA && !endA) {
                String firstString = firstFile.readLine();
                if (firstString != null) {
                    conllA = new ConllRow(firstString, collator);
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
                    conllB = new ConllRow(secondString, collator);
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
                                conllA.copyMisc(conllB);//??
                                printWriter.print(conllA.toString());
                            }
                            if (!Constants.NOVALUE.equals(conllA.getForma())) {
                                nextA = true;
                            }

                            if (!Constants.NOVALUE.equals(conllB.getForma())) {
                                nextB = true;
                            }
                            if (Constants.NOVALUE.equals(conllA.getForma())
                                    && Constants.NOVALUE.equals(conllB.getForma())) {
                                nextA = true;
                                nextB = true;
                            }
                            break;
                        case 1:
                            //A contiene B
                            if (!endA) {
                                conllA.appendId(conllB);
                                conllA.copyMisc(conllB);
                                printWriter.print(conllA.toString());
                            }
                            if (!Constants.NOVALUE.equals(conllA.getForma())) {
                                nextA = true;
                            } else {
                                printA = false;
                            }
                            if (!Constants.NOVALUE.equals(conllB.getForma())) {
                                nextB = true;
                            }
                            if (Constants.NOVALUE.equals(conllA.getForma())
                                    && Constants.NOVALUE.equals(conllB.getForma())) {
                                nextA = true;
                                nextB = true;
                            }
                            break;
                        case 2:
                            //B contiene A
                            if (!endB) {
                                if (!Constants.NOVALUE.equals(conllA.getForma())) {
                                    conllB.appendId(conllA);
                                }
                                conllB.copyMisc(conllA);
                                printWriter.print(conllB.toString());
                            }
                            if (!Constants.NOVALUE.equals(conllA.getForma())) {
                                nextA = true;
                            } else {
                                printA = false; //caso in cui conllA non va stampata perché già incorporata in almeno una B
                            }
                            if (!Constants.NOVALUE.equals(conllB.getForma())) {
                                nextB = true;
                            }
                            if (Constants.NOVALUE.equals(conllA.getForma())
                                    && Constants.NOVALUE.equals(conllB.getForma())) {
                                nextA = true;
                                nextB = true;
                            }
                            break;
                        case -1: //A precede B
                            if (!endA) {
                                if (printA) { //sono su un _ che ha trovato un corrispondente nell'altro file e quindi ho mergiato
                                    printWriter.print(conllA.toString());
                                } else {
                                    printA = true;
                                }
                            }
                            nextA = true;
                            break;
                        case -2: //B precede A
                            if (!endB) {
                                printWriter.print(conllB.toString());
                            }
                            nextB = true;
                            break;
                        case -3:
                            //caso tratti non confrontabili. li stampo entrambi e vado avanti
                            if (!endA) {
                                printWriter.print(conllA.toString());
                            }
                            nextA = true;
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
                        if (printA) { //sono su un _ che ha trovato un corrispondente nell'altro file e quindi ho mergiato
                            printWriter.print(conllA.toString());
                        } else {
                            printA = true;
                        }
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
