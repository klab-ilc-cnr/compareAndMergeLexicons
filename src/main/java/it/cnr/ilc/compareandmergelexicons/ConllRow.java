package it.cnr.ilc.compareandmergelexicons;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 *
 * @author Simone Marchi
 */
class ConllRow {

    private String id;
    private String forma;
    private String lemma;
    private String pos;
    private String[] traits;
    private String misc;

    public String getId() {
        return id;
    }

    private void setId(String id) {
        this.id = id;
    }

    public String getForma() {
        return forma;
    }

    private void setForma(String forma) {
        this.forma = forma;
    }

    public String getLemma() {
        return lemma;
    }

    private void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public String getPos() {
        return pos;
    }

    private void setPos(String pos) {
        this.pos = pos;
    }

    public String[] getTraits() {
        return traits;
    }

    private void setTraits(String traits) {
        String[] ana = traits.split("\\|");
        Arrays.sort(ana);
        this.traits = ana;
    }

    public String getMisc() {
        return misc;
    }

    private void setMisc(String misc) {
        this.misc = misc;
    }

    public ConllRow(String conllRow) throws MalformedConllRowException {

        String[] cols = conllRow.split("\t");

        if (cols.length == 10) {
            setId(cols[0]);
            setForma(cols[1]);
            setLemma(cols[2]);
            setPos(cols[3]);
            setTraits(cols[5]);
            setMisc(cols[9]);
        } else {
            throw new MalformedConllRowException("Malformed Conll row " + conllRow);
        }
    }

    /**
     * Compares this ConllRow row with another (ConllRow entry) comparing forma,
     * lemma and pos only.
     *
     * @param entry ConllRow row obj
     * @return 0 iff this forma, lemma and pos are identical to the argument. a
     * value less than 0 if this forma, lemma or pos are lexicographically less
     * than the argument; and a value greater than 0 if this forma, lemma or pos
     * are lexicographically greater than the string argument.
     */
    public int compareEntry(ConllRow entry) {
        int equal = 0;
        if (this.forma.equals(entry.getForma())) {
            if (this.lemma.equals(entry.getLemma())) {
                if (this.pos.equals(entry.getPos())) {
                    equal = 0;
                } else {
                    equal = getPos().compareTo(entry.getPos());
                }
            } else {
                equal = getLemma().compareTo(entry.getLemma());
            }
        } else {
            equal = getForma().compareTo(entry.getForma());
        }
        return equal;
    }

    /**
     * Compares this ConllRow traits with the entry traits.
     *
     *
     * @param entry ConllRow row obj
     * @return 0 iff traits are identical 1 iff traits from this entry contain
     * traits from the passed one 2 iff traits from passed entry contain traits
     * from this entry -1 otherwise (not equal and not contained)
     */
    public int compareTraits(ConllRow entry) {
        String firstTraits = Arrays.toString(this.getTraits());
        String secondTraits = Arrays.toString(entry.getTraits());

        if (firstTraits.equals(secondTraits)) {
            return 0;
        } else if (firstTraits.contains(secondTraits)) {
            return 1;
        } else if (secondTraits.contains(firstTraits)) {
            return 2;
        } else {
            return -1;
        }
    }

    @Override
    public String toString() {
        String traits = Arrays.stream(getTraits()).collect(Collectors.joining("|"));
        return String.format("%s\t%s\t%s\t%s\t_\t%s\t_\t_\t_\t%s\n", 
                getId(), getForma(), getLemma(),getPos(), traits, getMisc());
    }

}
