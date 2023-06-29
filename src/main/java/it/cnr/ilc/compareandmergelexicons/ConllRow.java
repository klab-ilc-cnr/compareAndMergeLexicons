package it.cnr.ilc.compareandmergelexicons;

import java.text.Collator;
import java.util.Arrays;
import java.util.Locale;
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

    private final Collator itCollator = Collator.getInstance(Locale.ITALIAN);

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
            itCollator.setStrength(Collator.SECONDARY);

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

        int equal;
        int compareForma = itCollator.compare(getForma(), entry.getForma());
        int compareLemma = itCollator.compare(getLemma(), entry.getLemma());
        int comparePos = itCollator.compare(getPos(), entry.getPos());
//        System.err.printf("forma: %s %s: %d\n",getForma(), entry.getForma(), compareForma);
//        System.err.printf("lemma: %s %s: %d\n",getLemma(), entry.getLemma(), compareLemma);
//        System.err.printf("pos: %s %s: %d\n",getPos(), entry.getPos(), comparePos);
        if (compareForma == 0) {
            if (compareLemma == 0) {
                if (comparePos == 0) {
                    equal = 0;
                } else {
                    equal = comparePos;
                }
            } else {
                equal = compareLemma;
            }
        } else {
            equal = compareForma;
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
        int ret;
        String firstTraits = String.join(",", this.getTraits());
        String secondTraits = String.join(",", entry.getTraits());
        if (firstTraits.equals(secondTraits)) {
            ret = 0;
        } else if (firstTraits.contains(secondTraits)) {
            ret = 1;
        } else if (secondTraits.contains(firstTraits)) {
            ret = 2;
        } else {
            ret = -1;
        }
        //System.err.printf("%s <=> %s : %d\n",firstTraits, secondTraits, ret);
        return ret;
    }

    @Override
    public String toString() {
        String traits = Arrays.stream(getTraits()).collect(Collectors.joining("|"));
        return String.format("%s\t%s\t%s\t%s\t_\t%s\t_\t_\t_\t%s\n",
                getId(), getForma(), getLemma(), getPos(), traits, getMisc());
    }

}
