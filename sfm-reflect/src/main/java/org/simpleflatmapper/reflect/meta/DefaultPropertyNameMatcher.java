package org.simpleflatmapper.reflect.meta;


import java.util.ArrayList;
import java.util.List;

import static org.simpleflatmapper.util.Asserts.requireNonNull;

public final class DefaultPropertyNameMatcher implements PropertyNameMatcher {
	private final String column;
	private final int from;

	private final boolean exactMatch;
	private final boolean caseSensitive;
	private final int effectiveEndIndex;

	public DefaultPropertyNameMatcher(String column, int from, boolean exactMatch, boolean caseSensitive) {
		if (from > column.length()) {
			throw new IndexOutOfBoundsException("Index " + from + " out of " + column.length());
		}
		this.column = requireNonNull("property", column);
		this.from = from;
		this.exactMatch = exactMatch;
		this.caseSensitive = caseSensitive;
		this.effectiveEndIndex = lastNonIgnorableChar(column) + 1;
	}



	@Override
	public PropertyNameMatch matches(final CharSequence property) {
		if (property == null) return null;

		int endIndex = _partialMatch(property);
		if  (endIndex == effectiveEndIndex) {
			int meaningfulCharProperty = meaningfulChar(property, 0, property.length());
			int meaningfulCharColumn = meaningfulChar(column, from, endIndex);

			int score =  meaningfulCharProperty;
			int skippedLetters = meaningfulCharProperty - meaningfulCharColumn;
			return new PropertyNameMatch(property.toString(), column.substring(from, endIndex), null, score, skippedLetters);
		} else {
			return null;
		}
	}

	private int lastNonIgnorableChar(String column) {
		for(int i = column.length() - 1; i >= 0; i--) {
			if (!isSeparatorChar(column.charAt(i))) return i;
		}
		return 0;
	}

	@Override
	public IndexedColumn matchIndex() {
		int index = -1;

		int listIndexStart = from;
		
		// skip separtor char
		while(listIndexStart < column.length() && isSeparatorChar(column.charAt(listIndexStart))) {
			listIndexStart++;
		}
		
		
		boolean encounterSeparator = false;
		int effectivePropertyStart = listIndexStart;
		while(listIndexStart < column.length()) {
			char ch = column.charAt(listIndexStart);
			if (Character.isDigit(ch)) {
				break;
			} else if (isSeparatorChar(ch)) {
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

		return new IndexedColumn(index, column.substring(listIndexStart, listIndexEnd), subPropertyNameMatcher,
                listIndexStart - effectivePropertyStart, (listIndexStart != effectivePropertyStart || (listIndexEnd < column.length() && !isSeparatorChar(column.charAt(listIndexEnd)))) // has text at the start or at the ends
		);
	}

	private int _partialMatch(final CharSequence property) {
		if (property == null) return -1;
		int indexColumn = from;
		int indexProperty = 0;
		int nbCharIncommon = 0;
		do {

			// next property char
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
				if ((skipedIgnorablePropertyCharacter || Character.isUpperCase(charProperty)) && nbCharIncommon > 0) // end of column with prefix match
					return indexColumn;
				else return -1;
			}
			char charColumn = column.charAt(indexColumn ++);
			boolean skipedIgnorableColumnCharacter = false;
			while(ignoreCharacter(charColumn)) {
				if (indexColumn >= column.length()) { // run out of character
					return -1;
				}
				charColumn = column.charAt(indexColumn ++);
				skipedIgnorableColumnCharacter = true;
			}

			if (caseSensitive && skipedIgnorableColumnCharacter) {
				charColumn = Character.toUpperCase(charColumn);
			}

			if (areDifferentCharacters(charProperty, charColumn)) {

				if (skipedIgnorableColumnCharacter && (skipedIgnorablePropertyCharacter || Character.isUpperCase(charProperty)) && nbCharIncommon > 0)
					return  indexColumn - 2;
				else
					return -1;
			}

			nbCharIncommon++;

		}
		while(true);
	}

	private boolean areDifferentCharacters(char c1, char c2) {
		if (caseSensitive) {
			return c1 != c2;
		} else {
			return Character.toLowerCase(c1) != Character.toLowerCase(c2);
		}
	}

	private boolean ignoreCharacter(final char charColumn) {
		return !exactMatch && isSeparatorChar(charColumn);
	}

	public static  boolean isSeparatorChar(char charColumn) {
		return charColumn == '_' || charColumn == ' ' || charColumn == '.' || charColumn == '-';
	}

	@Override
	public PropertyNameMatch partialMatch(final CharSequence property) {
		int index = _partialMatch(property);
		if (index != -1) {
			int meaningfulCharProperty = meaningfulChar(property, 0, property.length());
			int meaningfulCharColumn = meaningfulChar(column, from, index);

			int score =  meaningfulCharProperty;
			int skippedLater = meaningfulCharProperty - meaningfulCharColumn;
			return new PropertyNameMatch(property.toString(), column.substring(from, index), new DefaultPropertyNameMatcher(column, index, exactMatch, caseSensitive), score, skippedLater);
		} else {
			return null;
		}
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
            return new PropertyNameMatch(column.substring(from, index), column, new DefaultPropertyNameMatcher(column, index, exactMatch, caseSensitive), index - from, 0);
        } else {
            return null;
        }
    }

	@Override
	public List<PropertyNameMatcherKeyValuePair> keyValuePairs() {
		List<PropertyNameMatcherKeyValuePair> keyValuePairs = new ArrayList<PropertyNameMatcherKeyValuePair>();

		int f = from;
		// skip separator char
		for(; f < column.length() && isSeparatorChar(column.charAt(f)); f++)
			;
		keyValuePairs.add(
				new PropertyNameMatcherKeyValuePair(
						new DefaultPropertyNameMatcher(column, f, exactMatch, caseSensitive),
						new DefaultPropertyNameMatcher("", 0, exactMatch, caseSensitive)
				));
		for(int i = column.length() - 1; i >= f; i--) {
			char c = column.charAt(i);
			if (isSeparatorChar(c)) {
				PropertyNameMatcher key = new DefaultPropertyNameMatcher(column.substring(f,  i), 0, exactMatch, caseSensitive);
				PropertyNameMatcher value = new DefaultPropertyNameMatcher(column,  i + 1, exactMatch, caseSensitive);
				keyValuePairs.add(new PropertyNameMatcherKeyValuePair(key, value));
			}
		}

		return keyValuePairs;
	}

	@Override
	public int asScore() {
		return toScore(column, from);
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
		return toScore(property, 0);
	}

	public static int toScore(String property, int from) {
		int s = 0;
		for(int i = from; i < property.length(); i++) {
			if (!isSeparatorChar(property.charAt(i))) s++;
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
