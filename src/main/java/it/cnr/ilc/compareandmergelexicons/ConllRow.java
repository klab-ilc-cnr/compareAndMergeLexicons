package it.cnr.ilc.compareandmergelexicons;

import java.text.Collator;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.ToString;

/**
 *
 * @author Simone Marchi
 */
@ToString
class ConllRow {

    private String id;
    private String forma;
    private String lemma;
    private String pos;
    private HashMap<String, String> traits;
    private String misc;
    private Collator myCollator;

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

    public HashMap<String, String> getTraits() {
        return traits;
    }

    public Collator getMyCollator() {
        return myCollator;
    }

    private void setMyCollator(Collator myCollator) {
        this.myCollator = myCollator;
    }

    public String getTraitsAsString() {
        StringBuilder sb = new StringBuilder();
        if (traits.isEmpty()) {
            sb.append(Constants.NOVALUE);
        } else {
            for (Iterator<String> iterator = traits.keySet().iterator(); iterator.hasNext();) {
                String key = iterator.next();
                String value = traits.get(key);
                sb.append(key + Constants.EQUALS + value);
                if (iterator.hasNext()) {
                    sb.append("|");
                }
            }
        }
        return sb.toString();
    }

    private void setTraits(String traits) {
        this.traits = new LinkedHashMap<>(); //per mantenere l'ordine di inserimento
        if (traits.contains(Constants.EQUALS)) {
            String[] ana = traits.split("\\|"); //divido le classi dei tratti
            for (String s : ana) {
                String[] splitted = s.split(Constants.EQUALS); //divido chiave valore di un singolo tratto
                this.traits.put(splitted[0], splitted[1]);
            }
        }
    }

    public String getMisc() {
        return misc;
    }

    private void setMisc(String misc) {
        this.misc = misc;
    }

    public ConllRow(String conllRow, Collator collator) throws MalformedConllRowException, ParseException {

        String[] cols = conllRow.split("\t");

        if (cols.length == 10) {
            setId(cols[0]);
            setForma(cols[1]);
            if (cols[2].contains(" ")) {
                throw new MalformedConllRowException("Malformed Conll row: (" + conllRow + ")");
            }
            setLemma(cols[2]);
            setPos(cols[3]);
            setTraits(cols[5]);
            setMisc(cols[9]);
            setMyCollator(collator);

        } else {
            throw new MalformedConllRowException("Malformed Conll row: (" + conllRow + ")");
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

        int compareForma = 0;

        if (!getForma().equals(Constants.NOVALUE) || entry.getForma().equals(Constants.NOVALUE)) {
            compareForma = getMyCollator().compare(getForma(), entry.getForma());
        }

        int compareLemma = getMyCollator().compare(getLemma(), entry.getLemma());
        int comparePos = getMyCollator().compare(getPos(), entry.getPos());
//        System.err.printf("forma: %s %s: %d\n",getForma(), entry.getForma(), compareForma);
//        System.err.printf("lemma: %s %s: %d\n",getLemma(), entry.getLemma(), compareLemma);
//        System.err.printf("pos: %s %s: %d\n",getPos(), entry.getPos(), comparePos);
        if (compareLemma == 0) {
            if (comparePos == 0) {
                if (compareForma == 0) {
                    equal = 0;
                } else {
                    equal = (compareForma > 0 ? 1 : -1);
                }
            } else {
                equal = (comparePos > 0 ? 1 : -1);
            }
        } else {
            equal = (compareLemma > 0 ? 1 : -1);
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
        int ret = 0;

        String firstTraitsAsString = this.getTraitsAsString();
        String secondTraitsAsString = entry.getTraitsAsString();

        //VERB: lexinfo:person=lexinfo:secondPerson|lexinfo:number=lexinfo:singular|lexinfo:tense=lexinfo:past|lexinfo:mood=lexinfo:indicative
        if (this.getPos().equals(Constants.VERB)) {
            return this.compareVerbTraits(entry);
        } else {
            if (getTraits().equals(entry.getTraits())) {
                ret = 0;
            } else if (getTraits().entrySet().containsAll(entry.getTraits().entrySet())) {
                ret = 1;
            } else if (entry.getTraits().entrySet().containsAll(getTraits().entrySet())) {
                ret = 2;
            } else {
                //devo stabilire chi precede basandomi sui value dei tratti in comune
                for (Map.Entry<String, String> tr : entry.getTraits().entrySet()) {
                    String key = tr.getKey();
                    String value = tr.getValue();
                    if (this.getTraits().containsKey(key)) {
                        int compare = value.compareTo(this.getTraits().get(key));
                        if (compare > 0) { //this precede entry
                            return -1;
                        } else if (compare < 0) { //entry precede this
                            return -2;
                        }
                    }
                }
                // se arrivo qua i tratti sono inconfrontabili
                System.err.format("Inconfrontabili %s %s\n", this.getTraits(), entry.getTraits());
                return -3; //stampo e vado avanti su entrambi
                /*if (firstTraitsAsString.compareTo(secondTraitsAsString) < 0) {
                    ret = -1;
                } else {
                    ret = -2;
                }*/
            }
        }

        return ret;
    }

    private int compareVerbTraits(ConllRow entry) {
        int ret = 0;

        try {
            int mood = 0;
            int number = 0;
            int person = 0;
            int tense = 0;

            if (getTraits().equals(entry.getTraits())) {
                ret = 0;
            } else if (getTraits().entrySet().containsAll(entry.getTraits().entrySet())) {
                ret = 1;
            } else if (entry.getTraits().entrySet().containsAll(getTraits().entrySet())) {
                ret = 2;
            } else {

                if (getTraits().containsKey(Constants.MOOD) && entry.getTraits().containsKey(Constants.MOOD)) {
                    mood = getTraits().get(Constants.MOOD).compareTo(entry.getTraits().get(Constants.MOOD));
                }
                if (getTraits().containsKey(Constants.NUMBER) && entry.getTraits().containsKey(Constants.NUMBER)) {
                    number = getTraits().get(Constants.NUMBER).compareTo(entry.getTraits().get(Constants.NUMBER));
                }
                if (getTraits().containsKey(Constants.PERSON) && entry.getTraits().containsKey(Constants.PERSON)) {
                    person = getTraits().get(Constants.PERSON).compareTo(entry.getTraits().get(Constants.PERSON));
                }
                if (getTraits().containsKey(Constants.TENSE) && entry.getTraits().containsKey(Constants.TENSE)) {
                    tense = getTraits().get(Constants.TENSE).compareTo(entry.getTraits().get(Constants.TENSE));
                }
                if (mood > 0) {
                    ret = -2;
                } else if (mood < 0) {
                    ret = -1;
                } else if (number > 0) {
                    ret = -2;
                } else if (number < 0) {
                    ret = -1;
                } else if (person > 0) {
                    ret = -2;
                } else if (person < 0) {
                    ret = -1;
                } else if (tense > 0) {
                    ret = -2;
                } else if (tense < 0) {
                    ret = -1;
                } else {
                    ret = 0;
                }
            }
        } catch (NullPointerException e) {
            System.err.println("entry: " + entry.toString());
            e.printStackTrace();
        }

        return ret;
    }

    public void appendId(ConllRow entry) {
        setId(getId().concat(",").concat(entry.getId()));
    }

    public void copyMisc(ConllRow entry) {

        if (!this.misc.equals(Constants.NOVALUE) && !entry.getMisc().equals(Constants.NOVALUE) && !this.misc.equals(entry.getMisc())) {
            System.err.format("MISC diversi {} <>{}", this.misc, entry.getMisc());
        }
        if (!this.misc.equals(Constants.NOVALUE) && !entry.getMisc().equals(Constants.NOVALUE)) {
            System.err.format("Problem copying MISC value {} <=> {}", this.misc, entry.getMisc());
        }
        if (this.misc.equals(Constants.NOVALUE) && !entry.getMisc().equals(Constants.NOVALUE)) {
            this.setMisc(entry.getMisc());
        }
    }

    @Override
    public String toString() {
        return String.format("%s\t%s\t%s\t%s\t_\t%s\t_\t_\t_\t%s\n",
                getId(), getForma(), getLemma(), getPos(), getTraitsAsString(), getMisc());
    }

}
