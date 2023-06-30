/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package it.cnr.ilc.compareandmergelexicons;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 *
 * @author Simone Marchi
 */
public class CompareAndMergeLexicons {

    public static void main(String[] args) throws FileNotFoundException, MalformedConllRowException, Exception {

//        if (args.length < 2) {
//
//            System.err.println("No arguments!");
//            System.exit(-1);
//        }

//        String fileA = "/home/simone/Nextcloud/PROGETTI/FormarioItalex/Lexico/zwing.conll";
//        String fileB = "/home/simone/Nextcloud/PROGETTI/magic_src/zwing.conll";
//        String fileC = "/home/simone/Nextcloud/PROGETTI/UD/zzz.conll";
        String fileA = "/home/simone/Nextcloud/PROGETTI/FormarioItalex/Lexico/out";
        String fileB = "/home/simone/Nextcloud/PROGETTI/magic_src/formario_MAGIC.sort.conll";
        String fileC = "/home/simone/Nextcloud/PROGETTI/UD/out";
//        String fileA = "/home/simone/Nextcloud/PROGETTI/FormarioItalex/Lexico/test.conll";
//        String fileB = "/home/simone/Nextcloud/PROGETTI/magic_src/test.conll";
//        String fileC = "/home/simone/Nextcloud/PROGETTI/UD/test.conll";

        BufferedReader firstFile
                = new BufferedReader(new FileReader(fileA));
        
        BufferedReader secondFile
                = new BufferedReader(new FileReader(fileB));

        BufferedReader thirdFile
                = new BufferedReader(new FileReader(fileC));

        ConllComparator cc = new ConllComparator();
        cc.setFirstFile(firstFile);
        cc.setSecondFile(secondFile);
        cc.setThirdFile(thirdFile);
        
        cc.compareLineByLine();

    }
}
