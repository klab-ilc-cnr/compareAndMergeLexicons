/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.cnr.ilc.compareandmergelexicons;

/**
 *
 * @author Simone Marchi
 */
public class MalformedConllRowException extends Exception {
    public MalformedConllRowException(String errorMessage) {
        super(errorMessage);
    }
}
