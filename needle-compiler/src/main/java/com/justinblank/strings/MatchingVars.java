package com.justinblank.strings;

import com.justinblank.classcompiler.Vars;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

// TODO: refactor to avoid having to specify indexes of vars we don't care about, to avoid busywork
class MatchingVars implements Vars {

    static final String STATE = DFAClassBuilder.STATE_FIELD;
    static final String INDEX = DFAClassBuilder.INDEX_FIELD;
    static final String STRING = DFAClassBuilder.STRING_FIELD;
    static final String CHAR = DFAClassBuilder.CHAR_FIELD;
    static final String LENGTH = DFAClassBuilder.LENGTH_FIELD;
    static final String LAST_MATCH = "lastMatch";
    static final String WAS_ACCEPTED = "wasAccepted";
    static final String SUFFIX_INDEX = "suffixIndex";
    static final String MAX_START = "maxStart";
    int lengthVar = -1;
    int stringVar = -1;
    int charVar = -1;
    int counterVar = -1;
    int stateVar = -1;
    int lastMatchVar = -1;
    int wasAcceptedVar = -1;
    int byteClassVar = -1;
    int suffixIndexVar = -1;
    int maxStartVar = -1;
    int maxVar = 0;

    MatchingVars(int charVar, int counterVar, int stateVar, int lengthVar, int stringVar) {
        this.charVar = charVar;
        this.counterVar = counterVar;
        this.stateVar = stateVar;
        this.lengthVar = lengthVar;
        this.stringVar = stringVar;
    }

    @Override
    public List<Pair<String, Integer>> allVars() {
        List<Pair<String, Integer>> variables = new ArrayList<>();
        if (charVar != -1) {
            variables.add(Pair.of(DFAClassBuilder.CHAR_FIELD, charVar));
        }
        if (counterVar != -1) {
            variables.add(Pair.of(DFAClassBuilder.INDEX_FIELD, counterVar));
        }
        if (stringVar != -1) {
            variables.add(Pair.of(DFAClassBuilder.STRING_FIELD, stringVar));
        }
        if (lengthVar != -1) {
            variables.add(Pair.of(DFAClassBuilder.LENGTH_FIELD, lengthVar));
        }
        if (stateVar != -1) {
            variables.add(Pair.of(DFAClassBuilder.STATE_FIELD, stateVar));
        }
        if (byteClassVar != -1) {
            variables.add(Pair.of(DFAClassBuilder.BYTE_CLASS_FIELD, byteClassVar));
        }
        if (suffixIndexVar != -1) {
            variables.add(Pair.of(SUFFIX_INDEX, suffixIndexVar));
        }
        if (maxStartVar != -1) {
            variables.add(Pair.of(MAX_START, maxStartVar));
        }
        return variables;
    }

    public MatchingVars setLengthVar(int lengthVar) {
        this.lengthVar = lengthVar;
        return this;
    }

    public MatchingVars setStringVar(int stringVar) {
        this.stringVar = stringVar;
        return this;
    }

    public MatchingVars setCharVar(int charVar) {
        this.charVar = charVar;
        return this;
    }

    public MatchingVars setCounterVar(int counterVar) {
        this.counterVar = counterVar;
        return this;
    }

    public MatchingVars setStateVar(int stateVar) {
        this.stateVar = stateVar;
        return this;
    }

    public MatchingVars setByteClassVar(int byteClassVar) {
        this.byteClassVar = byteClassVar;
        return this;
    }

    public MatchingVars setLastMatchVar(int lastMatchVar) {
        this.lastMatchVar = lastMatchVar;
        return this;
    }

    public MatchingVars setWasAcceptedVar(int wasAcceptedVar) {
        this.wasAcceptedVar = wasAcceptedVar;
        return this;
    }

    public MatchingVars setSuffixIndexVar(int suffixIndexVar) {
        this.suffixIndexVar = suffixIndexVar;
        return this;
    }

    public MatchingVars setMaxStartVar(int maxStartVar) {
        this.maxStartVar = maxStartVar;
        return this;
    }

    public int indexByName(String name) {
        switch (name) {
            case STATE:
                return this.stateVar;
            case INDEX:
                return this.counterVar;
            case STRING:
                return this.stringVar;
            case CHAR:
                return this.charVar;
            case LENGTH:
                return this.lengthVar;
            case LAST_MATCH:
                return this.lastMatchVar;
            case WAS_ACCEPTED:
                return this.wasAcceptedVar;
            case DFAClassBuilder.BYTE_CLASS_FIELD:
                return this.byteClassVar;
            case SUFFIX_INDEX:
                return this.suffixIndexVar;
            case MAX_START:
                return this.maxStartVar;
            default:
                throw new IllegalArgumentException("Illegal argument for variable lookup: " + name);
        }
    }

    @Override
    public String nameByIndex(int count) {
        if (count == this.stateVar) {
            return STATE;
        }
        else if (count == this.charVar) {
            return CHAR;
        }
        else if (count == this.stringVar) {
            return STRING;
        }
        else if (count == this.counterVar) {
            return INDEX;
        }
        else if (count == this.lastMatchVar) {
            return LAST_MATCH;
        }
        else if (count == this.wasAcceptedVar) {
            return WAS_ACCEPTED;
        }
        else if (count == this.byteClassVar) {
            return DFAClassBuilder.BYTE_CLASS_FIELD;
        }
        else if (count == this.suffixIndexVar) {
            return SUFFIX_INDEX;
        }
        else {
            return null;
        }
    }

}
