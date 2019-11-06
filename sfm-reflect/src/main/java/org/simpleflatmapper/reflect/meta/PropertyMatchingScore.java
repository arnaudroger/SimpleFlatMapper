package org.simpleflatmapper.reflect.meta;

import java.util.Arrays;

public class PropertyMatchingScore implements Comparable<PropertyMatchingScore> {

    private NodeScore[] scores;

    private static final int NON_MAPPED = 1;
    private static final int SPECULATIVE = 2;
    private static final int PARTIAL = 4;
    private static final int SELF = 8;
    private static final int TUPLE = 16;
    private static final int ARRAY = 32;

    public PropertyMatchingScore() {
        scores = new NodeScore[0];
    }

    public PropertyMatchingScore(NodeScore[] scores) {
        this.scores = scores;
    }


    @Override
    public int compareTo(PropertyMatchingScore o) {
        int totalMatch = totalMatch();
        int oTotalMatch = o.totalMatch();

        if (totalMatch > oTotalMatch) return -1;
        if (oTotalMatch > totalMatch) return 1;

        if (isPartialMatch()) { // any partial match disqualify
            if (!o.isPartialMatch()) {
                return 1;
            }
        } else if (o.isPartialMatch()){
            return -1;
        }

        int effectiveMatch = effectiveTotalMatch();
        int oEffectiveMatch = o.effectiveTotalMatch();

        if (effectiveMatch > oEffectiveMatch) return -1;
        if (oEffectiveMatch > effectiveMatch) return 1;

        // least lateral move first
//        int firstSpeculativeNode = firstSpeculativeNode();
//        int oFirstSpeculativeNode = o.firstSpeculativeNode();
//        if (firstSpeculativeNode > oFirstSpeculativeNode) return -1;
//        if (oFirstSpeculativeNode > firstSpeculativeNode) return 1;

        int maxDepth = Math.min(o.depth(), depth());
        for(int i = 0; i < maxDepth; i++) {

            boolean speculative = scores[i].isSpeculative();
            boolean ospeculative = o.scores[i].isSpeculative();

            if (speculative) {
                if (!ospeculative) {
                    return 1;
                }

            } else if (ospeculative) {
                return -1;
            }
        }

        for(int i = 0; i < maxDepth; i++) {
            int c = scores[i].compareTo(o.scores[i]);
            if (c != 0) return c;
        }



        return depth() - o.depth();
    }

    private int firstSpeculativeNode() {
        for(int i = 0; i < scores.length; i++) {
            if (scores[i].isSpeculative()) return i;
        }
        return Integer.MAX_VALUE;
    }

    private boolean isPartialMatch() {
        for(NodeScore n : scores) {
            if (n.isPartialMatch()) return true;
        }
        return false;
    }

    private int depth() {
        return scores.length;
    }

    private int totalMatch() {
        int tm = 0;
        for(NodeScore ns : scores) {
            tm += ns.nbMatch;
        }
        return tm;
    }

    private int effectiveTotalMatch() {
        int tm = 0;
        for(NodeScore ns : scores) {
            tm += ns.effectiveMatch();
        }
        return tm;
    }



    public PropertyMatchingScore arrayIndex(IndexedColumn ic, boolean scoreFullName) {
        if (scoreFullName) {
            return with(NodeScore.tupleIndex(null, null, ic));
        } else {
            return with(NodeScore.arrayIndex(ic, true, isSpeculative()));
        }
    }


    public PropertyMatchingScore speculative() {
        return with(NodeScore.speculative(null, null));
    }
    public PropertyMatchingScore speculative(PropertyMeta<?, ?> propertyMeta, PropertyNameMatcher propertyNameMatcher) {
        return with(NodeScore.speculative(propertyMeta, propertyNameMatcher));
    }
    public PropertyMatchingScore tupleIndex(PropertyMeta<?, ?> propertyMeta, PropertyNameMatcher propertyNameMatcher,IndexedColumn ic) {
        return with(NodeScore.tupleIndex(propertyMeta, propertyNameMatcher, ic));
    }

    public PropertyMatchingScore matches(PropertyMeta<?, ?> propertyMeta, PropertyNameMatcher propertyNameMatcher, PropertyNameMatch propertyNameMatch) {
        return with(NodeScore.matches(propertyMeta, propertyNameMatcher, propertyNameMatch));
    }



    public PropertyMatchingScore self(PropertyMeta<?, ?> propertyMeta, PropertyNameMatcher propertyNameMatcher, String propName) {
        int score = DefaultPropertyNameMatcher.toScore(propName);
      //  System.out.println("score = " + score);
        boolean scoreFullName = scoreFullName();
        return with(NodeScore.self(propertyMeta, propertyNameMatcher, propName, scoreFullName));
    }


    // does not take full name if has been speculative all the way through
    private boolean scoreFullName(boolean scoreFullName) {
//        return scoreFullName;
        if (scoreFullName) {
            if (scores.length ==0) return true;
            for(NodeScore ns : scores) {
                if (ns.nbMatch > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean scoreFullName() {
        if (scores.length > 0) {
            NodeScore lastScore = scores[scores.length - 1];
            if (lastScore.propertyMeta != null) {
                return (lastScore.state & TUPLE) != 0;
            }
        }
        return false;
    }

    private boolean isSpeculative() {
        if (scores.length > 0) {
            NodeScore lastScore = scores[scores.length - 1];
            if (lastScore.propertyMeta != null) {
                return (lastScore.state & SPECULATIVE) != 0;
            }
        }
        return false;
    }

    public PropertyMatchingScore nonMappedProperty(NonMappedPropertyMeta<?, ?> nonMappedPropertyMeta, PropertyNameMatcher propertyNameMatcher) {
        return with(NodeScore.nonMapped(nonMappedPropertyMeta, propertyNameMatcher));
    }

    private PropertyMatchingScore with(NodeScore score) {
        NodeScore[] ns = Arrays.copyOf(this.scores, this.scores.length + 1);
        ns[ns.length - 1] = score;
        return new PropertyMatchingScore(ns);
    }


    @Override
    public String toString() {
        return "PropertyMatchingScore{" +
                "totalScore=" + totalMatch() +
                ", effectiveMatch=" + effectiveTotalMatch() +
                ", firstSpeculativeNode=" + firstSpeculativeNode() +
                ", scores=" + Arrays.toString(scores) +
                '}';
    }

    @Deprecated
    public static PropertyMatchingScore newInstance(boolean selfScoreFullName) {
        return newInstance();
    }

    public static PropertyMatchingScore newInstance() {
        return new PropertyMatchingScore();
    }
    

    private static class NodeScore  implements  Comparable<NodeScore> {
        private final PropertyMeta<?, ?> propertyMeta;
        private final PropertyNameMatcher propertyNameMatcher;

        private final int state;

        private final int selfNumberOfProperties;
        private final int nbMatch;
        private final int nbPartialMatch;
        private final int index;

        private NodeScore(PropertyMeta<?, ?> propertyMeta, PropertyNameMatcher propertyNameMatcher, int state, int selfNumberOfProperties, int nbMatch, int nbPartialMatch, int index) {
            this.propertyMeta = propertyMeta;
            this.propertyNameMatcher = propertyNameMatcher;
            this.state = state;
            this.selfNumberOfProperties = selfNumberOfProperties;
            this.nbMatch = nbMatch;
            this.nbPartialMatch = nbPartialMatch;
            this.index = index;
        }

        public static NodeScore nonMapped(NonMappedPropertyMeta<?, ?> nonMappedPropertyMeta, PropertyNameMatcher propertyNameMatcher) {
            return new NodeScore(nonMappedPropertyMeta, propertyNameMatcher, NON_MAPPED, 0, 0, 0, 0);
        }

        public static NodeScore self(PropertyMeta<?, ?> propertyMeta, PropertyNameMatcher propertyNameMatcher, String propName, boolean scoreFullName) {
            int numberOfProperties = propertyMeta.getPropertyClassMeta().getNumberOfProperties();
            int score = (numberOfProperties == 0 && scoreFullName) ? DefaultPropertyNameMatcher.toScore(propName) : 0;
            return new NodeScore(propertyMeta, propertyNameMatcher, SELF, numberOfProperties, score, 0, 0);
        }

        public static NodeScore matches(PropertyMeta<?, ?> propertyMeta, PropertyNameMatcher propertyNameMatcher, PropertyNameMatch propertyNameMatch) {
            return new NodeScore(propertyMeta, propertyNameMatcher, propertyNameMatch.skippedLetters != 0 ? PARTIAL : 0, 0, propertyNameMatch.score, propertyNameMatch.skippedLetters, 0);
        }

        public static NodeScore arrayIndex(IndexedColumn ic, boolean scoreFullName, boolean speculative) {
            int score = speculative ? 0 : (scoreFullName ? ic.getScore() : 1);
            return new NodeScore(null, null, ARRAY, 0, score, 0, ic.getIndexValue());
        }


        public static NodeScore tupleIndex(PropertyMeta<?, ?> propertyMeta, PropertyNameMatcher propertyNameMatcher, IndexedColumn ic) {
            return new NodeScore(propertyMeta, propertyNameMatcher, TUPLE, 0, ic.getScore(), 0, ic.getIndexValue());
        }

        public static NodeScore speculative(PropertyMeta<?, ?> propertyMeta, PropertyNameMatcher propertyNameMatcher) {
            return new NodeScore(propertyMeta, propertyNameMatcher, SPECULATIVE, 0, 0, 0, 0);
        }


        @Override
        public String toString() {
            return "NodeScore{" +
                    " state=" + state +
                    ", selfNumberOfProperties=" + selfNumberOfProperties +
                    ", nbMatch=" + nbMatch +
                    ", nbPartialMatch=" + nbPartialMatch +
                    ", index=" + index +
                    ", propertyMeta=" + propertyMeta +
                    ", propertyNameMatcher=" + propertyNameMatcher +
                    '}';
        }

        @Override
        public int compareTo(NodeScore o) {
            if (isSpeculative()) {
                if (!o.isSpeculative()) {
                    return 1;
                }
            } else if (o.isSpeculative()) {
                return -1;
            }

            // horizontal
            if (index < o.index) {
                return -1;
            } else if (index > o.index) {
                return 1;
            }


            return 0;
        }

        private boolean isSpeculative() {
            return (state & SPECULATIVE) != 0;
        }

        public boolean isPartialMatch() {
            return (state & PARTIAL) != 0;
        }


        public int effectiveMatch() {
            if ((state & (ARRAY |SELF )) != 0) return 0;
            return nbMatch;
        }
    }

}
