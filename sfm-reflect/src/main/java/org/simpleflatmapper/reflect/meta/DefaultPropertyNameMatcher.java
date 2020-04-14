package org.simpleflatmapper.reflect.meta;


import org.simpleflatmapper.util.CharPredicate;

import java.util.ArrayList;
import java.util.List;

import static org.simpleflatmapper.util.Asserts.requireNonNull;

public final class DefaultPropertyNameMatcher implements PropertyNameMatcher {
	public static final CharPredicate DEFAULT_IS_SEPARATOR_CHAR = new CharPredicate() {
		@Override
		public boolean apply(char c) {
			return _isSeparatorChar(c);
		}
	};

	private final String column;
	private final int from;

	private final boolean exactMatch;
	private final boolean caseSensitive;
	private final int effectiveEndIndex;

	private final CharPredicate isSeparatorChar;

	public DefaultPropertyNameMatcher(String column, int from, boolean exactMatch, boolean caseSensitive) {
		this(column, from, exactMatch, caseSensitive, DEFAULT_IS_SEPARATOR_CHAR);
	}

	public DefaultPropertyNameMatcher(String column, int from, boolean exactMatch, boolean caseSensitive, CharPredicate isSeparatorChar) {
		if (from > column.length()) {
			throw new IndexOutOfBoundsException("Index " + from + " out of " + column.length());
		}
		this.column = requireNonNull("property", column);
		this.from = from;
		this.exactMatch = exactMatch;
		this.caseSensitive = caseSensitive;
		this.isSeparatorChar = isSeparatorChar;
		this.effectiveEndIndex = lastNonIgnorableChar(column) + 1;
	}

	@Override
	public PropertyNameMatch matches(final CharSequence property) {
		return matches(property, false);
	}

	@Override
	public PropertyNameMatch matches(final CharSequence property, boolean tryPlural) {
		if (property == null) return null;

		int endIndex = _partialMatch(property, tryPlural);
		if  (endIndex == effectiveEndIndex) {
			int meaningfulCharProperty = meaningfulChar(property, 0, property.length());
			int meaningfulCharColumn = meaningfulChar(column, from, endIndex);

			int score =  meaningfulCharColumn;
			int skippedLetters = meaningfulCharProperty - meaningfulCharColumn;
			return new PropertyNameMatch(property.toString(), column.substring(from, endIndex), null, score, skippedLetters);
		} else {
			return null;
		}
	}

	@Override
	public PropertyNameMatch partialMatch(final CharSequence property) {
		return partialMatch(property, false);
	}

	@Override
	public PropertyNameMatch partialMatch(final CharSequence property, boolean tryPlural) {
		int index = _partialMatch(property, tryPlural);
		if (index != -1) {
			int meaningfulCharProperty = meaningfulChar(property, 0, property.length());
			int meaningfulCharColumn = meaningfulChar(column, from, index);

			int score =  meaningfulCharColumn;
			int skippedLater = meaningfulCharProperty - meaningfulCharColumn;
			return new PropertyNameMatch(property.toString(), column.substring(from, index), new DefaultPropertyNameMatcher(column, index, exactMatch, caseSensitive), score, skippedLater);
		} else {
			return null;
		}
	}

	private int lastNonIgnorableChar(String column) {
		for(int i = column.length() - 1; i >= 0; i--) {
			if (!isSeparatorChar.apply(column.charAt(i))) return i;
		}
		return 0;
	}

	@Override
	public IndexedColumn matchIndex() {
		int index = -1;

		int listIndexStart = from;
		
		// skip separtor char
		while(listIndexStart < column.length() && isSeparatorChar.apply(column.charAt(listIndexStart))) {
			listIndexStart++;
		}
		
		
		boolean encounterSeparator = false;
		int effectivePropertyStart = listIndexStart;
		while(listIndexStart < column.length()) {
			char ch = column.charAt(listIndexStart);
			if (Character.isDigit(ch)) {
				break;
			} else if (isSeparatorChar.apply(ch)) {
				encounterSeparator = true;
			} else if (encounterSeparator) {
				return null;
			}
			listIndexStart++;
		}

		int listIndexEnd = listIndexStart;
		while(listIndexEnd < column.length() &&  Character.isDigit(column.charAt(listIndexEnd))) {
			listIndexEnd++;
		}
		if (listIndexStart != listIndexEnd) {
			index = Integer.parseInt(column.substring(listIndexStart, listIndexEnd));
		}

		if (index == -1) {
			return null;
		}

		PropertyNameMatcher subPropertyNameMatcher = null;


		if (listIndexEnd < column.length()) {
			subPropertyNameMatcher = new DefaultPropertyNameMatcher(column, listIndexEnd, exactMatch, caseSensitive );
		}

		int score = meaningfulChar(column, effectivePropertyStart, listIndexEnd);

		return new IndexedColumn(
				index,
				column.substring(listIndexStart, listIndexEnd),
				subPropertyNameMatcher,
				score,
				(listIndexStart != effectivePropertyStart || (listIndexEnd < column.length() && !isSeparatorChar.apply(column.charAt(listIndexEnd)))) // has text at the start or at the ends
		);
	}

	private int _partialMatch(final CharSequence property, boolean tryPlural) {
		if (property == null) return -1;
		int indexColumn = from;
		int indexProperty = 0;
		int nbCharIncommon = 0;
		do {

			/*
				-- First skip non meaningful chars in property name
			 */
			if (indexProperty >= property.length()) {
				return indexColumn; // not more to match
			}
			char charProperty = property.charAt(indexProperty++);
			boolean skipedIgnorablePropertyCharacter = false;
			while(ignoreCharacter(charProperty)) {
				if (indexProperty >= property.length()) {
					return indexColumn; // not more to match
				}
				charProperty = property.charAt(indexProperty++);
				skipedIgnorablePropertyCharacter = true;
			}

			// next column char
			if (indexColumn >= column.length()) { // run out of character
				if (nbCharIncommon > 0){
					if (endOfPropertyWord(charProperty, skipedIgnorablePropertyCharacter)) {
						return indexColumn;
					}
					return isPluralEnd(property, indexProperty -1, column, from, indexColumn, tryPlural); // end of column with prefix match
				}
				return -1;
			}

			/*
				-- Second skip non meaningful chars in column name
			 */
			char charColumn = column.charAt(indexColumn ++);
			boolean skipedIgnorableColumnCharacter = false;
			while(ignoreCharacter(charColumn)) {
				if (indexColumn >= column.length()) { // run out of character
					return -1;
				}
				charColumn = column.charAt(indexColumn ++);
				skipedIgnorableColumnCharacter = true;
			}

			/*
			    if case sensitive makes first char of column char is upper case ?
			 */
			if (caseSensitive && skipedIgnorableColumnCharacter) {
				charColumn = Character.toUpperCase(charColumn);
			}

			if (areDifferentCharacters(charProperty, charColumn)) {

				if (nbCharIncommon > 0){
					if (skipedIgnorableColumnCharacter && endOfPropertyWord(charProperty, skipedIgnorablePropertyCharacter)) {
						return indexColumn - 2;
					}
					return isPluralEnd(property, indexProperty - 1, column, from, indexColumn - 2, tryPlural); // end of column with prefix match
				}
				return -1;
			}

			nbCharIncommon++;

		}
		while(true);
	}

	private boolean endOfPropertyWord(char charProperty, boolean skipedIgnorablePropertyCharacter) {
		return (skipedIgnorablePropertyCharacter || Character.isUpperCase(charProperty));
	}

	private int isPluralEnd(CharSequence property,  int indexProperty, CharSequence column, int fromColumn, int indexColumn,  boolean tryPlural) {
		if (!tryPlural) return -1;
		return  isPlural(property, indexProperty, column, fromColumn, indexColumn);
	}


	//https://www.grammarly.com/blog/plural-nouns/
	private int isPlural(CharSequence property,  int indexProperty, CharSequence column, int fromColumn, int indexColumn) {

		// either mismatch or end of column
		if (indexProperty < property.length()) {
			char lastChar = property.charAt(indexProperty);
			if (areEqualsCI(lastChar, 's')) {
				if (isEndOfWord(property, indexProperty + 1)) {
					return indexColumn;
				};
			}

			if (indexProperty + 1 < property.length()) {
				//es
				//s, -ss, -sh, -ch, -x, or -z
				// analysis -> analyses
				// tomato tomatoes
				if (areEqualsCI(lastChar, 'e') && areEqualsCI(property.charAt(indexProperty + 1), 's')) {
					if (isEndOfWord(property, indexProperty + 2)) {
						// if column endup with is add 2
						if (indexColumn + 2 < column.length()) {
							if (areEqualsCI(column.charAt(indexColumn + 1) , 'i') && areEqualsCI(column.charAt(indexColumn + 2) , 's')) return indexColumn + 3;
						}
						if (endWiths(column, from, indexColumn,new char[] { 's'}, new char[]{'s', 's'}, new char[]{ 's', 'h' }, new char[]{ 'c', 'h' },new char[] {'x' },new char[] {'z' }, new char[] {'o'} ))
						{
							return indexColumn;
						}
					}
				}

				if (indexProperty + 2 < property.length()) {
					// city cities
					if (areEqualsCI(lastChar , 'i') && areEqualsCI(property.charAt(indexProperty + 1), 'e') && areEqualsCI(property.charAt(indexProperty + 2), 's')) {

						if (isEndOfWord(property, indexProperty + 3)) {
							// if column endup with is add 2
							if (indexColumn +1  < column.length() && areEqualsCI(column.charAt(indexColumn + 1) , 'y')) {
								 return indexColumn + 2;
							}
						}
					}

					// fez fezzes
					if (areEqualsCI(lastChar , 'z') && areEqualsCI(property.charAt(indexProperty + 1) , 'e') && areEqualsCI(property.charAt(indexProperty + 2), 's')) {

						//s, -ss, -sh, -ch, -x, or -z
						if (isEndOfWord(property, indexProperty + 3)) {
							// if column endup with is add 2
							if (indexColumn  -1 < column.length() && areEqualsCI(column.charAt(indexColumn -1) , 'z')) {
								return indexColumn;
							}
						}
					}

					// gas gasses
					if (areEqualsCI(lastChar , 's') && areEqualsCI(property.charAt(indexProperty + 1), 'e') && areEqualsCI(property.charAt(indexProperty + 2), 's')) {
						if (isEndOfWord(property, indexProperty + 3)) {
							// if column endup with is add 2
							if (indexColumn  -1 < column.length() && areEqualsCI(column.charAt(indexColumn -1), 's')) {
								return indexColumn;
							}
						}
					}


					// wolf wolves
					if (areEqualsCI(lastChar, 'v') && areEqualsCI(property.charAt(indexProperty + 1), 'e') && areEqualsCI(property.charAt(indexProperty + 2), 's')) {
						if (isEndOfWord(property, indexProperty + 3)) {
							// if column endup with is add 2
							if (indexColumn +1  < column.length() && areEqualsCI(column.charAt(indexColumn + 1), 'f')) {
								return indexColumn + 2;
							}
						}
					}


				}


			}
		}
		return -1;
	}

	private boolean areEqualsCI(char c1, char c2) {
		return c1 == c2 || Character.toUpperCase(c1) == Character.toUpperCase(c2) || Character.toLowerCase(c1) == Character.toLowerCase(c2);
	}

	private boolean endWiths(CharSequence column, int from, int indexColumn, char[]... chars) {
		nextChars:
		for(int i = 0; i < chars.length; i++) {
			char[] cs = chars[i];
			int offset = indexColumn - cs.length;
			if (offset > from) {
				for (int j = 0; j < cs.length; j++) {
					if (!areEqualsCI(cs[j], column.charAt(offset + j))) continue nextChars;
				}
				return true;
			}
		}
		return false;
	}

	private boolean isEndOfWord(CharSequence property, int nextIndexProperty) {
		return nextIndexProperty >= property.length() || Character.isUpperCase(property.charAt(nextIndexProperty)) || isSeparatorChar.apply(property.charAt(nextIndexProperty));
	}

	private boolean areDifferentCharacters(char c1, char c2) {
		if (caseSensitive) {
			return c1 != c2;
		} else {
			return Character.toLowerCase(c1) != Character.toLowerCase(c2);
		}
	}

	private boolean ignoreCharacter(final char charColumn) {
		return !exactMatch && isSeparatorChar.apply(charColumn);
	}

	public static  boolean _isSeparatorChar(char charColumn) {
		return charColumn == '_' || charColumn == ' ' || charColumn == '.' || charColumn == '-';
	}



	private int meaningfulChar(CharSequence cs, int from, int to) {
		int s= 0;
		for(int i = from; i < to; i++) {
			if (!ignoreCharacter(cs.charAt(i))) s++;
		}
		return s;
	}

	@Override
    public PropertyNameMatch speculativeMatch() {

        int index = _speculativeMatch();

        if (index != -1) {
			int score = meaningfulChar(column, from, index);
			return new PropertyNameMatch(column.substring(from, index), column, new DefaultPropertyNameMatcher(column, index, exactMatch, caseSensitive), score, 0);
        } else {
            return null;
        }
    }

	@Override
	public List<PropertyNameMatcherKeyValuePair> keyValuePairs() {
		List<PropertyNameMatcherKeyValuePair> keyValuePairs = new ArrayList<PropertyNameMatcherKeyValuePair>();

		int f = from;
		// skip separator char
		for(; f < column.length() && isSeparatorChar.apply(column.charAt(f)); f++)
			;
		keyValuePairs.add(
				new PropertyNameMatcherKeyValuePair(
						new DefaultPropertyNameMatcher(column, f, exactMatch, caseSensitive),
						new DefaultPropertyNameMatcher("", 0, exactMatch, caseSensitive)
				));
		for(int i = column.length() - 1; i >= f; i--) {
			char c = column.charAt(i);
			if (isSeparatorChar.apply(c)) {
				PropertyNameMatcher key = new DefaultPropertyNameMatcher(column.substring(f,  i), 0, exactMatch, caseSensitive);
				PropertyNameMatcher value = new DefaultPropertyNameMatcher(column,  i + 1, exactMatch, caseSensitive);
				keyValuePairs.add(new PropertyNameMatcherKeyValuePair(key, value));
			}
		}

		return keyValuePairs;
	}

	@Override
	public int asScore() {
		return toScore(column, from, isSeparatorChar);
	}

	private int _speculativeMatch() {
        for(int i = from; i < column.length(); i++) {
            char c = column.charAt(i);
            if (c == '_' || c == '.' || c == ' ' || c == '-') {
                return i;
            }
        }
        return -1;
    }

	@Override
	public String toString() {
		return column.substring(from, column.length());
	}

	public static int toScore(String property) {
		return toScore(property, DEFAULT_IS_SEPARATOR_CHAR);
	}

	public static int toScore(String property, int from) {
		return toScore(property, from, DEFAULT_IS_SEPARATOR_CHAR);
	}

	public static int toScore(String property, CharPredicate isSeparatorChar) {
		return toScore(property, 0, isSeparatorChar);
	}

	public static int toScore(String property, int from, CharPredicate isSeparatorChar) {
		int s = 0;
		for(int i = from; i < property.length(); i++) {
			if (!isSeparatorChar.apply(property.charAt(i))) s++;
		}
		return s;
	}

	public static PropertyNameMatcher of(String value) {
		return new DefaultPropertyNameMatcher(value, 0, false, false);
	}

	public static PropertyNameMatcher exact(String value) {
		return new DefaultPropertyNameMatcher(value, 0, true, true);
	}
}
